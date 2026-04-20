package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.service.TransactionService;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CoffeeController {
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/give-coffee")
    public String giveCoffee(@RequestParam String receiver, Principal principal, RedirectAttributes redirectAttributes) {
        String sender = principal.getName();
        try {
            transactionService.giveCoffee(sender, receiver);
            redirectAttributes.addFlashAttribute("success", "Đã tặng 1 ly cà phê cho " + receiver);
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/home";
        }
    }
}
