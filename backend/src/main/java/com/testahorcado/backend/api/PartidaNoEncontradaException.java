package com.testahorcado.backend.api;

public class PartidaNoEncontradaException extends RuntimeException {

    public PartidaNoEncontradaException() {
        super("La partida no existe.");
    }

}
