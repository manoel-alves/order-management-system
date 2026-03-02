import { useCallback, useEffect, useRef, useState } from "react";
import {
    createProduct,
    getProductById,
    listProducts,
    searchProductsByDescription,
} from "../../api/products.js";

export function useProducts() {
    const [items, setItems] = useState([]);
    const [selectedProduct, setSelectedProduct] = useState(null);

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
            const data = await listProducts();

            if (requestId !== requestIdRef.current) return;

            setItems(Array.isArray(data) ? data : []);
        } catch (e) {
            if (requestId !== requestIdRef.current) return;

            setItems([]);
            setError(e?.message ?? "Falha ao listar produtos.");
        } finally {
            if (requestId === requestIdRef.current) {
                setLoadingList(false);
            }
        }
    }, [clearMessages]);

    const searchByDescription = useCallback(
        async (description) => {
            const requestId = ++requestIdRef.current;

            clearMessages();

            const trimmed = (description ?? "").trim();
            if (trimmed.length === 0) {
                setItems([]);
                setSelectedProduct(null);
                return;
            }

            setLoadingList(true);

            try {
                const data = await searchProductsByDescription(trimmed);

                if (requestId !== requestIdRef.current) return;

                setItems(Array.isArray(data) ? data : []);
                setSelectedProduct(null);
            } catch (e) {
                if (requestId !== requestIdRef.current) return;

                setItems([]);
                setSelectedProduct(null);
                setError(e?.message ?? "Falha ao buscar produtos.");
            } finally {
                if (requestId === requestIdRef.current) {
                    setLoadingList(false);
                }
            }
        },
        [clearMessages]
    );

    const createOne = useCallback(
        async ({ description, price, stockQuantity }) => {
            clearMessages();
            setLoadingCreate(true);

            try {
                const created = await createProduct({
                    description,
                    price,
                    stockQuantity,
                });

                setSuccess("Produto criado com sucesso.");
                setSelectedProduct(created ?? null);

                await refreshAll();
            } catch (e) {
                setError(e?.message ?? "Falha ao criar produto.");
            } finally {
                setLoadingCreate(false);
            }
        },
        [clearMessages, refreshAll]
    );

    const selectById = useCallback(
        async (id) => {
            const requestId = ++requestIdRef.current;

            clearMessages();

            try {
                const data = await getProductById(id);

                if (data) setItems([data]);
                else setItems([]);
                setSelectedProduct(data ?? null);

                if (requestId !== requestIdRef.current) return;

                setSelectedProduct(data ?? null);
            } catch (e) {
                if (requestId !== requestIdRef.current) return;

                setItems([]);
                setSelectedProduct(null);
                setError(e?.message ?? "Falha ao carregar detalhes do produto.");
            }
        },
        [clearMessages]
    );

    const clearDetails = useCallback(() => {
        setSelectedProduct(null);
    }, []);

    useEffect(() => {
        void refreshAll();
    }, [refreshAll]);

    return {
        items,
        selectedProduct,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        searchByDescription,
        createOne,
        selectById,
        clearDetails,
    };
}