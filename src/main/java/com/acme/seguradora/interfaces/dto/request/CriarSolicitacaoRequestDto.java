package com.acme.seguradora.interfaces.dto.request;

import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarSolicitacaoRequestDto {

    @NotNull(message = "customer_id é obrigatório")
    private UUID customer_id;

    @NotNull(message = "product_id é obrigatório")
    private UUID product_id;

    @NotNull(message = "category é obrigatório")
    private CategoriaSegurado category;

    @NotNull(message = "salesChannel é obrigatório")
    private CanalVenda salesChannel;

    @NotNull(message = "paymentMethod é obrigatório")
    private MetodoPagamento paymentMethod;

    @NotNull(message = "total_monthly_premium_amount é obrigatório")
    @Positive(message = "total_monthly_premium_amount deve ser positivo")
    private BigDecimal total_monthly_premium_amount;

    @NotNull(message = "insured_amount é obrigatório")
    @Positive(message = "insured_amount deve ser positivo")
    private BigDecimal insured_amount;

    @NotNull(message = "coverages é obrigatório")
    private Map<String, BigDecimal> coverages;

    @NotNull(message = "assistances é obrigatório")
    private List<String> assistances;
}
