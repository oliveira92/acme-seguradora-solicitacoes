package com.acme.seguradora.domain.entity;

import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEstado {
    private EstadoSolicitacao status;
    private LocalDateTime timestamp;
}
