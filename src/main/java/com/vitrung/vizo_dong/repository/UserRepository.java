package com.vitrung.vizo_dong.repository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vitrung.vizo_dong.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // cộng tiền
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.username = :username")
    int addBalance(@Param("username") String username, @Param("amount") Long amount);

    // trừ tiền
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.username = :username AND u.balance >= :amount")
    int deductBalance(@Param("username") String username, @Param("amount") Long amount);
}
