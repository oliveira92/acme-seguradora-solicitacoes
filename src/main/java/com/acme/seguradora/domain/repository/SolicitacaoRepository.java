package com.acme.seguradora.domain.repository;

import com.acme.seguradora.domain.entity.Solicitacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitacaoRepository {
    Solicitacao salvar(Solicitacao solicitacao);
    Optional<Solicitacao> buscarPorId(UUID id);
    List<Solicitacao> buscarPorCustomerId(UUID customerId);
}
