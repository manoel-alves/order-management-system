export async function apiFetch(path, options = {}) {
    const response = await fetch(path, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
        ...options,
    });

    const contentType = response.headers.get("content-type");
    let payload = null;

    try {
        if (contentType && contentType.includes("application/json")) {
            payload = await response.json();
        } else {
            payload = await response.text();
        }
    } catch {
        payload = null;
    }

    const emptyPayload =
        payload == null ||
        (typeof payload === "string" && payload.trim().length === 0);

    if (
        [502, 503, 504].includes(response.status) ||
        (response.status >= 500 && emptyPayload)
    ) {
        const error = new Error(
            "Servidor indisponível. Verifique se o backend está rodando e tente novamente."
        );
        error.code = "BACKEND_UNAVAILABLE";
        error.status = response.status;
        throw error;
    }

    if (!response.ok) {
        const message =
            payload && typeof payload === "object" && payload.message
                ? payload.message
                : typeof payload === "string" && payload.trim().length > 0
                    ? payload
                    : `Erro HTTP ${response.status}`;

        const error = new Error(message);
        error.status = response.status;
        error.payload = payload;
        throw error;
    }

    return payload;
}