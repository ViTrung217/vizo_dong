package com.vitrung.vizo_dong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vitrung.vizo_dong.entity.Transaction;
import com.vitrung.vizo_dong.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderOrderByCreatedAtDesc(User sender);
    List<Transaction> findByReceiverOrderByCreatedAtDesc(User receiver);
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUser(@Param("user") User user);
}
