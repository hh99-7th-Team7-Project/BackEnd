
//package com.sparta.airbnb_clone_be.service;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.airbnb_clone_be.dto.KakaoUserInfoDto;
//import com.sparta.airbnb_clone_be.model.User;
//import com.sparta.airbnb_clone_be.repository.UserRepository;
//import com.sparta.airbnb_clone_be.security.UserDetailsImpl;
//import com.sparta.airbnb_clone_be.security.jwt.JwtTokenUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import javax.servlet.http.HttpServletResponse;
//import javax.transaction.Transactional;
//import java.util.UUID;
//
//@RequiredArgsConstructor
//@Service
//public class KakaoUserService {
//    private final PasswordEncoder passwordEncoder;
//    private final UserRepository userRepository;
//
//    @Transactional
//    public void kakaoLogin(String code,HttpServletResponse response) throws JsonProcessingException {
//        // 1. "인가 코드"로 "액세스 토큰" 요청
//        System.out.println("1. \"인가 코드\"로 \"액세스 토큰\" 요청");  //#
//        String accessToken = getAccessToken(code);
////        System.out.println("인가 코드 : " + code);
//        System.out.println("1. getAccessToken + 인가 코드 : " + code);  //#
////        System.out.println("엑세스 토큰: " + accessToken);
//        System.out.println("1. getAccessToken + 엑세스 토큰: " + accessToken);  //#
//
//        // 2. 토큰으로 카카오 API 호출
//        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
//        System.out.println("2. kakaoUserInfo : "+ kakaoUserInfo);  //#
//
//        // 3. 필요시에 회원가입
//        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
//        System.out.println("3. kakaoUser : "+kakaoUser);  //#
//
//        // 4. 강제 로그인 처리
//        jwtTokenCreate(kakaoUser,response);
//        System.out.println("4. kakaoUser"+kakaoUser);  //#
//        System.out.println("4. response"+response);  //#
//    }
//
//    private String getAccessToken(String code) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        System.out.println("getAccessToken + headers : "+headers);  //#
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", "b6c4cf850cee0ecfe07d6cb202fa1c57"); //본인의 REST API키
//        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback"); //성공 후 리다이렉트 되는 곳
////        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback"); //테스트는 내꺼에서 해보자
////        body.add("redirect_uri", "http://3.36.78.102:8080/user/kakao/callback"); //테스트는 내꺼에서 해보자
//        body.add("code", code);
//        System.out.println("getAccessToken + body : "+body);  //#
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
//                new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//        System.out.println("getAccessToken + 유저정보 받는 post는 통과함");  //#
//        System.out.println("getAccessToken + response : "+response);  //#
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        String responseBody = response.getBody();
//        System.out.println("getAccessToken + responseBody : "+responseBody);  //#
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        System.out.println("getAccessToken + jsonNode : "+jsonNode);  //#
//        System.out.println("getAccessToken + jsonNode.get(\"access_token\").asText() : "+jsonNode.get("access_token").asText());  //#
//        return jsonNode.get("access_token").asText();
//    }
//
//    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
////        System.out.println("헤더까지는 받음 헤더 : " +headers);
//        System.out.println("getKakaoUserInfo + 헤더까지는 받음 헤더 : " +headers);  //#
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoUserInfoRequest,
//                String.class
//        );
//        System.out.println("getKakaoUserInfo + 유저정보 받는 post는 통과함");  //#
//
//        String responseBody = response.getBody();
////        System.out.println("resposneBody " + responseBody);
//        System.out.println("getKakaoUserInfo + resposneBody " + responseBody);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        Long id = jsonNode.get("id").asLong();
//        System.out.println("getKakaoUserInfo + id = " + id);  //#
//
//        String nickname = jsonNode.get("properties")
//                .get("nickname").asText();
//        String email = jsonNode.get("kakao_account")
//                .get("email").asText();
//
//        System.out.println("카카오 사용자 정보: " + id + ", " + nickname+ ", " + email);
//        return new KakaoUserInfoDto(id, nickname, email);
//    }
//
//    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
//        // DB 에 중복된 Kakao Id 가 있는지 확인
//        System.out.println("카톡유저확인 클래스 들어옴");
//        Long kakaoId = kakaoUserInfo.getId();
//        System.out.println("registerKakaoUserIfNeeded + kakaoId : "+kakaoId);  //#
//        User kakaoUser = userRepository.findByKakaoId(kakaoId)
//                .orElse(null);
//        System.out.println("registerKakaoUserIfNeeded + kakaoUser : "+kakaoUser);  //#
//
//        if (kakaoUser == null) {
//            // 회원가입
//            // username: kakao nickname
//            String nickname = kakaoUserInfo.getNickname();
//            System.out.println("닉네임 넣음 = "+nickname);
////            String username = kakaoUserInfo.getNickname();
//            String email = UUID.randomUUID().toString();  // 기존 username과 겹치지 않도록 복잡한 랜덤 username 생성
//            System.out.println("유저네임 넣음 = "+email);
//
//            // password: random UUID
//            String password = UUID.randomUUID().toString();
//            System.out.println("비밀번호 넣음 = "+password);
//
//            String encodedPassword = passwordEncoder.encode(password);
//            System.out.println("비밀번호 암호화  = "+encodedPassword);
//
//            kakaoUser = new User(email, nickname, encodedPassword, kakaoId);
//            userRepository.save(kakaoUser);
//        }
//        System.out.println("카카오톡 유저정보 넣음");
//        return kakaoUser;
//    }
//
//    // 밑에 원본에서 조금 수정함
////    private void forceLogin(User kakaoUser) {
////        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
////        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////
////        String token = JwtTokenUtils.generateJwtToken(userDetails);
////    }
//
//    private void jwtTokenCreate(User kakaoUser, HttpServletResponse response) {
//
//        System.out.println("jwtTokenCreate 클래스 들어옴");
//
//        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        //여기까진 평범한 로그인과 같음
//        System.out.println("강제로그인 시도까지 함");
//        //여기부터 토큰 프론트에 넘기는것
//
//        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());
//
//        System.out.println("jwtTokenCreate + userDetails1 : " + userDetails1.toString());  //#
//
//        final String token = JwtTokenUtils.generateJwtToken(userDetails1);
//
//        System.out.println("jwtTokenCreate + token값:" + token);  //#
//        response.addHeader("Authorization", "BEARER" + " " + token);
//
//    }
//}

