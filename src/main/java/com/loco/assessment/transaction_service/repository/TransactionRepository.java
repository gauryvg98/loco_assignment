
package com.loco.assessment.transaction_service.repository;

import com.loco.assessment.transaction_service.model.TransactionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByType(String type);

    Optional<TransactionEntity> findByTransactionId(Long txnId);

    Page<TransactionEntity> findAll(Pageable pageable);

    List<TransactionEntity> findAll();
}
