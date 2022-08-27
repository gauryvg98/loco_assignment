
package com.loco.assessment.transaction_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(
        name = "transactions"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    Long id;
    @Column(
            name = "transaction_id"
    )
    Long transactionId;
    Long value;
    String type;
    @Column(
            name = "parent_id"
    )
    Long parentId;
    @JoinColumn(
            name = "link_id"
    )
    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST}
    )
    @JsonBackReference
    TransactionLinkEntity transactionLink;

}
