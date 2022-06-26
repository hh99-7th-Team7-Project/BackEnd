package com.sparta.airbnb_clone_be.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.airbnb_clone_be.dto.LoginResponseDto;
import com.sparta.airbnb_clone_be.model.User;
import com.sparta.airbnb_clone_be.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        System.out.println(userDetails.getUsername() + "'s token : " + TOKEN_TYPE + " " + token);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
        System.out.println("LOGIN SUCCESS!");

        //User nicakname 내려주기 - 동관 천재님꺼 참고
        response.setContentType("application/json");
        User user = userDetails.getUser();
        LoginResponseDto loginResponseDto = new LoginResponseDto(user.getNickname(), true, token);
        String result = mapper.writeValueAsString(loginResponseDto);
        response.getWriter().write(result);
    }

}
