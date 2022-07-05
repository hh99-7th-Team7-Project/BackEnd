package com.sparta.coffang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sangil_be.login.dto.SocialLoginDto;
import com.project.sangil_be.model.GetTitle;
import com.project.sangil_be.model.User;
import com.project.sangil_be.mypage.repository.GetTitleRepository;
import com.project.sangil_be.login.repository.UserRepository;
import com.project.sangil_be.securtiy.UserDetailsImpl;
import com.project.sangil_be.securtiy.jwt.JwtTokenUtils;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final GetTitleRepository getTitleRepository;

    // 네이버 로그인
    public void naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

        // 1. 인가코드로 엑세스토큰 가져오기
        String accessToken = getAccessToken(code, state);

        // 2. 엑세스토큰으로 유저정보 가져오기
        SocialLoginDto naverUserInfo = getNaverUserInfo(accessToken);

        // 3. 유저확인 & 회원가입
        User naverUser = getUser(naverUserInfo);

        // 4. 시큐리티 강제 로그인
        Authentication authentication = securityLogin(naverUser);

        //5. jwt 토큰 발급
        jwtToken(authentication, response);
    }

    // 1. 인가코드로 엑세스토큰 가져오기
    private String getAccessToken(String code, String state) throws JsonProcessingException {

        // 헤더에 Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디에 필요한 정보 담기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "oq32J_8jgLtjcSRvYUO4");
        body.add("client_secret", "dc6LwAfBEL");
        body.add("code", code);
        body.add("state", state);

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST, naverToken,
                String.class
        );

        // response에서 엑세스토큰 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        String accessToken = responseToken.get("access_token").asText();
        return accessToken;
    }

    // 2. 엑세스토큰으로 유저정보 가져오기
    private SocialLoginDto getNaverUserInfo(String accessToken) throws JsonProcessingException {

        // 헤더에 엑세스토큰 담기, Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUser = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST, naverUser,
                String.class
        );

        // response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);


        String socialId = String.valueOf(jsonNode.get("response").get("id").asText());
        String username = jsonNode.get("response").get("nickname").asText()+ "_" + socialId;
        Random rnd = new Random();
        String s="";
        for (int i = 0; i < 8; i++) {
            s += String.valueOf(rnd.nextInt(10));
        }
        String nickname = "N" + "_" + s;

        return new SocialLoginDto(username, nickname, socialId);
    }

    // 3. 유저확인 & 회원가입
    private User getUser(SocialLoginDto naverUserInfo) {

        String socialId = naverUserInfo.getSocialId();
        User naverUser = userRepository.findBySocialId(socialId);

        if (naverUser == null) {
            // 회원가입
            // username: kakao nickname
            String naverusername =naverUserInfo.getUsername();
            String nickname = naverUserInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            String userImageUrl="없음";
            String userTitle="초심자";
            String userTitleImgUrl="https://i.esdrop.com/d/f/JdarL6WQ6C/CPrMK6E8n8.png";

            naverUser = new User(naverusername,socialId, encodedPassword,nickname,userImageUrl,userTitle,userTitleImgUrl);
            userRepository.save(naverUser);
            GetTitle getTitle = new GetTitle(userTitle,userTitleImgUrl,naverUser);
            getTitleRepository.save(getTitle);

        }
        return naverUser;
    }

    // 시큐리티 강제 로그인
    private Authentication securityLogin(User foundUser) {
        UserDetails userDetails = new UserDetailsImpl(foundUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // jwt 토큰 발급
    private void jwtToken(Authentication authentication,HttpServletResponse response) {
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);

    }
}