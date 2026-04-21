package com.testahorcado.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTest {

    @Test
    void shouldReturnReadyStatus() {
        Application application = new Application();

        assertEquals("Backend listo", application.getStatus());
    }
}
