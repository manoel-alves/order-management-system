import { useCallback, useEffect, useRef, useState } from "react";
import {
    createOrder,
    getOrderById,
    getTotalByCustomer,
    listOrders,
    listOrdersByCustomer,
    listOrdersByPeriod,
    listOrdersByProduct,
} from "../../api/orders.js";

export function useOrders() {
    const [items, setItems] = useState([]);
    const [totalByCustomer, setTotalByCustomer] = useState(null);

    const [loadingList, setLoadingList] = useState(false);
    const [loadingCreate, setLoadingCreate] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const requestIdRef = useRef(0);

    const clearMessages = useCallback(() => {
        setError("");
        setSuccess("");
    }, []);

    const refreshAll = useCallback(async () => {
        const requestId = ++requestIdRef.current;

        setLoadingList(true);

        try {
            const data = await listOrders();
            if (requestId !== requestIdRef.current) return;

            setItems(Array.isArray(data) ? data : []);
            setTotalByCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao listar pedidos.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const findById = useCallback(async (id) => {
        const requestId = ++requestIdRef.current;
        clearMessages();
        setLoadingList(true);

        try {
            const data = await getOrderById(id);
            if (requestId !== requestIdRef.current) return;

            setItems(data ? [data] : []);
            setTotalByCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao buscar pedido por ID.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const filterByCustomer = useCallback(async (customerId) => {
        const requestId = ++requestIdRef.current;
        clearMessages();
        setLoadingList(true);

        try {
            const data = await listOrdersByCustomer(customerId);
            if (requestId !== requestIdRef.current) return;

            setItems(Array.isArray(data) ? data : []);
            setTotalByCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao buscar pedidos por cliente.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const filterByProduct = useCallback(async (productId) => {
        const requestId = ++requestIdRef.current;
        clearMessages();
        setLoadingList(true);

        try {
            const data = await listOrdersByProduct(productId);
            if (requestId !== requestIdRef.current) return;

            setItems(Array.isArray(data) ? data : []);
            setTotalByCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao buscar pedidos por produto.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const filterByPeriod = useCallback(async (startIso, endIso) => {
        const requestId = ++requestIdRef.current;
        clearMessages();
        setLoadingList(true);

        try {
            const data = await listOrdersByPeriod(startIso, endIso);
            if (requestId !== requestIdRef.current) return;

            setItems(Array.isArray(data) ? data : []);
            setTotalByCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao buscar pedidos por período.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const fetchTotalByCustomer = useCallback(async (customerId) => {
        const requestId = ++requestIdRef.current;
        clearMessages();
        setLoadingList(true);

        try {
            const data = await getTotalByCustomer(customerId);
            if (requestId !== requestIdRef.current) return;

            setTotalByCustomer(data);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setTotalByCustomer(null);
            setError(e?.message ?? "Falha ao consultar total por cliente.");
        } finally {
            if (requestId === requestIdRef.current) setLoadingList(false);
        }
    }, [clearMessages]);

    const createOne = useCallback(
        async ({ customerId, items }) => {
            clearMessages();
            setLoadingCreate(true);

            try {
                await createOrder({ customerId, items });
                setSuccess("Pedido criado com sucesso.");
                await refreshAll();
                return true
            } catch (e) {
                setError(e?.message ?? "Falha ao criar pedido.");
                return false
            } finally {
                setLoadingCreate(false);
            }
        },
        [clearMessages, refreshAll]
    );

    useEffect(() => {
        void refreshAll();
    }, [refreshAll]);

    return {
        items,
        totalByCustomer,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        findById,
        filterByCustomer,
        filterByProduct,
        filterByPeriod,
        fetchTotalByCustomer,
        createOne,
    };
}