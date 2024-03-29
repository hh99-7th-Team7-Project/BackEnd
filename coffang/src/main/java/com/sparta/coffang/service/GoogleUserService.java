package com.sparta.coffang.service;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GoogleUserService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String googleRedirectUri;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 구글 로그인
    public void googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. 인가코드로 엑세스토큰 가져오기
        System.out.println("구글 로그인 1번 접근");
        String accessToken = getAccessToken(code);

        // 2. 엑세스토큰으로 유저정보 가져오기
        System.out.println("구글 로그인 2번 접근");
        SocialUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);

        // 3. 유저확인 & 회원가입
        System.out.println("구글 로그인 3번 접근");
        User foundUser = getUser(googleUserInfo);

        // 4. 시큐리티 강제 로그인
        System.out.println("구글 로그인 4번 접근");
        Authentication authentication = securityLogin(foundUser);

        // 5. jwt 토큰 발급
        System.out.println("구글 로그인 5번 접근");
        jwtToken(authentication, response);
    }

    // 1. 인가코드로 엑세스토큰 가져오기
    private String getAccessToken(String code) throws JsonProcessingException {
        // 헤더에 Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디에 필요한 정보 담기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id" , googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("code", code);
        body.add("redirect_uri", googleRedirectUri);
        body.add("grant_type", "authorization_code");

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleToken,
                String.class
        );

        // response에서 accessToken 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        return responseToken.get("access_token").asText();
    }

    // 2. 엑세스토큰으로 유저정보 가져오기
    private SocialUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        // 헤더에 엑세스토큰 담기, Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("getGoogleUserInfo + 헤더까지는 받음 헤더 : " +headers);  //#

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleUser = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.POST, googleUser,
                String.class
        );

        // response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String socialId = jsonNode.get("sub").asText();
        String email = jsonNode.get("email").asText();

        //nickname 랜덤
        Random rnd = new Random();
        String rdNick="";
        for (int i = 0; i < 8; i++) {
            rdNick += String.valueOf(rnd.nextInt(10));
        }
        String nickname = "G" + "_" + rdNick;

        // 카카오에서 이미지 가져오기
        String profileImage = jsonNode.get("picture").asText();
        String googleDefaultImg = "https://lh3.googleusercontent.com/a/AItbvmkmZXNdBh-oqIFJq_34wRHpzmWAG14RM0A22XWq=s96-c";
        String defaultImage = "https://mytest-coffick.s3.ap-northeast-2.amazonaws.com/coffindBasicImage.png";
        if (profileImage==null || profileImage.equals(googleDefaultImg))
            profileImage = defaultImage; // 우리 사이트 기본 이미지

        return new SocialUserInfoDto(socialId, nickname, email, profileImage);
    }

    // 3. 유저확인 & 회원가입
    private User getUser(SocialUserInfoDto googleUserInfo) {
        // 다른 소셜로그인이랑 이메일이 겹쳐서 잘못 로그인 될까봐. 다른 사용자인줄 알고 로그인이 된다. 그래서 소셜아이디로 구분해보자
        String googleEmail = googleUserInfo.getEmail();
        String googleSocialID = googleUserInfo.getSocialId();
        User googleUser = userRepository.findBySocialId(googleSocialID)
                .orElse(null);

        if (googleUser == null) {  // 회원가입
            String nickname = googleUserInfo.getNickname();
            String socialId = googleUserInfo.getSocialId();
            String password = UUID.randomUUID().toString(); // password: random UUID
            String encodedPassword = passwordEncoder.encode(password); // 비밀번호 암호화
            String profileImage = googleUserInfo.getProfileImage();

            googleUser = new User(googleEmail, nickname, encodedPassword, profileImage, socialId);
            userRepository.save(googleUser);
        }

        return googleUser;
    }

    // 4. 시큐리티 강제 로그인
    private Authentication securityLogin(User findUser) {
        UserDetails userDetails = new UserDetailsImpl(findUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //여기까진 평범한 로그인과 같음, 구글 강제로그인 시도까지 함
        return authentication;
    }

    // 5. jwt 토큰 발급
    private void jwtToken(Authentication authentication, HttpServletResponse response) {
        //여기부터 토큰 프론트에 넘기는것
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);
    }
}
