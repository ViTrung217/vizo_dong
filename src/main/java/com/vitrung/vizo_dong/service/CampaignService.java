    package com.vitrung.vizo_dong.service;

    import com.vitrung.vizo_dong.dto.CampaignCreateRequestDto;
    import com.vitrung.vizo_dong.entity.Campaign;
    import com.vitrung.vizo_dong.entity.CampaignDonation;
    import com.vitrung.vizo_dong.entity.User;
    import com.vitrung.vizo_dong.repository.CampaignDonationRepository;
    import com.vitrung.vizo_dong.repository.CampaignRepository;
    import com.vitrung.vizo_dong.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    public class CampaignService {

        @Autowired
        private CampaignRepository campaignRepository;

        @Autowired
        private CampaignDonationRepository donationRepository;

        @Autowired
        private UserRepository userRepository;

        // private final CampaignRepository campaignRepository;
        // private final CampaignDonationRepository donationRepository;
        // private final UserRepository userRepository;

        // // Constructor Injection
        // public CampaignService(CampaignRepository campaignRepository,
        //                        CampaignDonationRepository donationRepository,
        //                        UserRepository userRepository) {
        //     this.campaignRepository = campaignRepository;
        //     this.donationRepository = donationRepository;
        //     this.userRepository = userRepository;
        // }
        public Campaign createCampaign(String creator, CampaignCreateRequestDto dto) {
            String name = dto.getName() == null ? "" : dto.getName().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Tên campaign không được để trống");
            }
            if (dto.getGoalAmount() == null || dto.getGoalAmount() <= 0) {
                throw new IllegalArgumentException("Mục tiêu campaign phải lớn hơn 0");
            }

            Campaign campaign = new Campaign();
            campaign.setName(name);
            campaign.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
            campaign.setGoalAmount(dto.getGoalAmount());
            campaign.setCurrentAmount(0L);
            campaign.setCreatedBy(creator);
            return campaignRepository.save(campaign);
        }

        @Transactional
        public void updateCampaignByAdmin(Long campaignId, String name, String description, Long goalAmount) {
            Campaign campaign = getCampaign(campaignId);

            String normalizedName = name == null ? "" : name.trim();
            if (normalizedName.isEmpty()) {
                throw new IllegalArgumentException("Tên campaign không được để trống");
            }
            if (goalAmount == null || goalAmount <= 0) {
                throw new IllegalArgumentException("Mục tiêu campaign phải lớn hơn 0");
            }
            if (goalAmount < campaign.getCurrentAmount()) {
                throw new IllegalArgumentException("Mục tiêu campaign không thể nhỏ hơn số tiền đã quyên góp");
            }

            campaign.setName(normalizedName);
            campaign.setDescription(description == null ? "" : description.trim());
            campaign.setGoalAmount(goalAmount);
            campaignRepository.save(campaign);
        }

        @Transactional
        public void deleteCampaignByAdmin(Long campaignId) {
            if (!campaignRepository.existsById(campaignId)) {
                throw new IllegalArgumentException("Campaign không tồn tại");
            }
            donationRepository.deleteByCampaignId(campaignId);
            campaignRepository.deleteById(campaignId);
        }

        public Page<Campaign> getCampaignPage(int page, int size) {
            Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"));
            return campaignRepository.findAll(pageable);
        }

        public List<Campaign> getRecentCampaigns(int limit) {
            return campaignRepository.findAll(
                            PageRequest.of(0, Math.max(limit, 1), Sort.by(Sort.Direction.DESC, "createdAt"))
                    )
                    .getContent();
        }

        public long getTotalRaised() {
            Long value = campaignRepository.sumTotalRaised();
            return value == null ? 0L : value;
        }


        public Campaign getCampaign(Long id) {
            return campaignRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Campaign không tồn tại"));
        }

        public Page<CampaignDonation> getDonationHistory(Long campaignId, int page, int size) {
            Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
            return donationRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId, pageable);
        }

        @Transactional(rollbackFor = Exception.class)
        public void donate(Long campaignId, String username, Long amount) {
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Số tiền quyên góp phải lớn hơn 0");
            }

            Campaign campaign = getCampaign(campaignId);
            User user = userRepository.findByUsernameWithLock(username)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

            if (user.getBalance() < amount) {
                throw new IllegalArgumentException("Số dư không đủ để quyên góp");
            }

            user.setBalance(user.getBalance() - amount);
            campaign.setCurrentAmount(campaign.getCurrentAmount() + amount);

            CampaignDonation donation = new CampaignDonation();
            donation.setCampaign(campaign);
            donation.setDonorUsername(username);
            donation.setAmount(amount);

            donationRepository.save(donation);
        }
    }



































