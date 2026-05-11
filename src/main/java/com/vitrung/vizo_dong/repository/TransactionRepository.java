package com.vitrung.vizo_dong.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<Transaction> findTop20ByOrderByCreatedAtDesc();

    @Query(value = "SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.sender = :user OR t.receiver = :user")
    Page<Transaction> findAllByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
    Long sumAllTransactedAmount();

}
