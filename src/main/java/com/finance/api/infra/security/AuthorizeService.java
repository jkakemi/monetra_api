package com.finance.api.infra.security;

import com.finance.api.infra.persistence.SpringUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService implements UserDetailsService {

    @Autowired
    private SpringUserRepository springUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return springUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
    }
}
