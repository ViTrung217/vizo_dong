package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.TransactionHistoryDto;
import com.vitrung.vizo_dong.service.TransactionService;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HistoryController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/history")
    public String showHistory(Model model,
                              Principal principal,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "8") int size) {
        String username = principal.getName();
        Page<TransactionHistoryDto> historyPage = transactionService.getTransactionHistoryPage(username, page, size);
        Long balance = transactionService.getUserBalance(username);

        model.addAttribute("historyPage", historyPage);
        model.addAttribute("balance", balance);
        model.addAttribute("username", username);
        model.addAttribute("size", size);
        return "history";
    }
}
