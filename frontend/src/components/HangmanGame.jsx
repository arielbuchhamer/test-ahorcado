import { Component } from 'react';
import { CircleX, Eye, EyeOff, Play, RotateCcw, Send, Trophy } from 'lucide-react';

const MAX_LIVES = 6;
const VALID_WORD = /^[a-zñ]+$/i;
const VALID_LETTER = /^[a-zñ]$/i;
const ALPHABET = 'abcdefghijklmnñopqrstuvwxyz'.split('');

function normalizeText(value) {
  return value.toLocaleLowerCase('es-AR');
}

function HangmanDrawing({ misses }) {
  return (
    <svg
      className="hangman-drawing"
      viewBox="0 0 220 250"
      role="img"
      aria-label={`Dibujo del ahorcado con ${misses} errores de ${MAX_LIVES}`}
      data-testid="hangman-drawing"
    >
      <line className="gallows" x1="34" y1="224" x2="188" y2="224" />
      <line className="gallows" x1="62" y1="224" x2="62" y2="28" />
      <line className="gallows" x1="62" y1="28" x2="156" y2="28" />
      <line className="gallows" x1="156" y1="28" x2="156" y2="58" />
      <line className="gallows" x1="62" y1="68" x2="100" y2="28" />

      {misses >= 1 && <circle className="body-part" cx="156" cy="78" r="20" />}
      {misses >= 2 && <line className="body-part" x1="156" y1="98" x2="156" y2="156" />}
      {misses >= 3 && <line className="body-part" x1="156" y1="118" x2="128" y2="142" />}
      {misses >= 4 && <line className="body-part" x1="156" y1="118" x2="184" y2="142" />}
      {misses >= 5 && <line className="body-part" x1="156" y1="156" x2="132" y2="194" />}
      {misses >= 6 && <line className="body-part" x1="156" y1="156" x2="180" y2="194" />}
    </svg>
  );
}

class HangmanGame extends Component {
  state = {
    secretInput: '',
    showSecret: false,
    secretWord: '',
    guessedLetters: [],
    letterInput: '',
    setupError: '',
    roundMessage: '',
  };

  get isPlaying() {
    return this.state.secretWord.length > 0;
  }

  get wrongLetters() {
    const { guessedLetters, secretWord } = this.state;
    return guessedLetters.filter((letter) => !secretWord.includes(letter));
  }

  get livesLeft() {
    return MAX_LIVES - this.wrongLetters.length;
  }

  get hasWon() {
    const { guessedLetters, secretWord } = this.state;
    return this.isPlaying && [...secretWord].every((letter) => guessedLetters.includes(letter));
  }

  get hasLost() {
    return this.isPlaying && this.livesLeft === 0;
  }

  get isRoundOver() {
    return this.hasWon || this.hasLost;
  }

  get revealedWord() {
    const { guessedLetters, secretWord } = this.state;

    if (!secretWord) {
      return [];
    }

    if (this.isRoundOver) {
      return [...secretWord];
    }

    return [...secretWord].map((letter) => (guessedLetters.includes(letter) ? letter : ''));
  }

  startGame = (event) => {
    event.preventDefault();

    const { secretInput } = this.state;

    if (secretInput.length === 0) {
      this.setState({ setupError: 'Ingresá una palabra secreta.' });
      return;
    }

    if (!VALID_WORD.test(secretInput)) {
      this.setState({ setupError: 'Solo se aceptan letras sin tildes. La ñ está permitida.' });
      return;
    }

    this.setState({
      secretWord: normalizeText(secretInput),
      guessedLetters: [],
      letterInput: '',
      setupError: '',
      roundMessage: '',
    });
  };

  playLetter = (rawLetter) => {
    if (this.isRoundOver) {
      return;
    }

    const { guessedLetters, secretWord } = this.state;
    const normalizedLetter = normalizeText(rawLetter);

    if (!VALID_LETTER.test(rawLetter)) {
      this.setState({ roundMessage: 'Ingresá una sola letra sin tildes.' });
      return;
    }

    if (guessedLetters.includes(normalizedLetter)) {
      this.setState({
        letterInput: '',
        roundMessage: `La letra ${normalizedLetter} ya fue usada.`,
      });
      return;
    }

    const nextGuessedLetters = [...guessedLetters, normalizedLetter];
    const nextLivesLeft = secretWord.includes(normalizedLetter) ? this.livesLeft : this.livesLeft - 1;
    const nextHasWon = [...secretWord].every((letter) => nextGuessedLetters.includes(letter));

    if (nextHasWon) {
      this.setState({
        guessedLetters: nextGuessedLetters,
        letterInput: '',
        roundMessage: 'Ganaste. La palabra fue revelada completa.',
      });
      return;
    }

    if (nextLivesLeft === 0) {
      this.setState({
        guessedLetters: nextGuessedLetters,
        letterInput: '',
        roundMessage: 'Perdiste. La palabra secreta fue revelada.',
      });
      return;
    }

    this.setState({
      guessedLetters: nextGuessedLetters,
      letterInput: '',
      roundMessage: secretWord.includes(normalizedLetter)
        ? `Bien: ${normalizedLetter} está en la palabra.`
        : `No está: ${normalizedLetter}.`,
    });
  };

  submitLetter = (event) => {
    event.preventDefault();
    this.playLetter(this.state.letterInput);
  };

