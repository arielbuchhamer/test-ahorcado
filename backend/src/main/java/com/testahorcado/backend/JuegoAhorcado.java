package com.testahorcado.backend;

public class JuegoAhorcado {

    private final String palabraSecreta;
    private int vidas = 6;

    public JuegoAhorcado(String palabraSecreta) {
        if (palabraSecreta == null || palabraSecreta.isBlank()) {
            throw new IllegalArgumentException("La palabra secreta no puede estar vacia");
        }

        this.palabraSecreta = palabraSecreta;
    }

    public String obtenerPalabraOculta() {
        return "_ ".repeat(palabraSecreta.length()).trim();
    }

    public boolean adivinarPalabra(String palabra) {
        return palabraSecreta.equals(palabra);
    }

    public boolean adivinarLetra(char letra) {
        if (vidas == 0)
            return false;

        if (palabraSecreta.indexOf(letra) >= 0)
            return true;

        vidas--;
        return false;
    }

    public int obtenerVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

}
