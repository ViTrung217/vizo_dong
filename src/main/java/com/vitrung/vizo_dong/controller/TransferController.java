
package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.model.User;
import com.vitrung.vizo_dong.service.TransactionService;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class TransferController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;

    // Hiển thị Form Chuyển tiền
    @GetMapping("/transfer")
    public String showTransferForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("balance", user.getBalance());
        return "transfer";
    }

    // Xử lý logic Chuyển tiền
    @PostMapping("/transfer")
    public String processTransfer(
            @RequestParam String receiver,
            @RequestParam Long amount,
            @RequestParam(required = false) String message,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        String sender = principal.getName();

        try {
            String successMessage = transactionService.processTransfer(sender, receiver, amount, message);
            redirectAttributes.addFlashAttribute("success", successMessage);
            return "redirect:/history"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/transfer";
        }
    }
}