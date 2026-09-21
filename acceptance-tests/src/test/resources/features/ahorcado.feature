# language: es
Característica: Jugar al ahorcado desde la web
  Como jugador
  Quiero adivinar la palabra secreta letra por letra
  Para ganar la partida antes de quedarme sin vidas

  Antecedentes:
    Dado que abro el juego

  Escenario: Ganar la partida adivinando todas las letras
    Dado que inicio una partida con la palabra "oso"
    Cuando arriesgo la letra "o"
    Y arriesgo la letra "s"
    Entonces veo el mensaje de victoria "Descubriste la palabra completa: oso."
    Y la palabra muestra "o s o"

  Escenario: Perder la partida al quedarse sin vidas
    Dado que inicio una partida con la palabra "sol"
    Cuando arriesgo las letras "a, b, c, d, e, f"
    Entonces veo el mensaje de derrota "Te quedaste sin vidas. La palabra secreta era sol."
    Y me quedan 0 vidas

  Escenario: Fallar una letra resta una vida
    Dado que inicio una partida con la palabra "casa"
    Cuando arriesgo la letra "x"
    Entonces me quedan 5 vidas
    Y las letras usadas son "x"
    Y veo el mensaje "No está: x."

  Escenario: Acertar una letra revela todas sus apariciones
    Dado que inicio una partida con la palabra "banana"
    Cuando arriesgo la letra "a"
    Entonces la palabra muestra "_ a _ a _ a"
    Y me quedan 6 vidas
    Y veo el mensaje "Bien: a está en la palabra."

  Escenario: Repetir una letra no resta vidas
    Dado que inicio una partida con la palabra "casa"
    Cuando arriesgo la letra "x"
    Y arriesgo la letra "x"
    Entonces veo el mensaje "La letra x ya fue usada."
    Y me quedan 5 vidas

  Escenario: Rechazar una palabra secreta con tildes
    Cuando intento iniciar una partida con la palabra "camión"
    Entonces veo el error "Solo se aceptan letras sin tildes. La ñ está permitida."
    Y la partida no comienza

  Escenario: Empezar una nueva partida
    Dado que inicio una partida con la palabra "oso"
    Cuando arriesgo la letra "x"
    Y empiezo una nueva partida
    Entonces veo el formulario para ingresar la palabra secreta
