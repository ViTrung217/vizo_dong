package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.dto.RegisterRequestDto;
import com.vitrung.vizo_dong.dto.TopupRequestDto;
import com.vitrung.vizo_dong.entity.User;
import com.vitrung.vizo_dong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto registerRequest) throws Exception {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new Exception("Nguoi dung da ton tai");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new Exception("email da ton tai");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setBalance(0L);

        userRepository.save(newUser);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public boolean topupBalance(TopupRequestDto topupRequest) {
        String username = topupRequest.getUsername();
        Long amount = topupRequest.getAmount();
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("So tien nap phai lon hon 0");
        }
        return userRepository.addBalance(username, amount) > 0;
    }
}
