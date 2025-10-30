package com.acme.seguradora.application.service;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.ClassificacaoRisco;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ValidadorRegrasNegocioService {

    public boolean validarSolicitacao(Solicitacao solicitacao, ClassificacaoRisco classificacao) {
        BigDecimal capitalSegurado = solicitacao.getInsuredAmount();
        CategoriaSegurado categoria = solicitacao.getCategory();

        return switch (classificacao) {
            case REGULAR -> validarClienteRegular(capitalSegurado, categoria);
            case ALTO_RISCO -> validarClienteAltoRisco(capitalSegurado, categoria);
            case PREFERENCIAL -> validarClientePreferencial(capitalSegurado, categoria);
            case SEM_INFO -> validarClienteSemInformacao(capitalSegurado, categoria);
        };
    }

    private boolean validarClienteRegular(BigDecimal capitalSegurado, CategoriaSegurado categoria) {
        return switch (categoria) {
            case AUTO -> capitalSegurado.compareTo(new BigDecimal("75000.00")) <= 0;
            case RESIDENCIAL -> capitalSegurado.compareTo(new BigDecimal("250000.00")) <= 0;
            default -> capitalSegurado.compareTo(new BigDecimal("255000.00")) <= 0;
        };
    }

    private boolean validarClienteAltoRisco(BigDecimal capitalSegurado, CategoriaSegurado categoria) {
        return switch (categoria) {
            case AUTO -> capitalSegurado.compareTo(new BigDecimal("250000.00")) <= 0;
            case RESIDENCIAL -> capitalSegurado.compareTo(new BigDecimal("150000.00")) <= 0;
            default -> capitalSegurado.compareTo(new BigDecimal("125000.00")) <= 0;
        };
    }

    private boolean validarClientePreferencial(BigDecimal capitalSegurado, CategoriaSegurado categoria) {
        return switch (categoria) {
            case VIDA -> capitalSegurado.compareTo(new BigDecimal("800000.00")) <= 0;
            case AUTO, RESIDENCIAL -> capitalSegurado.compareTo(new BigDecimal("450000.00")) <= 0;
            default -> capitalSegurado.compareTo(new BigDecimal("375000.00")) <= 0;
        };
    }

    private boolean validarClienteSemInformacao(BigDecimal capitalSegurado, CategoriaSegurado categoria) {
        return switch (categoria) {
            case VIDA, RESIDENCIAL -> capitalSegurado.compareTo(new BigDecimal("220000.00")) <= 0;
            case AUTO -> capitalSegurado.compareTo(new BigDecimal("75000.00")) <= 0;
            default -> capitalSegurado.compareTo(new BigDecimal("55000.00")) <= 0;
        };
    }
}
