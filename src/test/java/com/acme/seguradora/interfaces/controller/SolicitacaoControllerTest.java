package com.acme.seguradora.interfaces.controller;

import com.acme.seguradora.application.usecase.CancelarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.ConsultarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.CriarSolicitacaoUseCase;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import com.acme.seguradora.interfaces.dto.request.CriarSolicitacaoRequestDto;
import com.acme.seguradora.interfaces.dto.response.CriarSolicitacaoResponseDto;
import com.acme.seguradora.interfaces.mapper.SolicitacaoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitacaoController.class)
class SolicitacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CriarSolicitacaoUseCase criarSolicitacaoUseCase;

    @Mock
    private ConsultarSolicitacaoUseCase consultarSolicitacaoUseCase;

    @Mock
    private CancelarSolicitacaoUseCase cancelarSolicitacaoUseCase;

    @Mock
    private SolicitacaoMapper mapper;

    private Solicitacao solicitacao;
    private CriarSolicitacaoRequestDto request;

    @BeforeEach
    void setup() {
        solicitacao = buildSolicitacao();
        request = buildRequest();
    }

    @Test
    void deveCriarSolicitacaoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        CriarSolicitacaoResponseDto response = CriarSolicitacaoResponseDto.builder()
                .id(id)
                .createdAt(solicitacao.getCreatedAt())
                .build();

        when(mapper.toEntity(any())).thenReturn(solicitacao);
        when(criarSolicitacaoUseCase.executar(any())).thenReturn(solicitacao);
        when(mapper.toCriarResponse(any())).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deveBuscarSolicitacaoPorId() throws Exception {
        UUID id = UUID.randomUUID();

        when(consultarSolicitacaoUseCase.buscarPorId(id)).thenReturn(solicitacao);

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarSolicitacoesPorCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();

        when(consultarSolicitacaoUseCase.buscarPorCustomerId(customerId)).thenReturn(List.of(solicitacao));

        mockMvc.perform(get("/orders")
                        .param("customer_id", customerId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void deveCancelarSolicitacaoComSucesso() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isNoContent());
    }

    private CriarSolicitacaoRequestDto buildRequest() {
        return CriarSolicitacaoRequestDto.builder()
                .customer_id(UUID.randomUUID())
                .product_id(UUID.randomUUID())
                .category(CategoriaSegurado.RESIDENCIAL)
                .salesChannel(CanalVenda.MOBILE)
                .paymentMethod(MetodoPagamento.CREDIT_CARD)
                .total_monthly_premium_amount(new BigDecimal("200.00"))
                .insured_amount(new BigDecimal("15000.00"))
                .coverages(Map.of("Cobertura", new BigDecimal("5000.00")))
                .assistances(List.of("Chaveiro", "Encanador"))
                .build();
    }

    private Solicitacao buildSolicitacao() {
        return Solicitacao.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .category(CategoriaSegurado.RESIDENCIAL)
                .saleChannel(CanalVenda.MOBILE)
                .paymentMethod(MetodoPagamento.CREDIT_CARD)
                .status(EstadoSolicitacao.RECEIVED)
                .totalMonthlyPremiumAmount(new BigDecimal("200.00"))
                .insuredAmount(new BigDecimal("15000.00"))
                .coverages(Map.of("Cobertura", new BigDecimal("5000.00")))
                .assistances(List.of("Chaveiro", "Encanador"))
                .build();
    }
}
