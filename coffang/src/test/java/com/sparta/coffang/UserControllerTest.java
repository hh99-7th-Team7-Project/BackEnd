package com.sparta.coffang;

import com.google.gson.Gson;
import com.sparta.coffang.controller.UserController;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.dto.responseDto.LoginResponseDto;
import com.sparta.coffang.dto.responseDto.SignpResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(MockitoExtension.class)
//public class UserControllerTest {
//    @InjectMocks
//    private UserController userController;
//    @Mock
//    private UserService userService;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @DisplayName("회원 가입 성공")
//    @Test
//    void signUpSuccess() throws Exception {
//        // given
//        SignupRequestDto request = signUpRequest();
//        SignpResponseDto response = loginResponseDto();
//
//        doReturn(ResponseEntity.ok().body("회원가입을 축하합니다")).when(userService)
//                .signupUser(request, "");
//
//
//        ResultActions resultActions = mockMvc.perform(
//                    MockMvcRequestBuilders.post("/api/signUp")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(new Gson().toJson(request)));
//
// // then
//        MvcResult mvcResult = (MvcResult) resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("nickname", response.getNickname()).exists())
//                .andExpect(jsonPath("username", response.getUsername()).exists());
//
//    }
//
//    private SignupRequestDto signUpRequest() {
//        return SignupRequestDto.builder()
//                .username("test@test.test")
//                .nickname("test")
//                .password("password1")
//                .build();
//    }
//
//    private SignpResponseDto loginResponseDto() {
//        return SignpResponseDto.builder()
//                .username("test@test.test")
//                .nickname("testNickname")
//                .password("password1")
//                .admin(false)
//                .build();
//    }
//}
