package com.acme.seguradora.interfaces.dto.response;

import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponseDto {

    private UUID id;
    private UUID customerId;
    private UUID productId;
    private CategoriaSegurado category;
    private CanalVenda salesChannel;
    private MetodoPagamento paymentMethod;
    private EstadoSolicitacao status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private BigDecimal total_monthly_premium_amount;
    private BigDecimal insured_amount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;
    private List<HistoricoResponseDto> history;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricoResponseDto {
        private EstadoSolicitacao status;
        private LocalDateTime timestamp;
    }
}
