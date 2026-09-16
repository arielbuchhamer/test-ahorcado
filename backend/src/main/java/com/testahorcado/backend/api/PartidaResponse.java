package com.testahorcado.backend.api;

import com.testahorcado.backend.EstadoPartida;
import com.testahorcado.backend.JuegoAhorcado;
import com.testahorcado.backend.ResultadoIntento;

import java.util.List;

public record PartidaResponse(
        String id,
        String palabraOculta,
        int vidas,
        List<String> letrasUsadas,
        EstadoPartida estado,
        String palabraSecreta,
        ResultadoIntento resultado) {

    public static PartidaResponse de(String id, JuegoAhorcado juego) {
        return de(id, juego, null);
    }

    public static PartidaResponse de(String id, JuegoAhorcado juego, ResultadoIntento resultado) {
        EstadoPartida estado = juego.obtenerEstado();

        return new PartidaResponse(
                id,
                juego.obtenerPalabraOculta(),
                juego.obtenerVidas(),
                juego.obtenerLetrasUsadas().stream().map(String::valueOf).toList(),
                estado,
                estado == EstadoPartida.EN_JUEGO ? null : juego.obtenerPalabraSecreta(),
                resultado);
    }

}
