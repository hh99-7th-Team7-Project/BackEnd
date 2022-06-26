package com.sparta.airbnb_clone_be.security;

import com.sparta.airbnb_clone_be.security.filter.FormLoginFilter;
import com.sparta.airbnb_clone_be.security.filter.JwtAuthFilter;
import com.sparta.airbnb_clone_be.security.jwt.HeaderTokenExtractor;
import com.sparta.airbnb_clone_be.security.provider.FormLoginAuthProvider;
import com.sparta.airbnb_clone_be.security.provider.JWTAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    public WebSecurityConfig(
            JWTAuthProvider jwtAuthProvider,
            HeaderTokenExtractor headerTokenExtractor
    ) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.headerTokenExtractor = headerTokenExtractor;
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll() // preflight 대응
                .antMatchers("/auth/**").permitAll(); // /auth/**에 대한 접근을 인증 절차 없이 허용(로그인 관련 url)
        // 특정 권한을 가진 사용자만 접근을 허용해야 할 경우, 하기 항목을 통해 가능

        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().sameOrigin();
        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 FormLoginFilter, JwtFilter 를 등록합니다.
         * FormLoginFilter : 로그인 인증을 실시합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */
        http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .anyRequest()
                .permitAll();
//                .and()
//                // [로그아웃 기능]
//                .logout()
//                // 로그아웃 요청 처리 URL
//                .logoutUrl("/api/logout")
//                .logoutSuccessUrl("/")
//                .permitAll();
    }

    @Bean
    public FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
        formLoginFilter.setFilterProcessesUrl("/login"); //로그인이 진행 됨
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        formLoginFilter.afterPropertiesSet();
        return formLoginFilter;
    }

    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler();
    }

    @Bean
    public FormLoginAuthProvider formLoginAuthProvider() {
        return new FormLoginAuthProvider(encodePassword());
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // Static 정보 접근 허용
        skipPathList.add("GET,/images/**");
        skipPathList.add("GET,/css/**");

        // h2-console 허용 - 위에 있어서 주석
//        skipPathList.add("GET,/h2-console/**");
//        skipPathList.add("POST,/h2-console/**");

        // 회원 관리 API 허용
        skipPathList.add("GET,/user/**");

        //카카오톡 skipPathList
//        skipPathList.add("GET,/oauth/**");
//        skipPathList.add("GET,/oauth/kakao/callback");
//        skipPathList.add("GET,/user/kakao/callback");

        //회원가입하기, 로그인 관련 skipPathList
        skipPathList.add("POST,/signup");  //회원가입
        skipPathList.add("POST,/signup/email");  //email 중복 체크
        skipPathList.add("POST,/signup/nickname");  //nickname 중복 체크
        skipPathList.add("POST,/login");

        //로그인 없이도 접근 가능한 skipPathList
        skipPathList.add("GET,/api/**"); //GET메서드에 /api 다음 주소는 모두 로그인없이 접근 가능

        //로그인이 왜 두개지?
//        skipPathList.add("POST,/api/login");
//        skipPathList.add("POST,/user/login");

        //포스트,댓글 불러오는것 허락---------------로그인 성공하고 다시 보자
//        skipPathList.add("GET,/api/main");
//        skipPathList.add("GET,/api/detail/{postid}");
//        skipPathList.add("GET,/api/comment/{postId}");

//----------아래는 그대로----------
        skipPathList.add("GET,/");
        skipPathList.add("GET,/basic.js");

        skipPathList.add("GET,/favicon.ico");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}