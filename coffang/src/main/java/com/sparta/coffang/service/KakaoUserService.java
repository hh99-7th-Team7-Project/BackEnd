package com.sparta.coffang.service;
/**
 * 프론트와 카카오 로그인 연결 된 후에 sout 출력문 다 삭제
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coffang.dto.responseDto.SocialUserInfoDto;
import com.sparta.coffang.model.User;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void kakaoLogin(String code,HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        System.out.println("카카오 로그인 1번 접근");
        String accessToken = getAccessToken(code);
        System.out.println("인가 코드 : " + code);
        System.out.println("엑세스 토큰: " + accessToken);

        // 2. 토큰으로 카카오 API 호출
        System.out.println("카카오 로그인 2번 접근");
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        System.out.println("카카오 로그인 3번 접근");
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
        System.out.println("카카오 로그인 4번 접근");
        jwtTokenCreate(kakaoUser,response);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "ad2b3f54d2cfc0abc76ddd8b27cddd27"); //본인의 REST API키
//        body.add("redirect_uri", "http://localhost:3000/oauth/kakao/callback"); //성공 후 리다이렉트 되는 곳
        body.add("redirect_uri", "http://localhost:8080/oauth/kakao/callback"); //테스트는 내꺼에서 해보자
//        body.add("redirect_uri", "http://3.36.78.102:8080/oauth/kakao/callback"); //테스트는 내꺼에서 해보자
        body.add("code", code);

        /**
         * 카카오 로그인 잘 되는지 확인하기
         * https://kauth.kakao.com/oauth/authorize?client_id={REST_API_KEY}&redirect_uri={REDIRECT_URI}&response_type=code
         * https://kauth.kakao.com/oauth/authorize?client_id=ad2b3f54d2cfc0abc76ddd8b27cddd27&redirect_uri=http://localhost:8080/oauth/kakao/callback&response_type=code
         */

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        System.out.println("getAccessToken + 카카오 유저정보 받는 post는 통과함");

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        System.out.println("헤더까지는 받음 헤더 : " +headers);
        System.out.println("getKakaoUserInfo + 헤더까지는 받음 헤더 : " +headers);  //#

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        System.out.println("getKakaoUserInfo + 유저정보 받는 post는 통과함");  //#

        //response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        //nickname 랜덤
//        String nickname = jsonNode.get("properties").get("nickname").asText();
        Random rnd = new Random();
        String rdNick="";
        for (int i = 0; i < 8; i++) {
            rdNick += String.valueOf(rnd.nextInt(10));
        }
        String nickname = "K" + "_" + rdNick;

        String socialId = jsonNode.get("id").asText();
        String email = jsonNode.get("kakao_account").get("email").asText(); //이메일을 받아서 user DB의 username에 넣기

        System.out.println("카카오 사용자 정보: " + socialId + ", " + nickname+ ", " + email);
        return new SocialUserInfoDto(socialId, nickname, email);
    }

    private User registerKakaoUserIfNeeded(SocialUserInfoDto kakaoUserInfo) {
        System.out.println("카톡유저확인 클래스 들어옴");
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String kakaoEmail = kakaoUserInfo.getEmail();
        User kakaoUser = userRepository.findByUsername(kakaoEmail)
                .orElse(null);
        System.out.println("registerKakaoUserIfNeeded + kakaoUser : "+kakaoUser);  //#null값이 들어오네 그러면 회원가입 가능 기존 user가 없다는 뜻

        if (kakaoUser == null) {  // 회원가입
            String nickname = kakaoUserInfo.getNickname();
            System.out.println("닉네임 넣음 = "+nickname);
            String socialId = kakaoUserInfo.getSocialId();
//            String email = UUID.randomUUID().toString();  // 기존 username과 겹치지 않도록 복잡한 랜덤 username 생성
            String email = kakaoUserInfo.getEmail();
            System.out.println("유저네임 넣음 = "+email);

            // password: random UUID
            String password = UUID.randomUUID().toString();
            System.out.println("비밀번호 넣음 = "+password);

            String encodedPassword = passwordEncoder.encode(password);
            System.out.println("비밀번호 암호화  = "+encodedPassword);

            String profileImage = "기본 이미지 넣기";

            //가입할 때 일반사용자로 로그인
            UserRoleEnum role = UserRoleEnum.USER;

            kakaoUser = new User(email, nickname, encodedPassword, profileImage, role, socialId);
            userRepository.save(kakaoUser);
        }
        System.out.println("카카오톡 유저정보 넣음");
        return kakaoUser;
    }

    // 밑에 원본에서 조금 수정함
//    private void forceLogin(User kakaoUser) {
//        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String token = JwtTokenUtils.generateJwtToken(userDetails);
//    }

    private void jwtTokenCreate(User kakaoUser, HttpServletResponse response) {

        System.out.println("jwtTokenCreate 클래스 들어옴");

        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //여기까진 평범한 로그인과 같음
        System.out.println("강제로그인 시도까지 함");
        //여기부터 토큰 프론트에 넘기는것

        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());
        final String token = JwtTokenUtils.generateJwtToken(userDetails1);
        response.addHeader("Authorization", "BEARER" + " " + token);

        System.out.println("jwtTokenCreate + token값:" + token);  //#
    }
}

