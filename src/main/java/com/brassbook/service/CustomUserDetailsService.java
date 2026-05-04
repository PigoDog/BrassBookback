package com.brassbook.service;

import com.brassbook.dto.request.RegistrationRequest;
import com.brassbook.entity.User;
import com.brassbook.enums.UserRole;
import com.brassbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );
    }

    public User createNewUser(RegistrationRequest userToCreate) {
        User user = new User();
        user.setEmail(userToCreate.getEmail());
        user.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        user.setRole(userToCreate.getRoleName());
        user.setStatus(userToCreate.getStatus());
        user.setIsConfirmed(true);
        if (userToCreate.getRoleName() == UserRole.ROLE_COMPANY) {
            user.setFirstName(userToCreate.getFirstName());
            user.setLastName(userToCreate.getLastName());
            user.setCompanyName(userToCreate.getCompanyName());
            user.setProfession(userToCreate.getProfession());
            user.setInn(userToCreate.getInn());
        }
        return userRepository.save(user);
    }

}
