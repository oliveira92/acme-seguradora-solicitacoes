package com.acme.seguradora.application.dto;

import com.acme.seguradora.domain.enums.ClassificacaoRisco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudeResponseDto {

    private UUID orderId;
    private UUID customerId;
    private LocalDateTime analyzedAt;
    private ClassificacaoRisco classification;
    private List<OcorrenciaDto> occurrences;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcorrenciaDto {
        private UUID id;
        private long productId;
        private String type;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
