
package com.loco.assessment.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loco.assessment.transaction_service.model.TransactionEntity;
import com.loco.assessment.transaction_service.model.TransactionLinkEntity;
import com.loco.assessment.transaction_service.pojo.SumResponse;
import com.loco.assessment.transaction_service.pojo.TransactionEntry;
import com.loco.assessment.transaction_service.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionService {
    final TransactionRepository transactionRepository;
    final TransactionLinkService transactionLinkService;
    final ObjectMapper objectMapper;

    @Transactional
    public void saveTransaction(TransactionEntry transactionEntry, Long transactionId) throws Exception {
        transactionEntry.setTransactionId(transactionId);
        TransactionEntity parentTransaction = null;
        if (transactionEntry.getParentId() != null) {
            Optional<TransactionEntity> parentTransactionEntityOptional = this.transactionRepository.findByTransactionId(transactionEntry.getParentId());
            if (!parentTransactionEntityOptional.isPresent()) {
                throw new Exception("Parent TransactionId not found");
            }

            parentTransaction = parentTransactionEntityOptional.get();
        }

        TransactionEntity transactionEntity = this.mapToEntity(transactionEntry);
        transactionEntity.setId(null);
        transactionEntity.setTransactionLink(this.generateTransactionLink(parentTransaction));
        this.transactionRepository.save(transactionEntity);
    }

    public TransactionEntry getTransactionById(Long transactionId) throws Exception {
        Optional<TransactionEntity> transactionEntityOptional = this.transactionRepository.findByTransactionId(transactionId);
        if (!transactionEntityOptional.isPresent()) {
            throw new Exception("TransactionId not found");
        } else {
            TransactionEntry transactionEntry = this.mapToEntry(transactionEntityOptional.get());
            transactionEntry.setId(null);
            transactionEntry.setLinkId(null);
            return transactionEntry;
        }
    }

    public List<TransactionEntry> filterByType(String type) {
        List<TransactionEntity> transactionEntities = this.transactionRepository.findAllByType(type);
        return CollectionUtils.isEmpty(transactionEntities) ? new ArrayList() : transactionEntities.stream().map(this::mapToEntry).collect(Collectors.toList());
    }

    public SumResponse getSumOfConnectedTransactions(Long transactionId) throws Exception {
        String transactionLinkKey = "/" + transactionId + "/";
        List<TransactionLinkEntity> linkEntities = this.transactionLinkService.getTransactionLinksFromParentTransactionKey(transactionLinkKey);
        TransactionEntry transactionEntry = this.getTransactionById(transactionId);
        AtomicLong sum = new AtomicLong(transactionEntry.getValue());
        if (!CollectionUtils.isEmpty(linkEntities)) {
            linkEntities.forEach((linkEntity) -> linkEntity.getTransactions().forEach((txn) -> sum.set(sum.get() + txn.getValue())));
        }
        return new SumResponse(sum.get());
    }

    private TransactionLinkEntity generateTransactionLink(TransactionEntity parentTransaction) {
        String path;
        if (parentTransaction == null) {
            path = null;
        } else if (parentTransaction != null && parentTransaction.getTransactionLink() == null) {
            path = "/" + parentTransaction.getTransactionId() + "/";
        } else {
            String var10000 = parentTransaction.getTransactionLink().getFlatPath();
            path = var10000 + parentTransaction.getTransactionId() + "/";
        }

        return this.transactionLinkService.saveTransactionLinkIfNotPresent(path);
    }

    public List<TransactionEntry> getAllTransactions(Integer page, Integer size) {
        if (page != null && size != null) {
            if (size < 1) {
                size = 150;
            }

            if (page < 0) {
                page = 0;
            }

            return this.transactionRepository.findAll(PageRequest.of(page, size)).stream().map(this::mapToEntry).collect(Collectors.toList());
        } else {
            return this.transactionRepository.findAll().stream().map(this::mapToEntry).collect(Collectors.toList());
        }
    }

    private TransactionEntry mapToEntry(TransactionEntity transactionEntity) {
        TransactionEntry transactionEntry = this.objectMapper.convertValue(transactionEntity, TransactionEntry.class);
        if (transactionEntity.getTransactionLink() != null) {
            transactionEntry.setLinkId(transactionEntity.getTransactionLink().getId());
        }

        return transactionEntry;
    }

    private TransactionEntity mapToEntity(TransactionEntry transactionEntry) {
        return this.objectMapper.convertValue(transactionEntry, TransactionEntity.class);
    }
}
