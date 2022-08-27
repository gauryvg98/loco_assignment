package com.loco.assessment.transaction_service.repository;

import com.loco.assessment.transaction_service.model.TransactionLinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLinkRepository extends CrudRepository<TransactionLinkEntity, Long> {
    Optional<TransactionLinkEntity> findByFlatPath(String flatPath);

    List<TransactionLinkEntity> findByFlatPathContaining(String flatPathKey);

    Page<TransactionLinkEntity> findAll(Pageable pageable);

    List<TransactionLinkEntity> findAll();
}
