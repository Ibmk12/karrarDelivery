package com.karrardelivery.service;

import com.karrardelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageService messageService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        return userRepository.findByPhoneAndDeletedFalse(phone)
                .map(user -> {
                    if (!user.isEnabled()) {
                        throw new UsernameNotFoundException(
                                messageService.getMessage("user.disabled")
                        );
                    }
                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getPhone())
                            .password(user.getPassword())
                            .roles(user.getRole())
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.not.found")
                ));
    }

}
