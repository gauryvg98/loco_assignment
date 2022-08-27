
package com.loco.assessment.transaction_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(
        name = "transaction_links"
)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionLinkEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    Long id;
    String flatPath;
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST},
            mappedBy = "transactionLink"
    )
    @JsonManagedReference
    private List<TransactionEntity> transactions;
}
