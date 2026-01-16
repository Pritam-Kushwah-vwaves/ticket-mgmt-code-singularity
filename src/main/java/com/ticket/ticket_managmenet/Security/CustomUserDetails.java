package com.ticket.ticket_managmenet.Security;

import com.ticket.ticket_managmenet.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // ROLE_
//        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
//
//        // PERMISSIONS
//        user.getRole().getPermissions()
//                .forEach(p ->
//                        authorities.add(new SimpleGrantedAuthority(p.name()))
//                );

//        return authorities;
        return user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override public String getPassword() { return user.getPassword(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

