package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.TopupRequestDto;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    // Hiển thị form nạp tiền
    @GetMapping("/admin/topup")
    public String showTopupPage() {
        return "admin/topup";
    }

    // Xử lý nạp tiền
    @PostMapping("/admin/topup")
    public String processTopup(@ModelAttribute TopupRequestDto topupRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            boolean topupSuccess = userService.topupBalance(topupRequest);
            if (topupSuccess) {
                redirectAttributes.addFlashAttribute("success",
                        "Nạp thành công " + topupRequest.getAmount() + " Vizo Đồng cho " + topupRequest.getUsername());
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng: " + topupRequest.getUsername());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/topup";
    }
}