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

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 구글 로그인
    public SocialUserInfoDto googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        // 1. 인가코드로 엑세스토큰 가져오기
        System.out.println("1. 진입");
        String accessToken = getAccessToken(code);

        // 2. 엑세스토큰으로 유저정보 가져오기
        System.out.println("2. 진입");
        SocialUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);

        // 3. 유저확인 & 회원가입
        System.out.println("3. 진입");
        User foundUser = getUser(googleUserInfo);

        // 4. 시큐리티 강제 로그인
        System.out.println("4. 진입");
        Authentication authentication = securityLogin(foundUser);

        // 5. jwt 토큰 발급
        System.out.println("5. 진입");
        jwtToken(authentication, response);
        return googleUserInfo;
    }

    // 1. 인가코드로 엑세스토큰 가져오기
    private String getAccessToken(String code) throws JsonProcessingException {

        // 헤더에 Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("1. headers  : "+ headers);

        // 바디에 필요한 정보 담기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id" , googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("code", code);
        body.add("redirect_uri", "http://localhost:8080/oauth/google/callback");
        body.add("grant_type", "authorization_code");
        System.out.println("1. body : "+body);

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
        System.out.println("1. googleToken : "+ googleToken);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("1. restTemplate : "+ restTemplate);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleToken,
                String.class
        );
        System.out.println("1. response : "+ response);

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
        System.out.println("2. headers : "+ headers);

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        System.out.println("2. googleUserInfoRequest : "+googleUserInfoRequest);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("2. restTemplate : "+ restTemplate);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.POST, googleUserInfoRequest,
                String.class
        ); //accessToken이 잘못되었을 경우
        System.out.println("2. response : "+ response);

        // response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        System.out.println("jsonNode.get(\"sub\").asLong() sub?? : "+ jsonNode.get("sub").asLong());
        System.out.println("jsonNode.get(\"id\").asText() id를 text로?? : "+ jsonNode.get("id").asText());

//        Long id = jsonNode.get("sub").asLong();
        String socialId = String.valueOf(jsonNode.get("sub").asLong());
        System.out.println(" 2. socialId1 : "+ socialId);
        Random rnd = new Random();
        String s="";
        for (int i = 0; i < 8; i++) {
            s += String.valueOf(rnd.nextInt(10));
        }
        socialId = "G" + "_" + s;
        System.out.println(" 2. socialId2 : "+ socialId);

        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("name").asText();

        System.out.println("구글 사용자 정보: " + socialId + ", " + nickname+ ", " + email);
        return new SocialUserInfoDto(socialId, nickname, email);

    }

    // 3. 유저확인 & 회원가입
    private User getUser(SocialUserInfoDto googleUserInfo) {
        // DB 에 중복된 Google email, nickname이 있는지 확인
        String googleEmail = googleUserInfo.getEmail();
        String nickname = googleUserInfo.getNickname();
        String socialId = googleUserInfo.getSocialId();
        User googleUser = userRepository.findByUsername(googleEmail)
                .orElse(null);

        if (googleUser == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

//            기본이미지
//            String profile = "https://ossack.s3.ap-northeast-2.amazonaws.com/basicprofile.png";
            String profileImage = "기본이미지 넣기";

            //가입할 때 일반사용자로 로그인
            UserRoleEnum role = UserRoleEnum.USER;

            googleUser = new User(googleEmail, nickname, encodedPassword, profileImage, role, socialId);
            userRepository.save(googleUser);
        }

        return googleUser;
    }

    // 4. 시큐리티 강제 로그인
    private Authentication securityLogin(User findUser) {

        UserDetails userDetails = new UserDetailsImpl(findUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 5. jwt 토큰 발급
    private void jwtToken(Authentication authentication, HttpServletResponse response) {
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);

    }
}
