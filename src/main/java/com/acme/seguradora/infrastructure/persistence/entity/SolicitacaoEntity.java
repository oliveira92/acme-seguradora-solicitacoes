package com.acme.seguradora.infrastructure.persistence.entity;

import com.acme.seguradora.domain.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "solicitacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "customer_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID customerId;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaSegurado category;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_channel", nullable = false, length = 50)
    private CanalVenda salesChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private MetodoPagamento paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoSolicitacao status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "total_monthly_premium_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalMonthlyPremiumAmount;

    @Column(name = "insured_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coverages", columnDefinition = "JSON", nullable = false)
    private Map<String, BigDecimal> coverages;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "assistances", columnDefinition = "JSON", nullable = false)
    private List<String> assistances;

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("timestamp ASC")
    @Builder.Default
    private List<HistoricoEstadoEntity> history = new ArrayList<>();
}
