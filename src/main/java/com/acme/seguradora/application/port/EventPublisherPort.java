package com.acme.seguradora.application.port;

public interface EventPublisherPort {
    void publicar(String topico, Object evento);
}
