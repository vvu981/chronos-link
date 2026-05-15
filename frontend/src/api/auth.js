const BASE_URL = import.meta.env.VITE_API_URL;

// ¡Faltaba este 'export' aquí delante!
export const loginRequest = async (credentials) => {
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) throw new Error("Credenciales inválidas");
    return response.json();
};

// Haz lo mismo para el registro si no lo tiene
export const registerRequest = async (userData) => {
    // ... lógica del registro
};