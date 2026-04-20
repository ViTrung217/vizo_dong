package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.CampaignCreateRequestDto;
import com.vitrung.vizo_dong.dto.CampaignDonateRequestDto;
import com.vitrung.vizo_dong.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/campaigns")
    public String showCampaigns(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "8") int size) {
        model.addAttribute("campaignPage", campaignService.getCampaignPage(page, size));
        model.addAttribute("size", size);
        return "campaign/list";
    }

    @GetMapping("/campaigns/{id}")
    public String showCampaignDetail(@PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Model model) {
        model.addAttribute("campaign", campaignService.getCampaign(id));
        model.addAttribute("historyPage", campaignService.getDonationHistory(id, page, size));
        model.addAttribute("donateRequest", new CampaignDonateRequestDto());
        model.addAttribute("size", size);
        return "campaign/detail";
    }

    @PostMapping("/campaigns/{id}/donate")
    public String donateToCampaign(@PathVariable Long id,
                                   @ModelAttribute CampaignDonateRequestDto donateRequest,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            campaignService.donate(id, principal.getName(), donateRequest.getAmount());
            redirectAttributes.addFlashAttribute("success", "Quyên góp thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/campaigns/" + id;
    }

    @GetMapping("/admin/campaigns/new")
    public String showCreateCampaignForm(Model model) {
        model.addAttribute("campaignCreate", new CampaignCreateRequestDto());
        return "admin/campaign-create";
    }

    @PostMapping("/admin/campaigns")
    public String createCampaign(@ModelAttribute CampaignCreateRequestDto campaignCreate,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            campaignService.createCampaign(principal.getName(), campaignCreate);
            redirectAttributes.addFlashAttribute("success", "Tạo campaign thành công");
            return "redirect:/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/campaigns/new";
        }
    }
}
