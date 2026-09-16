package com.testahorcado.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class JuegoAhorcado {

    public static final int VIDAS_INICIALES = 6;

    private static final Locale ESPANIOL = Locale.forLanguageTag("es-AR");
    private static final Pattern PALABRA_VALIDA = Pattern.compile("[a-zñ]+");
    private static final Pattern LETRA_VALIDA = Pattern.compile("[a-zñ]");

    private final String palabraSecreta;
    private final List<Character> letrasUsadas = new ArrayList<>();
    private int vidas = VIDAS_INICIALES;

    public JuegoAhorcado(String palabraSecreta) {
        if (palabraSecreta == null || palabraSecreta.isBlank()) {
            throw new IllegalArgumentException("Ingresá una palabra secreta.");
        }

        String palabraNormalizada = normalizar(palabraSecreta);

        if (!PALABRA_VALIDA.matcher(palabraNormalizada).matches()) {
            throw new IllegalArgumentException("Solo se aceptan letras sin tildes. La ñ está permitida.");
        }

        this.palabraSecreta = palabraNormalizada;
    }

    public String obtenerPalabraOculta() {
        StringBuilder palabraOculta = new StringBuilder();

        for (char letra : palabraSecreta.toCharArray()) {
            palabraOculta.append(letrasUsadas.contains(letra) ? letra : '_').append(' ');
        }

        return palabraOculta.toString().trim();
    }

    public String obtenerPalabraSecreta() {
        return palabraSecreta;
    }

    public boolean adivinarPalabra(String palabra) {
        return palabraSecreta.equals(palabra);
    }

    public boolean adivinarLetra(char letra) {
        return intentarLetra(String.valueOf(letra)) == ResultadoIntento.ACIERTO;
    }

    public ResultadoIntento intentarLetra(String letra) {
        if (letra == null || !LETRA_VALIDA.matcher(normalizar(letra)).matches()) {
            throw new IllegalArgumentException("Ingresá una sola letra sin tildes.");
        }

        if (obtenerEstado() != EstadoPartida.EN_JUEGO)
            return ResultadoIntento.PARTIDA_TERMINADA;

        char letraNormalizada = normalizar(letra).charAt(0);

        if (letrasUsadas.contains(letraNormalizada))
            return ResultadoIntento.REPETIDA;

        letrasUsadas.add(letraNormalizada);

        if (palabraSecreta.indexOf(letraNormalizada) >= 0)
            return ResultadoIntento.ACIERTO;

        vidas--;
        return ResultadoIntento.FALLO;
    }

    public EstadoPartida obtenerEstado() {
        if (vidas == 0)
            return EstadoPartida.PERDIDA;

        if (!obtenerPalabraOculta().contains("_"))
            return EstadoPartida.GANADA;

        return EstadoPartida.EN_JUEGO;
    }

    public List<Character> obtenerLetrasUsadas() {
        return List.copyOf(letrasUsadas);
    }

    public int obtenerVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    private static String normalizar(String texto) {
        return texto.toLowerCase(ESPANIOL);
    }

}
