package com.acme.seguradora.infrastructure.persistence.entity;

import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_estado")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitacao status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id")
    private SolicitacaoEntity solicitacao;
}
