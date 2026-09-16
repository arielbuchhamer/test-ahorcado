package com.testahorcado.backend.api;

import com.testahorcado.backend.JuegoAhorcado;
import com.testahorcado.backend.ResultadoIntento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    public record CrearPartidaRequest(String palabra) {
    }

    public record IntentarLetraRequest(String letra) {
    }

    private final PartidaRepository repository;

    public PartidaController(PartidaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaResponse crear(@RequestBody CrearPartidaRequest request) {
        JuegoAhorcado juego = new JuegoAhorcado(request.palabra());
        String id = repository.guardar(juego);
        return PartidaResponse.de(id, juego);
    }

    @GetMapping("/{id}")
    public PartidaResponse obtener(@PathVariable String id) {
        return PartidaResponse.de(id, repository.buscar(id));
    }

    @PostMapping("/{id}/letras")
    public PartidaResponse intentarLetra(@PathVariable String id, @RequestBody IntentarLetraRequest request) {
        JuegoAhorcado juego = repository.buscar(id);

        synchronized (juego) {
            ResultadoIntento resultado = juego.intentarLetra(request.letra());
            return PartidaResponse.de(id, juego, resultado);
        }
    }

}
