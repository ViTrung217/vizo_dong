package com.vitrung.vizo_dong.repository;

import com.vitrung.vizo_dong.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
	@Query("SELECT COALESCE(SUM(c.currentAmount), 0) FROM Campaign c")
	Long sumTotalRaised();
}
