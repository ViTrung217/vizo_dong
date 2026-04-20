package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.dto.TransactionHistoryDto;
import com.vitrung.vizo_dong.dto.TransferRequestDto;
import com.vitrung.vizo_dong.entity.Transaction;
import com.vitrung.vizo_dong.entity.User;
import com.vitrung.vizo_dong.repository.TransactionRepository;
import com.vitrung.vizo_dong.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .limit(Math.max(limit, 0))
                .collect(Collectors.toList());
    }

    public long countAllTransactions() {
        return transactionRepository.count();
    }

    public long sumAllTransactedAmount() {
        Long value = transactionRepository.sumAllTransactedAmount();
        return value == null ? 0L : value;
    }

    public String processTransfer(String senderUsername, TransferRequestDto transferRequest) throws Exception {
        String normalizedMessage = (transferRequest.getMessage() == null || transferRequest.getMessage().isBlank())
                ? "Chuyển tiền"
                : transferRequest.getMessage().trim();

        transferVizoDong(
                senderUsername,
                transferRequest.getReceiver(),
                transferRequest.getAmount(),
                normalizedMessage,
                "TRANSFER"
        );
        return "Chuyển thành công " + transferRequest.getAmount() + " Vizo Đồng cho " + transferRequest.getReceiver();
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

    public List<TransactionHistoryDto> getTransactionHistory(String username)  {
        return getTransactionHistoryPage(username, 0, Integer.MAX_VALUE).getContent();
        }

        public Page<TransactionHistoryDto> getTransactionHistoryPage(String username, int page, int size) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại: " + username));

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        return transactionRepository
            .findAllByUser(currentUser, PageRequest.of(safePage, safeSize))
            .map(tx -> mapToHistoryDto(tx, username));
    }

    private TransactionHistoryDto mapToHistoryDto(Transaction tx, String currentUsername) {
        TransactionHistoryDto dto = new TransactionHistoryDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setMessage(tx.getMessage());
        dto.setType(tx.getType());
        dto.setCreatedAt(tx.getCreatedAt());

        if (tx.getSender() != null && tx.getSender().getUsername().equals(currentUsername)) {
            dto.setDirection("Gửi");
            dto.setSenderName("Tôi");
            dto.setReceiverName(tx.getReceiver() != null ? tx.getReceiver().getUsername() : "system");
            dto.setAmountDisplay("-" + tx.getAmount());
        } else {
            dto.setDirection("Nhận");
            dto.setSenderName(tx.getSender() != null ? tx.getSender().getUsername() : "system");
            dto.setReceiverName("Tôi");
            dto.setAmountDisplay("+" + tx.getAmount());
        }
        return dto;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor = Exception.class)
    public void giveCoffee(String senderUsername, String receiverUsername) throws Exception {
        if(senderUsername.equals(receiverUsername)){
            throw new Exception("Không thể tặng cà phê cho chính mình");
        }

        transferVizoDong(senderUsername, receiverUsername, 10000L, "Tặng cà phê", "GIVE_COFFEE");
    }

    public Long getUserBalance(String username){
        return userRepository.findByUsername(username)
            .map(User::getBalance)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }
}