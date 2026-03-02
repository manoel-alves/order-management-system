import { apiFetch } from "./http.js";

export function listOrders() {
    return apiFetch("/api/orders");
}

export function getOrderById(id) {
    return apiFetch(`/api/orders/${id}`);
}

export function listOrdersByCustomer(customerId) {
    return apiFetch(`/api/orders?customerId=${encodeURIComponent(customerId)}`);
}

export function listOrdersByProduct(productId) {
    return apiFetch(`/api/orders?productId=${encodeURIComponent(productId)}`);
}

export function listOrdersByPeriod(startIso, endIso) {
    const start = encodeURIComponent(startIso);
    const end = encodeURIComponent(endIso);
    return apiFetch(`/api/orders/by-period?start=${start}&end=${end}`);
}

export function getTotalByCustomer(customerId) {
    return apiFetch(`/api/orders/total?customerId=${encodeURIComponent(customerId)}`);
}

export function createOrder(payload) {
    return apiFetch("/api/orders", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}