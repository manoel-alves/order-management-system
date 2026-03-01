import { apiFetch } from "./http.js";

export function listProducts() {
    return apiFetch("/api/products");
}

export function searchProductsByDescription(description) {
    const query = encodeURIComponent(description);
    return apiFetch(`/api/products?description=${query}`);
}

export function getProductById(id) {
    return apiFetch(`/api/products/${id}`);
}

export function createProduct({ description, price, stockQuantity }) {
    return apiFetch("/api/products", {
        method: "POST",
        body: JSON.stringify({ description, price, stockQuantity }),
    });
}