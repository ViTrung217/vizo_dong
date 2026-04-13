package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vitrung.vizo_dong.model.User;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(String username, String email, String rawPassword) throws Exception {
        if(userRepository.existsByUsername(username)) {
            throw new Exception("Nguoi dung da ton tai");
        }
        if(userRepository.existsByEmail(email)){
            throw new Exception("email da ton tai");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setBalance(0L);

        userRepository.save(newUser);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public boolean topupBalance(String username, Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("So tien nap phai lon hon 0");
        }
        return userRepository.addBalance(username, amount) > 0;
    }
}
