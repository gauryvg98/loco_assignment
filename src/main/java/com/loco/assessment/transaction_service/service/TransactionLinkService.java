
package com.loco.assessment.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loco.assessment.transaction_service.model.TransactionLinkEntity;
import com.loco.assessment.transaction_service.pojo.TransactionLinkEntry;
import com.loco.assessment.transaction_service.repository.TransactionLinkRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionLinkService {
    final TransactionLinkRepository transactionLinkRepository;
    final ObjectMapper objectMapper;

    @Cacheable(
            value = {"linkEntity"},
            unless = "#result == null"
    )
    public TransactionLinkEntity getLinkByPath(String path) {
        return this.transactionLinkRepository.findByFlatPath(path).orElse(null);
    }

    public List<TransactionLinkEntry> getAllLinks(Integer pageSize, Integer pageNumber) {
        return (pageSize != null && pageNumber != null) ?
                this.transactionLinkRepository.findAll(PageRequest.of(pageNumber, pageSize)).stream().map(this::mapToEntry).collect(Collectors.toList())
                : this.transactionLinkRepository.findAll().stream().map(this::mapToEntry).collect(Collectors.toList());
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Cacheable(
            value = {"linkEntity"},
            unless = "#result == null"
    )
    public TransactionLinkEntity saveTransactionLinkIfNotPresent(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        TransactionLinkEntity existingEntity = this.getLinkByPath(path);

        if (existingEntity == null) {
            existingEntity = new TransactionLinkEntity();
            existingEntity.setId(null);
            existingEntity.setFlatPath(path);
            existingEntity = this.transactionLinkRepository.save(existingEntity);
        }

        return existingEntity;
    }

    public List<TransactionLinkEntity> getTransactionLinksFromParentTransactionKey(String key) {
        List<TransactionLinkEntity> linkEntities = this.transactionLinkRepository.findByFlatPathContaining(key);
        return linkEntities;
    }

    private TransactionLinkEntry mapToEntry(TransactionLinkEntity transactionLinkEntity) {
        return this.objectMapper.convertValue(transactionLinkEntity, TransactionLinkEntry.class);
    }
}
