package com.acme.seguradora.application.usecase;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.exception.SolicitacaoNaoEncontradaException;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarSolicitacaoUseCase {

    private final SolicitacaoRepository solicitacaoRepository;

    @Transactional(readOnly = true)
    public Solicitacao buscarPorId(UUID id) {
        return solicitacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(
                        "Solicitação não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> buscarPorCustomerId(UUID customerId) {
        return solicitacaoRepository.buscarPorCustomerId(customerId);
    }
}
