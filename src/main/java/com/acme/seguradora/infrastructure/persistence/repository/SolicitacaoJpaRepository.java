package com.acme.seguradora.infrastructure.persistence.repository;

import com.acme.seguradora.infrastructure.persistence.entity.SolicitacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolicitacaoJpaRepository extends JpaRepository<SolicitacaoEntity, UUID> {

    List<SolicitacaoEntity> findByCustomerId(UUID customerId);

}
