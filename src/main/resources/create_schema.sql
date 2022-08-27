CREATE TABLE transaction_links (
                                   id bigint not null auto_increment,
                                   type flatPath(255) not null
)engine=InnoDB;

CREATE FULLTEXT INDEX flat_path_index
ON transaction_links(flatPath);

CREATE TABLE transactions (
                              id bigint not null auto_increment,
                              transaction_id bigint not null,
                              value bingint not null,
                              type varchar(255),
                              link_id bigint,
                              primary key (id),
                              foreign key (link_id) references transaction_links(flatPath)
)engine=InnoDB;

CREATE INDEX transaction_id_index ON transactions (transaction_id);

CREATE INDEX type_index ON transactions (type);