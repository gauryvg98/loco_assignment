
package com.loco.assessment.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loco.assessment.transaction_service.cache.TransactionCacheHandler;
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

import com.loco.assessment.transaction_service.utils.TransactionLinkParseUtil;
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
    final TransactionCacheHandler cacheHandler;
    final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
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

        if(transactionEntity.getTransactionLink() != null){
            cacheHandler.updateCachedConnectedSum(transactionEntity.getTransactionLink().getFlatPath(),transactionEntity.getValue());
        }
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

        Long cachedSum = cacheHandler.getCachedConnectedSum(transactionId);

        if(cachedSum != null){
            return new SumResponse(cachedSum);
        }

        String transactionLinkKey = TransactionLinkParseUtil.generateTransactionLinkKey(transactionId);

        List<TransactionLinkEntity> linkEntities = this.transactionLinkService.getTransactionLinksFromParentTransactionKey(transactionLinkKey);

        TransactionEntry transactionEntry = this.getTransactionById(transactionId);

        AtomicLong sum = new AtomicLong(transactionEntry.getValue());

        if (!CollectionUtils.isEmpty(linkEntities)) {
            linkEntities.forEach((linkEntity) -> linkEntity.getTransactions().forEach((txn) -> sum.set(sum.get() + txn.getValue())));
        }

        cacheHandler.insertConnectedSumIntoCache(transactionId,sum.get());

        return new SumResponse(sum.get());
    }

    private TransactionLinkEntity generateTransactionLink(TransactionEntity parentTransaction) {
        return this.transactionLinkService.saveTransactionLinkIfNotPresent(TransactionLinkParseUtil.generateTransactionLinkKey(parentTransaction));
    }

    public List<TransactionEntry> getAllTransactions(Integer page, Integer size) {
        return (page != null && size != null) ?
                this.transactionRepository.findAll(PageRequest.of(page, size)).stream().map(this::mapToEntry).collect(Collectors.toList())
                : this.transactionRepository.findAll().stream().map(this::mapToEntry).collect(Collectors.toList());
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
