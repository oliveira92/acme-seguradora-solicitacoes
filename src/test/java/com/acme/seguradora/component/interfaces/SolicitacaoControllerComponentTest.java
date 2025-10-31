package com.acme.seguradora.component.interfaces;

import com.acme.seguradora.application.usecase.CancelarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.ConsultarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.CriarSolicitacaoUseCase;
import com.acme.seguradora.domain.entity.HistoricoEstado;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import com.acme.seguradora.interfaces.controller.SolicitacaoController;
import com.acme.seguradora.interfaces.dto.request.CriarSolicitacaoRequestDto;
import com.acme.seguradora.interfaces.mapper.SolicitacaoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitacaoController.class)
@Import(SolicitacaoMapper.class)
class SolicitacaoControllerComponentTest {

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

    private Solicitacao solicitacao;
    private UUID solicitacaoId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        solicitacaoId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        solicitacao = Solicitacao.builder()
                .id(solicitacaoId)
                .customerId(customerId)
                .productId(UUID.randomUUID())
                .category(CategoriaSegurado.AUTO)
                .saleChannel(CanalVenda.MOBILE)
                .paymentMethod(MetodoPagamento.PIX)
                .status(EstadoSolicitacao.RECEIVED)
                .createdAt(LocalDateTime.now())
                .totalMonthlyPremiumAmount(new BigDecimal("99.90"))
                .insuredAmount(new BigDecimal("10000.00"))
                .coverages(Map.of("Roubo", new BigDecimal("5000.00")))
                .assistances(List.of("Guincho"))
                .history(List.of(HistoricoEstado.builder()
                        .status(EstadoSolicitacao.RECEIVED)
                        .timestamp(LocalDateTime.now())
                        .build()))
                .build();
    }

    @Test
    void deveCriarSolicitacao() throws Exception {
        when(criarSolicitacaoUseCase.executar(any(Solicitacao.class))).thenReturn(solicitacao);

        CriarSolicitacaoRequestDto request = CriarSolicitacaoRequestDto.builder()
                .customer_id(customerId)
                .product_id(UUID.randomUUID())
                .category(CategoriaSegurado.AUTO)
                .salesChannel(CanalVenda.MOBILE)
                .paymentMethod(MetodoPagamento.PIX)
                .total_monthly_premium_amount(new BigDecimal("99.90"))
                .insured_amount(new BigDecimal("10000.00"))
                .coverages(Map.of("Roubo", new BigDecimal("5000.00")))
                .assistances(List.of("Guincho"))
                .build();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(solicitacaoId.toString()));
    }

    @Test
    void deveBuscarSolicitacaoPorId() throws Exception {
        when(consultarSolicitacaoUseCase.buscarPorId(solicitacaoId)).thenReturn(solicitacao);

        mockMvc.perform(get("/orders/{id}", solicitacaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    void deveListarSolicitacoesPorCustomerId() throws Exception {
        when(consultarSolicitacaoUseCase.buscarPorCustomerId(customerId)).thenReturn(List.of(solicitacao));

        mockMvc.perform(get("/orders").param("customer_id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(solicitacaoId.toString()));
    }

    @Test
    void deveCancelarSolicitacao() throws Exception {
        doNothing().when(cancelarSolicitacaoUseCase).executar(eq(solicitacaoId));

        mockMvc.perform(delete("/orders/{id}", solicitacaoId))
                .andExpect(status().isNoContent());

        Mockito.verify(cancelarSolicitacaoUseCase).executar(solicitacaoId);
    }
}
