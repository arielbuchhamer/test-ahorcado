const API_URL = import.meta.env.VITE_API_URL ?? '';

async function request(path, body) {
  let response;

  try {
    response = await fetch(`${API_URL}/api${path}`, {
      method: body ? 'POST' : 'GET',
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error('No se pudo conectar con el servidor.');
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.error ?? 'Ocurrió un error inesperado.');
  }

  return data;
}

export function crearPartida(palabra) {
  return request('/partidas', { palabra });
}

export function intentarLetra(id, letra) {
  return request(`/partidas/${id}/letras`, { letra });
}
