package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.AdminUserUpdateRequestDto;
import com.vitrung.vizo_dong.dto.TopupRequestDto;
import com.vitrung.vizo_dong.entity.User;
import com.vitrung.vizo_dong.entity.UserRole;
import com.vitrung.vizo_dong.service.CampaignService;
import com.vitrung.vizo_dong.service.TransactionService;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AdminController {

    private UserService userService;
    private TransactionService transactionService;
    private CampaignService campaignService;

    public AdminController(UserService userService, TransactionService transactionService, CampaignService campaignService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.campaignService = campaignService;
    }

    private void buildAdminModel(Model model, String keyword, int page, int size) {
        model.addAttribute("topupRequest", new TopupRequestDto());
        model.addAttribute("usersPage", userService.searchUsers(keyword, page, size));
        model.addAttribute("allCampaigns", campaignService.getCampaignPage(0, 100).getContent());
        model.addAttribute("keyword", keyword == null ? "" : keyword.trim());
        model.addAttribute("size", size);
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalBalance", userService.getTotalBalance());
        model.addAttribute("totalTransactions", transactionService.countAllTransactions());
        model.addAttribute("totalTransactedAmount", transactionService.sumAllTransactedAmount());
        model.addAttribute("campaignTotalRaised", campaignService.getTotalRaised());
        model.addAttribute("recentCampaigns", campaignService.getRecentCampaigns(8));
        model.addAttribute("recentTransactions", transactionService.getRecentTransactions(20));
    }

    @GetMapping("/admin")
    public String showAdminDashboard(Model model,
                                     @RequestParam(defaultValue = "") String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        buildAdminModel(model, keyword, page, size);
        return "admin/dashboard";
    }

    @GetMapping("/admin/users/{id}")
    public String showUserDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            AdminUserUpdateRequestDto userUpdate = new AdminUserUpdateRequestDto();
            userUpdate.setEmail(user.getEmail());
            userUpdate.setRole(user.getRole());

            model.addAttribute("user", user);
            model.addAttribute("userUpdate", userUpdate);
            model.addAttribute("topupRequest", new TopupRequestDto());
            model.addAttribute("roles", UserRole.values());
            return "admin/user-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    // Xử lý nạp tiền từ dashboard
    @PostMapping("/admin/users/topup")
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
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/update")
    public String updateUserDetail(@PathVariable Long id,
                                   @ModelAttribute AdminUserUpdateRequestDto userUpdate,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserByAdmin(id, userUpdate, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam UserRole role,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRoleByAdmin(id, role, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Cập nhật quyền thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserByAdmin(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Đã xóa người dùng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/admin/campaigns/{id}/update")
    public String updateCampaign(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 @RequestParam Long goalAmount,
                                 RedirectAttributes redirectAttributes) {
        try {
            campaignService.updateCampaignByAdmin(id, name, description, goalAmount);
            redirectAttributes.addFlashAttribute("success", "Cập nhật campaign thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }
    @PostMapping("/admin/campaigns/{id}/delete")
    public String deleteCampaign(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            campaignService.deleteCampaignByAdmin(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa campaign");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }
}