  resetGame = () => {
    this.setState({
      secretInput: '',
      showSecret: false,
      secretWord: '',
      guessedLetters: [],
      letterInput: '',
      setupError: '',
      roundMessage: '',
    });
  };

  renderResultBanner() {
    if (!this.isRoundOver) {
      return null;
    }

    const { secretWord } = this.state;
    const isVictory = this.hasWon;
    const ResultIcon = isVictory ? Trophy : CircleX;

    return (
      <aside
        className={`result-banner ${isVictory ? 'result-banner-win' : 'result-banner-loss'}`}
        role="status"
        aria-live="polite"
        data-testid="game-message"
      >
        <div className="result-icon" aria-hidden="true">
          <ResultIcon size={34} strokeWidth={2.5} />
        </div>
        <div className="result-copy">
          <strong>{isVictory ? 'Victoria' : 'Partida perdida'}</strong>
          <p>
            {isVictory
              ? `Descubriste la palabra completa: ${secretWord}.`
              : `Te quedaste sin vidas. La palabra secreta era ${secretWord}.`}
          </p>
        </div>
      </aside>
    );
  }

  renderSetupForm() {
    const { secretInput, setupError, showSecret } = this.state;

    return (
      <form className="setup-form" onSubmit={this.startGame}>
        <label className="field-label" htmlFor="secret-word">
          Palabra secreta
        </label>
        <div className="input-row">
          <input
            id="secret-word"
            className="text-input"
            type={showSecret ? 'text' : 'password'}
            value={secretInput}
            onChange={(event) => this.setState({ secretInput: event.target.value, setupError: '' })}
            autoComplete="off"
            autoFocus
            data-testid="secret-word-input"
          />
          <button
            className="icon-button"
            type="button"
            aria-label={showSecret ? 'Ocultar palabra' : 'Mostrar palabra'}
            onClick={() => this.setState((state) => ({ showSecret: !state.showSecret }))}
            data-testid="toggle-secret-visibility"
          >
            {showSecret ? <EyeOff aria-hidden="true" size={20} /> : <Eye aria-hidden="true" size={20} />}
          </button>
        </div>
        {setupError && (
          <p className="message error" role="alert" data-testid="secret-error">
            {setupError}
          </p>
        )}
        <button className="primary-button" type="submit" data-testid="start-game-button">
          <Play aria-hidden="true" size={18} />
          Comenzar partida
        </button>
      </form>
    );
  }

  renderGameRound() {
    const { guessedLetters, letterInput, roundMessage } = this.state;

    return (
      <div className="play-layout">
        <section className="drawing-panel" aria-label="Dibujo del ahorcado">
          <HangmanDrawing misses={this.wrongLetters.length} />
        </section>

        <section className="round-panel" aria-label="Partida">
          <div className="status-grid">
            <div>
              <span className="status-label">Vidas</span>
              <strong data-testid="lives-left">{this.livesLeft}</strong>
            </div>
            <div>
              <span className="status-label">Errores</span>
              <strong>{this.wrongLetters.length}</strong>
            </div>
            <div>
              <span className="status-label">Letras usadas</span>
              <strong data-testid="used-letters">{guessedLetters.length > 0 ? guessedLetters.join(' ') : '-'}</strong>
            </div>
          </div>

          <div className="word-slots" aria-label="Palabra oculta" data-testid="hidden-word">
            {this.revealedWord.map((letter, index) => (
              <span className="letter-slot" key={`${letter || 'hidden'}-${index}`}>
                {letter}
              </span>
            ))}
          </div>

          {this.renderResultBanner()}

          <form className="guess-form" onSubmit={this.submitLetter}>
            <label className="field-label" htmlFor="guess-letter">
              Letra
            </label>
            <div className="input-row">
              <input
                id="guess-letter"
                className="text-input letter-input"
                type="text"
                value={letterInput}
                maxLength={1}
                disabled={this.isRoundOver}
                onChange={(event) => this.setState({ letterInput: event.target.value })}
                autoComplete="off"
                autoFocus
                data-testid="guess-input"
              />
              <button
                className="icon-button send-button"
                type="submit"
                aria-label="Probar letra"
                disabled={this.isRoundOver}
                data-testid="guess-button"
              >
                <Send aria-hidden="true" size={20} />
              </button>
            </div>
          </form>

          <div className="alphabet-grid" aria-label="Letras disponibles">
            {ALPHABET.map((letter) => (
              <button
                className="letter-button"
                type="button"
                key={letter}
                disabled={guessedLetters.includes(letter) || this.isRoundOver}
                onClick={() => this.playLetter(letter)}
              >
                {letter}
              </button>
            ))}
          </div>

          {roundMessage && !this.isRoundOver && (
            <p className="message" role="status" data-testid="round-message">
              {roundMessage}
            </p>
          )}
        </section>
      </div>
    );
  }

  render() {
    return (
      <main className="app">
        <section className="game-shell" aria-labelledby="app-title">
          <header className="app-header">
            <div>
              <h1 id="app-title">Ahorcado</h1>
            </div>
            {this.isPlaying && (
              <button
                className="icon-text-button"
                type="button"
                onClick={this.resetGame}
                data-testid="new-game-button"
              >
                <RotateCcw aria-hidden="true" size={18} />
                Nueva partida
              </button>
            )}
          </header>

          {this.isPlaying ? this.renderGameRound() : this.renderSetupForm()}
        </section>
      </main>
    );
  }
}

export default HangmanGame;
