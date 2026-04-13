package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.model.Transaction;
import com.vitrung.vizo_dong.model.User;
import com.vitrung.vizo_dong.repository.TransactionRepository;
import com.vitrung.vizo_dong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public String processTransfer(String senderUsername, String receiverUsername, Long amount, String message) throws Exception {
        String normalizedMessage = (message == null || message.isBlank()) ? "Chuyển tiền" : message.trim();
        transferVizoDong(senderUsername, receiverUsername, amount, normalizedMessage, "TRANSFER");
        return "Chuyển thành công " + amount + " Vizo Đồng cho " + receiverUsername;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ) 
    public void transferVizoDong(String senderUsername, String receiverUsername, Long amount, String message, String type) throws Exception {

        if (amount <= 0) {
            throw new Exception("Số tiền phải lớn hơn 0");
        }
        if (senderUsername.equals(receiverUsername)) {
            throw new Exception("Không thể chuyển tiền cho chính mình");
        }
        User sender = userRepository.findByUsernameWithLock(senderUsername)
                .orElseThrow(() -> new Exception("Người gửi không tồn tại: " + senderUsername));
                
        User receiver = userRepository.findByUsernameWithLock(receiverUsername)
                .orElseThrow(() -> new Exception("Người nhận không tồn tại: " + receiverUsername));
        if (sender.getBalance() < amount) {
            throw new Exception("Số dư không đủ. Hiện có: " + sender.getBalance());
        }
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        Transaction tx = new Transaction(sender, receiver, amount, message, type);
        transactionRepository.save(tx);

        System.out.println("Giao dịch thành công: " + senderUsername + " -> " + receiverUsername + ": " + amount);
    }
}