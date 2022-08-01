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
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String kakaoRedirect;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 카카오 로그인
    @Transactional
    public void kakaoLogin(String code,HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        System.out.println("카카오 로그인 1번 접근");
        String accessToken = getAccessToken(code);

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

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId); //본인의 REST API키
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", kakaoRedirect); //성공 후 리다이렉트 되는 곳
//        body.add("redirect_uri", "http://localhost:3000/oauth/kakao/callback");
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

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출
    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String socialId = jsonNode.get("id").asText();

        // nickname 랜덤
        Random rnd = new Random();
        String rdNick="";
        for (int i = 0; i < 8; i++) {
            rdNick += String.valueOf(rnd.nextInt(10));
        }
        String nickname = "K" + "_" + rdNick;

        // 이메일 값 필수
        String email = jsonNode.get("kakao_account").get("email").asText();

        // 카카오에서 이미지 가져오기
        String profileImage = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
        String kakaoDefaultImg = "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"; //카카오 기본 이미지
        String defaultImage = "https://mytest-coffick.s3.ap-northeast-2.amazonaws.com/coffindBasicImage.png";
        if (profileImage==null || profileImage.equals(kakaoDefaultImg))
            profileImage = defaultImage; // 우리 사이트 기본 이미지

        return new SocialUserInfoDto(socialId, nickname, email, profileImage);
    }

    // 3. 필요시에 회원가입
    private User registerKakaoUserIfNeeded(SocialUserInfoDto kakaoUserInfo) {
        // 카카오에서 nickname이랑 username(이메일)이 랜덤으로 값이 들어간다. 그래서 또 다시 로그인 버튼을 누르면 같은 계정이라도
        // 다른 사용자인줄 알고 로그인이 된다. 그래서 소셜아이디로 구분해보자
        String kakaoSocialID = kakaoUserInfo.getSocialId();
        User kakaoUser = userRepository.findBySocialId(kakaoSocialID)
                        .orElse(null);

        // null값 오면 회원가입 가능 기존 사용자가 없다는 뜻
        if (kakaoUser == null) {  // 회원가입
            String nickname = kakaoUserInfo.getNickname();
            String socialId = kakaoUserInfo.getSocialId();
            String email = kakaoUserInfo.getEmail();
            String password = UUID.randomUUID().toString(); // password: random UUID
            String encodedPassword = passwordEncoder.encode(password); // 비밀번호 암호화
            String profileImage = kakaoUserInfo.getProfileImage();
            UserRoleEnum role = UserRoleEnum.USER; // 가입할 때 일반사용자로 로그인

            kakaoUser = new User(email, nickname, encodedPassword, profileImage, socialId);
            userRepository.save(kakaoUser);
        }

        return kakaoUser;
    }

    // 4. 강제 로그인 처리 & jwt 토큰 발급
    private void jwtTokenCreate(User kakaoUser, HttpServletResponse response) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 강제로그인 시도까지 함, 여기까진 평범한 로그인과 같음

        // 여기부터 토큰 프론트에 넘기는것
        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetails1);
        response.addHeader("Authorization", "BEARER" + " " + token);
    }
}

