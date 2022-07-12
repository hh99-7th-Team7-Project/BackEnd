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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverUserService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    String naverClientSecret;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 네이버 로그인
    public void naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

        // 1. 인가코드로 엑세스토큰 가져오기
        System.out.println("네이버 로그인 1번 접근");
        String accessToken = getAccessToken(code, state);
        System.out.println("인가 코드 : " + code);
        System.out.println("엑세스 토큰: " + accessToken);

        // 2. 엑세스토큰으로 유저정보 가져오기
        System.out.println("네이버 로그인 2번 접근");
        SocialUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);

        // 3. 유저확인 & 회원가입
        System.out.println("네이버 로그인 3번 접근");
        User naverUser = getUser(naverUserInfo);

        // 4. 시큐리티 강제 로그인
        System.out.println("네이버 로그인 4번 접근");
        Authentication authentication = securityLogin(naverUser);

        //5. jwt 토큰 발급
        System.out.println("네이버 로그인 5번 접근");
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
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("code", code);
        body.add("state", state);

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverToken = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverToken,
                String.class
        );
        System.out.println("getAccessToken + 네이버 유저정보 받는 post는 통과함");

        // response에서 엑세스토큰 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        return responseToken.get("access_token").asText();
    }

    // 2. 엑세스토큰으로 유저정보 가져오기
    private SocialUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {

        // 헤더에 엑세스토큰 담기, Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("getGoogleUserInfo + 헤더까지는 받음 헤더 : " +headers);  //#

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUser = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST, naverUser,
                String.class
        );
        System.out.println("getGoogleUserInfo + 유저정보 받는 post는 통과함");  //#

        // response에서 유저정보 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        //nickname 랜덤
        Random rnd = new Random();
        String rdNick="";
        for (int i = 0; i < 8; i++) {
            rdNick += String.valueOf(rnd.nextInt(10));
        }
        String nickname = "N" + "_" + rdNick;

        String socialId = String.valueOf(jsonNode.get("response").get("id").asText());
        String email = jsonNode.get("response").get("email").asText();

        String profileImage =
                jsonNode.get("response").has("profile_image") ?
                        jsonNode.get("response").get("profile_image").asText() : null;

        String naverDefaultImg = "네이버 기본 이미지도 있겠지??/";
        String defaultImage = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/fbcebde7-ae14-42f0-9a75-261914c1053f.png";
        if (profileImage==null || profileImage.equals(naverDefaultImg)) {
            //우리 사이트 기본 이미지
            profileImage = defaultImage;
        }

        System.out.println("네이버 사용자 정보: " + socialId + ", " + nickname+ ", " + email);
        return new SocialUserInfoDto(socialId, nickname, email, profileImage);
    }

    // 3. 유저확인 & 회원가입
    private User getUser(SocialUserInfoDto naverUserInfo) {
        System.out.println("네이버버유저확인 클래 들어옴");
        // DB 에 중복된 Naver email이 있는지 확인
        String naverEmail = naverUserInfo.getEmail();
        User naverUser = userRepository.findByUsername(naverEmail)
                .orElse(null);

        if (naverUser == null) {  // 회원가입
            String socialId = naverUserInfo.getSocialId();
            String nickname = naverUserInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

//            기본이미지
//            String profile = "https://ossack.s3.ap-northeast-2.amazonaws.com/basicprofile.png";
            String profileImage = "기본이미지 넣기";

            //가입할 때 일반사용자로 로그인
            UserRoleEnum role = UserRoleEnum.USER;

            naverUser = new User(naverEmail, nickname, encodedPassword, profileImage, role, socialId);
            userRepository.save(naverUser);
        }
        System.out.println("네이버 유저정보 넣음");
        return naverUser;
    }

    // 시큐리티 강제 로그인
    private Authentication securityLogin(User foundUser) {

        System.out.println("네이버 securityLogin 클래스 들어옴");

        UserDetails userDetails = new UserDetailsImpl(foundUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //여기까진 평범한 로그인과 같음
        System.out.println("네이버 강제로그인 시도까지 함");
        //여기부터 토큰 프론트에 넘기는것

        return authentication;
    }

    // jwt 토큰 발급
    private void jwtToken(Authentication authentication,HttpServletResponse response) {

        System.out.println("네이버 jwtToken 클래스 들어옴");

        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);

        System.out.println("네이버 jwtTokenCreate + token값:" + token);  //#

    }
}