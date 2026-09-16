package com.testahorcado.backend;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void deberiaRechazarUnaPalabraSecretaConTildes() {
        assertThrows(IllegalArgumentException.class, () -> new JuegoAhorcado("camión"));
    }

    @Test
    void deberiaRechazarUnaPalabraSecretaConNumerosOEspacios() {
        assertThrows(IllegalArgumentException.class, () -> new JuegoAhorcado("hola mundo"));
        assertThrows(IllegalArgumentException.class, () -> new JuegoAhorcado("hola1"));
    }

    @Test
    void deberiaAceptarUnaPalabraSecretaConEnie() {
        JuegoAhorcado juego = new JuegoAhorcado("niño");

        assertEquals(ResultadoIntento.ACIERTO, juego.intentarLetra("ñ"));
    }

    @Test
    void deberiaIgnorarMayusculasEnLaPalabraSecretaYEnLasLetras() {
        JuegoAhorcado juego = new JuegoAhorcado("Hola");

        assertEquals(ResultadoIntento.ACIERTO, juego.intentarLetra("H"));
        assertEquals("h _ _ _", juego.obtenerPalabraOculta());
    }

    @Test
    void deberiaOcultarTodasLasLetrasAlEmpezar() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertEquals("_ _ _ _", juego.obtenerPalabraOculta());
    }

    @Test
    void deberiaRevelarTodasLasAparicionesDeUnaLetraAcertada() {
        JuegoAhorcado juego = new JuegoAhorcado("banana");

        juego.intentarLetra("a");

        assertEquals("_ a _ a _ a", juego.obtenerPalabraOculta());
    }

    @Test
    void deberiaRegistrarLasLetrasUsadasEnOrden() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        juego.intentarLetra("x");
        juego.intentarLetra("h");

        assertEquals(List.of('x', 'h'), juego.obtenerLetrasUsadas());
    }

    @Test
    void deberiaInformarLetraRepetidaSinRestarVidas() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        juego.intentarLetra("x");

        assertEquals(ResultadoIntento.REPETIDA, juego.intentarLetra("x"));
        assertEquals(5, juego.obtenerVidas());
        assertEquals(List.of('x'), juego.obtenerLetrasUsadas());
    }

    @Test
    void deberiaRechazarUnaLetraInvalida() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertThrows(IllegalArgumentException.class, () -> juego.intentarLetra("á"));
        assertThrows(IllegalArgumentException.class, () -> juego.intentarLetra("1"));
        assertThrows(IllegalArgumentException.class, () -> juego.intentarLetra("ab"));
        assertThrows(IllegalArgumentException.class, () -> juego.intentarLetra(""));
        assertThrows(IllegalArgumentException.class, () -> juego.intentarLetra(null));
        assertEquals(6, juego.obtenerVidas());
    }

    @Test
    void deberiaEmpezarEnJuego() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        assertEquals(EstadoPartida.EN_JUEGO, juego.obtenerEstado());
    }

    @Test
    void deberiaGanarAlDescubrirTodasLasLetras() {
        JuegoAhorcado juego = new JuegoAhorcado("oso");

        juego.intentarLetra("o");
        juego.intentarLetra("s");

        assertEquals(EstadoPartida.GANADA, juego.obtenerEstado());
    }

    @Test
    void deberiaPerderAlQuedarseSinVidas() {
        JuegoAhorcado juego = new JuegoAhorcado("hola");

        for (String letra : List.of("b", "c", "d", "e", "f", "g")) {
            juego.intentarLetra(letra);
        }

        assertEquals(EstadoPartida.PERDIDA, juego.obtenerEstado());
    }

    @Test
    void noDeberiaAceptarLetrasCuandoLaPartidaTermino() {
        JuegoAhorcado juego = new JuegoAhorcado("oso");
        juego.intentarLetra("o");
        juego.intentarLetra("s");

        assertEquals(ResultadoIntento.PARTIDA_TERMINADA, juego.intentarLetra("x"));
        assertEquals(6, juego.obtenerVidas());
        assertEquals(List.of('o', 's'), juego.obtenerLetrasUsadas());
    }

    @Test
    void deberiaExponerLaPalabraSecretaNormalizada() {
        JuegoAhorcado juego = new JuegoAhorcado("HOLA");

        assertEquals("hola", juego.obtenerPalabraSecreta());
    }
}
