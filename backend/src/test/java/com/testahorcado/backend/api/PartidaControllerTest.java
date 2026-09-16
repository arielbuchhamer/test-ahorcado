package com.testahorcado.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PartidaControllerTest {

    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deberiaCrearUnaPartida() throws Exception {
        mockMvc.perform(post("/api/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"palabra\":\"hola\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.palabraOculta").value("_ _ _ _"))
                .andExpect(jsonPath("$.vidas").value(6))
                .andExpect(jsonPath("$.letrasUsadas", emptyIterable()))
                .andExpect(jsonPath("$.estado").value("EN_JUEGO"))
                .andExpect(jsonPath("$.palabraSecreta", nullValue()));
    }

    @Test
    void deberiaRechazarUnaPalabraInvalida() throws Exception {
        mockMvc.perform(post("/api/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"palabra\":\"camión\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se aceptan letras sin tildes. La ñ está permitida."));
    }

    @Test
    void deberiaRechazarUnaPartidaSinPalabra() throws Exception {
        mockMvc.perform(post("/api/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ingresá una palabra secreta."));
    }

    @Test
    void deberiaAcertarUnaLetra() throws Exception {
        String id = crearPartida("hola");

        mockMvc.perform(post("/api/partidas/{id}/letras", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"o\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("ACIERTO"))
                .andExpect(jsonPath("$.palabraOculta").value("_ o _ _"))
                .andExpect(jsonPath("$.vidas").value(6))
                .andExpect(jsonPath("$.letrasUsadas", contains("o")));
    }

    @Test
    void deberiaRestarUnaVidaAlFallarUnaLetra() throws Exception {
        String id = crearPartida("hola");

        mockMvc.perform(post("/api/partidas/{id}/letras", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("FALLO"))
                .andExpect(jsonPath("$.vidas").value(5));
    }

    @Test
    void deberiaRechazarUnaLetraInvalida() throws Exception {
        String id = crearPartida("hola");

        mockMvc.perform(post("/api/partidas/{id}/letras", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"é\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ingresá una sola letra sin tildes."));
    }

    @Test
    void deberiaRevelarLaPalabraSecretaAlGanar() throws Exception {
        String id = crearPartida("oso");
        intentarLetra(id, "o");

        mockMvc.perform(post("/api/partidas/{id}/letras", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"s\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("GANADA"))
                .andExpect(jsonPath("$.palabraSecreta").value("oso"));
    }

    @Test
    void deberiaObtenerElEstadoDeUnaPartida() throws Exception {
        String id = crearPartida("hola");
        intentarLetra(id, "h");

        mockMvc.perform(get("/api/partidas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.palabraOculta").value("h _ _ _"))
                .andExpect(jsonPath("$.resultado", nullValue()));
    }

    @Test
    void deberiaResponderNotFoundSiLaPartidaNoExiste() throws Exception {
        mockMvc.perform(get("/api/partidas/{id}", "no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("La partida no existe."));

        mockMvc.perform(post("/api/partidas/{id}/letras", "no-existe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"a\"}"))
                .andExpect(status().isNotFound());
    }

    private String crearPartida(String palabra) throws Exception {
        String respuesta = mockMvc.perform(post("/api/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"palabra\":\"" + palabra + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = ID.matcher(respuesta);
        matcher.find();
        return matcher.group(1);
    }

    private void intentarLetra(String id, String letra) throws Exception {
        mockMvc.perform(post("/api/partidas/{id}/letras", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"letra\":\"" + letra + "\"}"))
                .andExpect(status().isOk());
    }
}
