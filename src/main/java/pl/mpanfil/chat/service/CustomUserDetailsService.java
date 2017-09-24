package pl.mpanfil.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.mpanfil.chat.domain.User;
import pl.mpanfil.chat.domain.UserFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marcin Panfil on 22.09.2017.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserFactory userFactory;

    @Autowired
    public CustomUserDetailsService(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userFactory.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add((GrantedAuthority) () -> "read");
                authorities.add((GrantedAuthority) () -> "write");
                return authorities;
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return username;
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
        };
    }
}
