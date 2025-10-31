package com.acme.seguradora.interfaces.controller;

import com.acme.seguradora.application.usecase.CancelarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.ConsultarSolicitacaoUseCase;
import com.acme.seguradora.application.usecase.CriarSolicitacaoUseCase;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.interfaces.dto.request.CriarSolicitacaoRequestDto;
import com.acme.seguradora.interfaces.dto.response.CriarSolicitacaoResponseDto;
import com.acme.seguradora.interfaces.dto.response.SolicitacaoResponseDto;
import com.acme.seguradora.interfaces.mapper.SolicitacaoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Solicitações de Apólice", description = "APIs para gerenciamento de solicitações de apólice de seguros")
public class SolicitacaoController {

    private final CriarSolicitacaoUseCase criarSolicitacaoUseCase;
    private final ConsultarSolicitacaoUseCase consultarSolicitacaoUseCase;
    private final CancelarSolicitacaoUseCase cancelarSolicitacaoUseCase;
    private final SolicitacaoMapper mapper;

    @PostMapping
    @Operation(
            summary = "Criar solicitação de apólice",
            description = "Cria uma nova solicitação de apólice de seguro"
    )
    public ResponseEntity<CriarSolicitacaoResponseDto> criar(
            @Validated @RequestBody CriarSolicitacaoRequestDto request) {

        log.info("Recebendo solicitação de criação de apólice para customer_id: {}", request.getCustomer_id());

        Solicitacao solicitacao = mapper.toEntity(request);
        Solicitacao solicitacaoCriada = criarSolicitacaoUseCase.executar(solicitacao);

        CriarSolicitacaoResponseDto response = mapper.toCriarResponse(solicitacaoCriada);

        log.info("Solicitação criada com sucesso. ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar solicitação por ID",
            description = "Retorna os detalhes de uma solicitação específica"
    )
    public ResponseEntity<SolicitacaoResponseDto> buscarPorId(@PathVariable UUID id) {

        log.info("Consultando solicitação por ID: {}", id);

        Solicitacao solicitacao = consultarSolicitacaoUseCase.buscarPorId(id);
        SolicitacaoResponseDto response = mapper.toResponseDto(solicitacao);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Consultar solicitações por customer_id",
            description = "Retorna todas as solicitações de um cliente"
    )
    public ResponseEntity<List<SolicitacaoResponseDto>> buscarPorCustomerId(
            @RequestParam("customer_id") UUID customerId) {

        log.info("Consultando solicitações para customer_id: {}", customerId);

        List<Solicitacao> solicitacoes = consultarSolicitacaoUseCase.buscarPorCustomerId(customerId);

        List<SolicitacaoResponseDto> response = solicitacoes.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancelar solicitação",
            description = "Cancela uma solicitação de apólice (não permitido se já aprovada ou rejeitada)"
    )
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {

        log.info("Cancelando solicitação ID: {}", id);

        cancelarSolicitacaoUseCase.executar(id);

        log.info("Solicitação cancelada com sucesso. ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}
