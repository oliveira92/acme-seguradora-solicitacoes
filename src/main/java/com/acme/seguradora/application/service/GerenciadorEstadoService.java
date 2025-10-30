package com.acme.seguradora.application.service;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.exception.ValidacaoNegocioException;
import org.springframework.stereotype.Service;

@Service
public class GerenciadorEstadoService {

    public void validarTransicao(EstadoSolicitacao estadoAtual, EstadoSolicitacao novoEstado) {
        boolean transicaoValida = switch (estadoAtual) {
            case RECEIVED -> novoEstado == EstadoSolicitacao.VALIDATED ||
                    novoEstado == EstadoSolicitacao.REJECTED ||
                    novoEstado == EstadoSolicitacao.CANCELED;
            case VALIDATED -> novoEstado == EstadoSolicitacao.PENDING ||
                    novoEstado == EstadoSolicitacao.CANCELED;
            case PENDING -> novoEstado == EstadoSolicitacao.APPROVED ||
                    novoEstado == EstadoSolicitacao.REJECTED;
            case APPROVED, REJECTED, CANCELED -> false;
        };

        if (!transicaoValida) {
            throw new ValidacaoNegocioException(
                    String.format("Transição inválida de %s para %s", estadoAtual, novoEstado)
            );
        }
    }

    public void atualizarEstado(Solicitacao solicitacao, EstadoSolicitacao novoEstado) {
        validarTransicao(solicitacao.getStatus(), novoEstado);
        solicitacao.adicionarHistorico(novoEstado);
    }
}
