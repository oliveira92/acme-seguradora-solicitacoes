package com.acme.seguradora.application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoValidadaEvent {
    private UUID solicitacaoId;
    private UUID customerId;
    private String clssification;
    private LocalDateTime timestamp;
}
