package com.cfkiatong.springbootbankingapp.security;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final UserStatus userStatus;
    private final Set<Role> roles;

    public UserPrincipal(UserEntity userEntity) {
        this.id = userEntity.getUserId();
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.userStatus = userEntity.getUserStatus();
        this.roles = userEntity.getRoles();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userStatus == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userStatus != UserStatus.LOCKED;
    }

}