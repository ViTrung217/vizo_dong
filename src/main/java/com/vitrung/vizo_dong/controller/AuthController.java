package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.RegisterRequestDto;
import com.vitrung.vizo_dong.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/register")
    public String ShowRegisterPage(){
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute RegisterRequestDto registerRequest,
                                  RedirectAttributes redirectAttributes){
                                        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
                                            redirectAttributes.addFlashAttribute("error", "Mat khau khong khop");
                                            return "redirect:/register";
                                        }
                                        try {
                                            userService.register(registerRequest);
                                            redirectAttributes.addFlashAttribute("success", "Dang ky thanh cong, vui long dang nhap");
                                            return "redirect:/login";
                                        } catch (Exception e) {
                                            redirectAttributes.addFlashAttribute("error", e.getMessage());
                                            return "redirect:/register";
                                    }


    }
    @GetMapping("/home")
    public String HomePage(Model model, Principal principal) {
        String username = principal.getName();

        User user = userService.getUserByUsername(username);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("balance", user.getBalance());
        return "home";
    }
}
