package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.request.AdminRequestDto;
import com.sparta.coffang.dto.request.SignupRequestDto;
import com.sparta.coffang.model.User;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    //보안상 원래는 이렇게 '관리자 가입 토큰' 보여주면 안됨
    //이 토큰을 이메일 인증으로 돌리던지 해봐야겠다.
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    public String signupUser(SignupRequestDto requestDto, PhotoDto photoDto) {
        String username = requestDto.getUsername();
        System.out.println("username : "+username);
        String nickname = requestDto.getNickname();
        System.out.println("nickname : "+nickname);
//        System.out.println("password : "+requestDto.getPassword());


        String profileImage = String.valueOf(photoDto);

        //패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());
//        System.out.println("password : "+password);

        //가입할 때 일반사용자로 로그인
        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(username,nickname, password, profileImage, role);
        userRepository.save(user);
        return "회원가입을 축하합니다!!";
    }

    public boolean checkUsername(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        return (!userRepository.findByUsername(username).isPresent());
//        if(userRepository.findByUsername(username).isPresent()){
////            throw new IllegalArgumentException("중복된 이메일입니다.");
//            //1.ResponseEntity 로 상태를 같이 넘겨줄지 (ok, bad 등등)
//            //2. 아니면 true, false
//            //3. 문자열로
//            return "존재하는 이메일입니다.";
//        } else {
//            return "사용가능한 이메일입니다.";
//        }
    }

    public boolean checkNickname(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        return (!userRepository.findByNickname(nickname).isPresent());
//        if(userRepository.findByNickname(nickname).isPresent()){
//            return "중복된 닉네임입니다.";
//        } else {
//            return "사용가능한 닉네임입니다.";
//        }
    }

    public UserRoleEnum adminAuthorization(AdminRequestDto requestDto, UserDetailsImpl userDetails) {
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        //역할 변경
        userDetails.getUser().setRole(role);
        //변경된 역할 저장
        userRepository.save(userDetails.getUser());

        return role;
    }
}
