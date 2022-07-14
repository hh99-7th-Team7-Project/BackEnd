package com.sparta.coffang.service;
/**
 * 프론트와 카카오 로그인 연결 된 후에 sout 출력문 다 삭제
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.responseDto.SocialUserInfoDto;
import com.sparta.coffang.model.User;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String kakaoClientSecret;

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

        // 4. 강제 로그인 처리 & jwt 토큰 발급
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
        body.add("client_id", kakaoClientId); //본인의 REST API키
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", "http://localhost:3000/oauth/kakao/callback"); //성공 후 리다이렉트 되는 곳
//        body.add("redirect_uri", "http://localhost:8080/oauth/kakao/callback");
        //body.add("redirect_uri", "http://3.36.78.102:8080/oauth/kakao/callback");
        body.add("code", code);

        /**
         * 카카오 로그인 잘 되는지 확인하기
         * https://kauth.kakao.com/oauth/authorize?client_id={REST_API_KEY}&redirect_uri={REDIRECT_URI}&response_type=code
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

        // 필수 값이 아니라 값이 없으면 null로 초기화
        String email =
                jsonNode.get("kakao_account").has("email") ?
                        jsonNode.get("kakao_account").get("email").asText() : null;

        //null로 들어온 이메일 값 임의의 이메일 값 부여하기... 이렇게 해도 되나?? -> 임의로 들어온 이메일 나중에 이메일 인증으로 바꿀 수 있게 해보자
        if(email == null) {
            String rdEmail="";
            for (int i = 0; i < 8; i++) {
                rdEmail += String.valueOf(rnd.nextInt(10));
            }
            email = "co" + rdEmail + "@coffind.com";
        }

        //카카오에서 이미지 가져오기
        String profileImage =
                jsonNode.get("kakao_account").get("profile").has("profile_image_url") ?
                        jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText()
                        : null;

        String kakaoDefaultImg = "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"; //카카오 기본 이미지
        String defaultImage = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/profileBasicImage.png";
        if (profileImage==null || profileImage.equals(kakaoDefaultImg)) {
            //우리 사이트 기본 이미지
            profileImage = defaultImage;
        }

        System.out.println("카카오 사용자 정보: " + socialId + ", " + nickname+ ", " + email + ", " + profileImage);
        return new SocialUserInfoDto(socialId, nickname, email, profileImage);
    }

    private User registerKakaoUserIfNeeded(SocialUserInfoDto kakaoUserInfo) {
        System.out.println("카톡유저확인 클래스 들어옴");
        // DB 에 중복된 Kakao Id 가 있는지 확인
//        String kakaoEmail = kakaoUserInfo.getEmail();
//        User kakaoUser = userRepository.findByUsername(kakaoEmail)
//                .orElse(null);

        //카카오에서 nickname이랑 username(이메일)이 랜덤으로 값이 들어간다. 그래서 또 다시 로그인 버튼을 누르면 같은 계정이라도
        // 다른 사용자인줄 알고 로그인이 된다. 그래서 소셜아이디로 구분해보자
        String kakaoSocialID = kakaoUserInfo.getSocialId();
        User kakaoUser = userRepository.findBySocialId(kakaoSocialID)
                        .orElse(null);

        System.out.println("kakaoUserInfo.getSocialId() = " +kakaoUserInfo.getSocialId() );
        System.out.println("kakaoSocialID = " + kakaoSocialID);

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

//            String profileImage = "기본 이미지 넣기";
            String profileImage = kakaoUserInfo.getProfileImage();
            System.out.println("profileImage = "+ profileImage);

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
//        final String token = JwtTokenUtils.generateJwtToken(userDetails1); //final은 왜 붙였지.??
        String token = JwtTokenUtils.generateJwtToken(userDetails1);
        response.addHeader("Authorization", "BEARER" + " " + token);

        System.out.println("jwtTokenCreate + token값:" + token);  //#
    }
}

