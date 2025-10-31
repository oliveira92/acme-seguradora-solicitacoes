package com.acme.seguradora.interfaces.mapper;

import com.acme.seguradora.domain.entity.HistoricoEstado;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.interfaces.dto.request.CriarSolicitacaoRequestDto;
import com.acme.seguradora.interfaces.dto.response.CriarSolicitacaoResponseDto;
import com.acme.seguradora.interfaces.dto.response.SolicitacaoResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SolicitacaoMapper {

    public Solicitacao toEntity(CriarSolicitacaoRequestDto dto) {
        return Solicitacao.builder()
                .customerId(dto.getCustomer_id())
                .productId(dto.getProduct_id())
                .category(dto.getCategory())
                .saleChannel(dto.getSalesChannel())
                .paymentMethod(dto.getPaymentMethod())
                .totalMonthlyPremiumAmount(dto.getTotal_monthly_premium_amount())
                .insuredAmount(dto.getInsured_amount())
                .coverages(dto.getCoverages())
                .assistances(dto.getAssistances())
                .build();
    }

    public CriarSolicitacaoResponseDto toCriarResponse(Solicitacao solicitacao) {
        return CriarSolicitacaoResponseDto.builder()
                .id(solicitacao.getId())
                .createdAt(solicitacao.getCreatedAt())
                .build();
    }

    public SolicitacaoResponseDto toResponseDto(Solicitacao solicitacao) {
        return SolicitacaoResponseDto.builder()
                .id(solicitacao.getId())
                .customerId(solicitacao.getCustomerId())
                .productId(solicitacao.getProductId())
                .category(solicitacao.getCategory())
                .salesChannel(solicitacao.getSaleChannel())
                .paymentMethod(solicitacao.getPaymentMethod())
                .status(solicitacao.getStatus())
                .createdAt(solicitacao.getCreatedAt())
                .finishedAt(solicitacao.getFinishedAt())
                .total_monthly_premium_amount(solicitacao.getTotalMonthlyPremiumAmount())
                .insured_amount(solicitacao.getInsuredAmount())
                .coverages(solicitacao.getCoverages())
                .assistances(solicitacao.getAssistances())
                .history(solicitacao.getHistory().stream()
                        .map(this::toHistoricoDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private SolicitacaoResponseDto.HistoricoResponseDto toHistoricoDto(HistoricoEstado historico) {
        return SolicitacaoResponseDto.HistoricoResponseDto.builder()
                .status(historico.getStatus())
                .timestamp(historico.getTimestamp())
                .build();
    }
}
