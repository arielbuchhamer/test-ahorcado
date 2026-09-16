package com.testahorcado.backend.api;

import com.testahorcado.backend.JuegoAhorcado;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PartidaRepository {

    private final Map<String, JuegoAhorcado> partidas = new ConcurrentHashMap<>();

    public String guardar(JuegoAhorcado juego) {
        String id = UUID.randomUUID().toString();
        partidas.put(id, juego);
        return id;
    }

    public JuegoAhorcado buscar(String id) {
        JuegoAhorcado juego = partidas.get(id);

        if (juego == null) {
            throw new PartidaNoEncontradaException();
        }

        return juego;
    }

}
