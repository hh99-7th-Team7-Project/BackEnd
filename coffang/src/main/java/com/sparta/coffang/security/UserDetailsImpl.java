package com.sparta.airbnb_clone_be.security;

import com.sparta.airbnb_clone_be.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //user에서 username을 email로 설정해서 email로 들고 온거고 다른 것들 바꾸는 거보다 메서드는 그대로 써도 될거 같아서 그대로 나둠
    //email이랑 username 같다고 보면 됨
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    @Override //인가를 해주는 부분
//    public Collection<? extends GrantedAuthority> getAuthorities() {
////        UserRoleEnum role = user.getRole();
//        String authority = "ROLE_USER";
//
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(simpleGrantedAuthority);
//
//        return authorities;
//    }
@Override // 인가를 해주는 부분
public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
}
}