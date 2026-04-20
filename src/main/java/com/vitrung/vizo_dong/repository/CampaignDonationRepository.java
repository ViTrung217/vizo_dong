package com.vitrung.vizo_dong.repository;

import com.vitrung.vizo_dong.entity.CampaignDonation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface CampaignDonationRepository extends JpaRepository<CampaignDonation, Long> {
    Page<CampaignDonation> findByCampaignIdOrderByCreatedAtDesc(Long campaignId, Pageable pageable);

    @Modifying
    @Transactional
    void deleteByCampaignId(Long campaignId);
}
