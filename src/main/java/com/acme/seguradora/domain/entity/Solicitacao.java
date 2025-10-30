package com.acme.seguradora.domain.entity;

import com.acme.seguradora.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao {

    private UUID id;
    private UUID customerId;
    private UUID productId;
    private CategoriaSegurado category;
    private CanalVenda saleChannel;
    private MetodoPagamento paymentMethod;
    private EstadoSolicitacao status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;

    @Builder.Default
    private List<HistoricoEstado> history = new ArrayList<>();

    public void adicionarHistorico(EstadoSolicitacao novoEstado) {
        this.status = novoEstado;
        HistoricoEstado historico = HistoricoEstado.builder()
                .status(novoEstado)
                .timestamp(LocalDateTime.now())
                .build();
        this.history.add(historico);

        if (novoEstado == EstadoSolicitacao.APPROVED ||
                novoEstado == EstadoSolicitacao.REJECTED ||
                novoEstado == EstadoSolicitacao.CANCELED) {
            this.finishedAt = LocalDateTime.now();
        }
    }

    public boolean podeSerCancelada() {
        return status == EstadoSolicitacao.APPROVED &&
                status != EstadoSolicitacao.REJECTED;
    }

    public boolean estaFinalizada() {
        return status == EstadoSolicitacao.APPROVED ||
                status == EstadoSolicitacao.REJECTED ||
                status == EstadoSolicitacao.CANCELED;
    }
}
