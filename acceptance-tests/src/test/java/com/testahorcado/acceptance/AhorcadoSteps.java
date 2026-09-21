package com.testahorcado.acceptance;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AhorcadoSteps {

    private Page page() {
        return NavegadorHooks.page;
    }

    private Locator porTestId(String testId) {
        return page().getByTestId(testId);
    }

    @Dado("que abro el juego")
    public void abrirElJuego() {
        page().navigate(NavegadorHooks.BASE_URL);
    }

    @Dado("que inicio una partida con la palabra {string}")
    public void iniciarPartida(String palabra) {
        intentarIniciarPartida(palabra);
        assertThat(porTestId("hidden-word")).isVisible();
    }

    @Cuando("intento iniciar una partida con la palabra {string}")
    public void intentarIniciarPartida(String palabra) {
        porTestId("secret-word-input").fill(palabra);
        porTestId("start-game-button").click();
    }

    @Cuando("arriesgo la letra {string}")
    public void arriesgarLetra(String letra) {
        porTestId("guess-input").fill(letra);
        porTestId("guess-button").click();

        // Espera la respuesta de la API: el input se limpia al procesar la letra.
        assertThat(porTestId("guess-input")).isEmpty();
    }

    @Cuando("arriesgo las letras {string}")
    public void arriesgarLetras(String letras) {
        for (String letra : letras.split(",")) {
            arriesgarLetra(letra.trim());
        }
    }

    @Cuando("empiezo una nueva partida")
    public void empezarNuevaPartida() {
        porTestId("new-game-button").click();
    }

    @Entonces("veo el mensaje de victoria {string}")
    public void verMensajeDeVictoria(String mensaje) {
        assertThat(porTestId("game-message")).containsText("Victoria");
        assertThat(porTestId("game-message")).containsText(mensaje);
    }

    @Entonces("veo el mensaje de derrota {string}")
    public void verMensajeDeDerrota(String mensaje) {
        assertThat(porTestId("game-message")).containsText("Partida perdida");
        assertThat(porTestId("game-message")).containsText(mensaje);
    }

    @Entonces("veo el mensaje {string}")
    public void verMensaje(String mensaje) {
        assertThat(porTestId("round-message")).hasText(mensaje);
    }

    @Entonces("veo el error {string}")
    public void verError(String mensaje) {
        assertThat(porTestId("secret-error")).hasText(mensaje);
    }

    @Entonces("me quedan {int} vidas")
    public void verVidas(int vidas) {
        assertThat(porTestId("lives-left")).hasText(String.valueOf(vidas));
    }

    @Entonces("las letras usadas son {string}")
    public void verLetrasUsadas(String letras) {
        assertThat(porTestId("used-letters")).hasText(letras);
    }

    @Entonces("la palabra muestra {string}")
    public void verPalabra(String palabra) {
        String[] letras = palabra.replace("_", "").split(" ", -1);
        assertThat(porTestId("hidden-word").locator(".letter-slot")).hasText(letras);
    }

    @Entonces("la partida no comienza")
    public void laPartidaNoComienza() {
        assertThat(porTestId("hidden-word")).not().isAttached();
        assertThat(porTestId("secret-word-input")).isVisible();
    }

    @Entonces("veo el formulario para ingresar la palabra secreta")
    public void verFormularioInicial() {
        assertThat(porTestId("secret-word-input")).isVisible();
        assertThat(porTestId("secret-word-input")).isEmpty();
        assertThat(porTestId("hidden-word")).not().isAttached();
    }

}
