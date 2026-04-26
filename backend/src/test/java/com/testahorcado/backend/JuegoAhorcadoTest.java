package com.testahorcado.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JuegoAhorcadoTest {
    @Test
    void deberiaRechazarUnaPalabraSecretaVacia() {
        assertThrows(IllegalArgumentException.class, () -> new JuegoAhorcado(" "));
    }

    @Test
    void deberiaAceptarLaPalabraCorrecta() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertTrue(juego.adivinarPalabra("hola"));
    }

    @Test
    void deberiaRechazarUnaPalabraIncorrecta() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertFalse(juego.adivinarPalabra("chau"));
    }

    @Test
    void deberiaDevolverTrueSiLaLetraEstaEnLaPalabra() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertTrue(juego.adivinarLetra('h'));
    }

    @Test
    void deberiaMantenerLasVidasSiLaLetraEstaEnLaPalabra() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        juego.adivinarLetra('h');

        assertEquals(6, juego.obtenerVidas());
    }

    @Test
    void deberiaDevolverFalseSiLaLetraNoEstaEnLaPalabra() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertFalse(juego.adivinarLetra('x'));
    }

    @Test
    void deberiaRestarUnaVidaSiLaLetraNoEstaEnLaPalabra() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        juego.adivinarLetra('x');

        assertEquals(5, juego.obtenerVidas());
    }

    @Test
    void deberiaPerderLuegoDeQuedarseSinVidas() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        juego.adivinarLetra('x');
        juego.adivinarLetra('z');
        juego.adivinarLetra('q');
        juego.adivinarLetra('w');
        juego.adivinarLetra('t');
        juego.adivinarLetra('p');

        assertEquals(0, juego.obtenerVidas());
        assertFalse(juego.adivinarLetra('h'));
    }
}
