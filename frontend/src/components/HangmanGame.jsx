import { Component } from 'react';
import { CircleX, Eye, EyeOff, Play, RotateCcw, Send, Trophy } from 'lucide-react';
import { crearPartida, intentarLetra } from '../api.js';

const MAX_LIVES = 6;
const ALPHABET = 'abcdefghijklmnñopqrstuvwxyz'.split('');

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

const INITIAL_STATE = {
  secretInput: '',
  showSecret: false,
  partida: null,
  letterInput: '',
  setupError: '',
  roundMessage: '',
  loading: false,
};

function roundMessageFor({ resultado, estado }, letter) {
  if (estado === 'GANADA') {
    return 'Ganaste. La palabra fue revelada completa.';
  }

  if (estado === 'PERDIDA') {
    return 'Perdiste. La palabra secreta fue revelada.';
  }

  switch (resultado) {
    case 'ACIERTO':
      return `Bien: ${letter} está en la palabra.`;
    case 'FALLO':
      return `No está: ${letter}.`;
    case 'REPETIDA':
      return `La letra ${letter} ya fue usada.`;
    default:
      return '';
  }
}

class HangmanGame extends Component {
  state = INITIAL_STATE;

  get isPlaying() {
    return this.state.partida !== null;
  }

  get guessedLetters() {
    return this.state.partida?.letrasUsadas ?? [];
  }

  get livesLeft() {
    return this.state.partida?.vidas ?? MAX_LIVES;
  }

  get misses() {
    return MAX_LIVES - this.livesLeft;
  }

  get hasWon() {
    return this.state.partida?.estado === 'GANADA';
  }

  get hasLost() {
    return this.state.partida?.estado === 'PERDIDA';
  }

  get isRoundOver() {
    return this.hasWon || this.hasLost;
  }

  get revealedWord() {
    const { partida } = this.state;

    if (!partida) {
      return [];
    }

    if (this.isRoundOver) {
      return [...partida.palabraSecreta];
    }

    return partida.palabraOculta.split(' ').map((letter) => (letter === '_' ? '' : letter));
  }

  startGame = async (event) => {
    event.preventDefault();

    this.setState({ loading: true, setupError: '' });

    try {
      const partida = await crearPartida(this.state.secretInput);
      this.setState({ partida, letterInput: '', roundMessage: '', loading: false });
    } catch (error) {
      this.setState({ setupError: error.message, loading: false });
    }
  };

  playLetter = async (rawLetter) => {
    const { partida, loading } = this.state;

    if (this.isRoundOver || loading) {
      return;
    }

    this.setState({ loading: true });

    try {
      const nextPartida = await intentarLetra(partida.id, rawLetter);
      const letter = rawLetter.toLocaleLowerCase('es-AR');

      this.setState({
        partida: nextPartida,
        letterInput: '',
        roundMessage: roundMessageFor(nextPartida, letter),
        loading: false,
      });
    } catch (error) {
      this.setState({ roundMessage: error.message, loading: false });
    }
  };

  submitLetter = (event) => {
    event.preventDefault();
    this.playLetter(this.state.letterInput);
  };

  resetGame = () => {
    this.setState(INITIAL_STATE);
  };

  renderResultBanner() {
    if (!this.isRoundOver) {
      return null;
    }

    const { palabraSecreta } = this.state.partida;
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
              ? `Descubriste la palabra completa: ${palabraSecreta}.`
              : `Te quedaste sin vidas. La palabra secreta era ${palabraSecreta}.`}
          </p>
        </div>
      </aside>
    );
  }

  renderSetupForm() {
    const { secretInput, setupError, showSecret, loading } = this.state;

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
        <button className="primary-button" type="submit" disabled={loading} data-testid="start-game-button">
          <Play aria-hidden="true" size={18} />
          Comenzar partida
        </button>
      </form>
    );
  }

  renderGameRound() {
    const { letterInput, roundMessage, loading } = this.state;
    const { guessedLetters } = this;

    return (
      <div className="play-layout">
        <section className="drawing-panel" aria-label="Dibujo del ahorcado">
          <HangmanDrawing misses={this.misses} />
        </section>

        <section className="round-panel" aria-label="Partida">
          <div className="status-grid">
            <div>
              <span className="status-label">Vidas</span>
              <strong data-testid="lives-left">{this.livesLeft}</strong>
            </div>
            <div>
              <span className="status-label">Errores</span>
              <strong>{this.misses}</strong>
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
                disabled={this.isRoundOver || loading}
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
                disabled={guessedLetters.includes(letter) || this.isRoundOver || loading}
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
