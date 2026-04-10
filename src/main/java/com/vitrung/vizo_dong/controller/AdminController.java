package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // Hiển thị form nạp tiền
    @GetMapping("/admin/topup")
    public String showTopupPage() {
        return "admin/topup";
    }

    // Xử lý nạp tiền
    @PostMapping("/admin/topup")
    public String processTopup(@RequestParam String username,
                               @RequestParam Long amount,
                               RedirectAttributes redirectAttributes) {
        try {
            int rows = userRepository.addBalance(username, amount);
            if (rows > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Nạp thành công " + amount + " Vizo Đồng cho " + username);
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng: " + username);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/topup";
    }
}