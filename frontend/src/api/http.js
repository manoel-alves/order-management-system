const DEFAULT_HEADERS = {
    "Content-Type": "application/json",
    "Accept": "application/json",
};

export async function apiFetch(path, options = {}) {
    const config = {
        method: options.method ?? "GET",
        headers: {
            ...DEFAULT_HEADERS,
            ...(options.headers ?? {}),
        },
        body: options.body,
    };

    const response = await fetch(path, config);

    const contentType = response.headers.get("content-type") ?? "";
    const isJson = contentType.includes("application/json");

    const payload = isJson ? await response.json().catch(() => null) : await response.text().catch(() => null);

    if (!response.ok) {
        const message =
            (payload && typeof payload === "object" && payload.message) ? payload.message
                : (typeof payload === "string" && payload.trim().length > 0) ? payload
                    : `Erro HTTP ${response.status}`;

        const error = new Error(message);
        error.status = response.status;
        error.payload = payload;
        throw error;
    }

    return payload;
}