package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.ProfileUpdateRequestDto;
import com.vitrung.vizo_dong.entity.User;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        ProfileUpdateRequestDto profileUpdate = new ProfileUpdateRequestDto();
        profileUpdate.setEmail(currentUser.getEmail());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUpdate", profileUpdate);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute ProfileUpdateRequestDto profileUpdate,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(principal.getName(), profileUpdate);
            redirectAttributes.addFlashAttribute("success", "Cập nhật profile thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
