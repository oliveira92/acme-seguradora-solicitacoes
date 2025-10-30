package com.acme.seguradora.application.port;

import com.acme.seguradora.application.dto.FraudeResponseDto;

import java.util.UUID;

public interface FraudeApiPort {
    FraudeResponseDto consultarFraude(UUID solicitacaoId, UUID customerId);
}
