package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.dto.RegisterRequestDto;
import com.vitrung.vizo_dong.dto.ProfileUpdateRequestDto;
import com.vitrung.vizo_dong.dto.TopupRequestDto;
import com.vitrung.vizo_dong.event.UserRegisteredEvent;
import com.vitrung.vizo_dong.entity.User;
import com.vitrung.vizo_dong.entity.UserRole;
import com.vitrung.vizo_dong.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void bootstrapAdminRole() {
        userRepository.findByUsername("admin").ifPresent(adminUser -> {
            if (adminUser.getRole() != UserRole.ADMIN) {
                adminUser.setRole(UserRole.ADMIN);
                userRepository.save(adminUser);
            }
        });
    }

    public void register(RegisterRequestDto registerRequest) throws Exception {
        String username = registerRequest.getUsername() == null ? "" : registerRequest.getUsername().trim();
        String email = registerRequest.getEmail() == null ? "" : registerRequest.getEmail().trim();

        if (username.isEmpty()) {
            throw new Exception("Username không được để trống");
        }
        if (email.isEmpty()) {
            throw new Exception("Email không được để trống");
        }

        if(userRepository.existsByUsername(username)) {
            throw new Exception("Nguoi dung da ton tai");
        }
        if(userRepository.existsByEmail(email)){
            throw new Exception("email da ton tai");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setBalance(0L);
        newUser.setRole(UserRole.USER);

        User savedUser = userRepository.save(newUser);
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(savedUser, LocaleContextHolder.getLocale()));
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

    public List<User> getAllUsersByBalanceDesc() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "balance"));
    }

    public Page<User> searchUsers(String keyword, int page, int size) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "balance"));

        if (normalizedKeyword.isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                normalizedKeyword,
                normalizedKeyword,
                pageable
        );
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long getTotalBalance() {
        return userRepository.findAll()
                .stream()
                .mapToLong(user -> user.getBalance() == null ? 0L : user.getBalance())
                .sum();
    }

    public void updateProfile(String username, ProfileUpdateRequestDto request) throws Exception {
        User user = getUserByUsername(username);

        String newEmail = request.getEmail() == null ? "" : request.getEmail().trim();
        if (newEmail.isEmpty()) {
            throw new Exception("Email không được để trống");
        }
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new Exception("Email đã tồn tại");
        }
        user.setEmail(newEmail);

        String newPassword = request.getNewPassword() == null ? "" : request.getNewPassword().trim();
        String confirmPassword = request.getConfirmPassword() == null ? "" : request.getConfirmPassword().trim();
        if (!newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new Exception("Mật khẩu xác nhận không khớp");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }

    public void updateUserRoleByAdmin(Long userId, UserRole role, String currentAdminUsername) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Người dùng không tồn tại"));

        if (user.getUsername().equalsIgnoreCase(currentAdminUsername) && role != UserRole.ADMIN) {
            throw new Exception("Không thể tự hạ quyền tài khoản admin đang đăng nhập");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteUserByAdmin(Long userId, String currentAdminUsername) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Người dùng không tồn tại"));

        if (user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new Exception("Không thể xóa tài khoản admin đang đăng nhập");
        }

        userRepository.delete(user);
    }

}
