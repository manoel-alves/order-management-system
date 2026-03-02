import { useMemo, useState } from "react";
import { datetimeLocalToIso } from "../../utils/datetime.js";

function formatPrice(value) {
    const n = Number(String(value ?? 0).replace(",", "."));
    if (Number.isNaN(n)) return "R$ 0,00";
    return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(n);
}

export default function OrderSearch({
                                        loading,
                                        onListAll,
                                        onFindById,
                                        onByCustomer,
                                        onByProduct,
                                        onByPeriod,
                                        onTotalByCustomer,
                                        totalByCustomer,
                                        customers,
                                        products,
                                        loadingOptions,
                                    }) {
    const [orderId, setOrderId] = useState("");
    const [customerId, setCustomerId] = useState("");
    const [productId, setProductId] = useState("");
    const [totalCustomerId, setTotalCustomerId] = useState("");

    const [startLocal, setStartLocal] = useState("");
    const [endLocal, setEndLocal] = useState("");

    const [collapsed, setCollapsed] = useState(false);
    const [periodError, setPeriodError] = useState("");

    const startIso = useMemo(() => datetimeLocalToIso(startLocal), [startLocal]);
    const endIso = useMemo(() => datetimeLocalToIso(endLocal), [endLocal]);

    const periodValidation = useMemo(() => {
        if (!startLocal || !endLocal) return { ok: false, message: "" };
        if (!startIso || !endIso) return { ok: false, message: "Data/hora inválida." };

        const startMs = new Date(startIso).getTime();
        const endMs = new Date(endIso).getTime();
        if (Number.isNaN(startMs) || Number.isNaN(endMs)) return { ok: false, message: "Data/hora inválida." };
        if (startMs > endMs) return { ok: false, message: "O início não pode ser maior que o fim." };
        return { ok: true, message: "" };
    }, [startLocal, endLocal, startIso, endIso]);

    const resetFilters = () => {
        setOrderId("");
        setCustomerId("");
        setProductId("");
        setStartLocal("");
        setEndLocal("");
        setPeriodError("");
        setTotalCustomerId("");
    };

    return (
        <div className="card w-100 ui-card">
            <div className="card-body">
                <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-3">
                    <div>
                        <h5 className="card-title mb-0">Filtros</h5>
                        <div className="small text-muted">Refine a lista de pedidos usando um ou mais critérios.</div>
                    </div>

                    <div className="d-flex gap-2">
                        <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => setCollapsed((v) => !v)}
                        >
                            {collapsed ? "Mostrar" : "Ocultar"}
                        </button>

                        <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            disabled={loading}
                            onClick={() => {
                                resetFilters();
                                void onListAll();
                            }}
                        >
                            Limpar
                        </button>

                        <button
                            type="button"
                            className="btn btn-sm btn-outline-primary"
                            disabled={loading}
                            onClick={() => void onListAll()}
                        >
                            Listar todos
                        </button>
                    </div>
                </div>

                {!collapsed && (
                    <>
                        {/* Filtros rápidos */}
                        <div className="row g-2">
                            <div className="col-lg-3">
                                <label className="form-label mb-1">ID do pedido</label>
                                <div className="input-group">
                                    <input
                                        className="form-control"
                                        placeholder="Ex.: 12"
                                        type="number"
                                        min="0"
                                        value={orderId}
                                        onChange={(e) => setOrderId(e.target.value)}
                                    />
                                    <button
                                        type="button"
                                        className="btn btn-outline-primary"
                                        disabled={loading || !orderId}
                                        onClick={() => void onFindById(orderId)}
                                    >
                                        Buscar
                                    </button>
                                </div>
                            </div>

                            <div className="col-lg-4">
                                <label className="form-label mb-1">Por cliente</label>
                                <div className="input-group">
                                    <select
                                        className="form-select"
                                        value={customerId}
                                        onChange={(e) => setCustomerId(e.target.value)}
                                        disabled={loadingOptions}
                                    >
                                        <option value="">{loadingOptions ? "Carregando..." : "Selecione"}</option>
                                        {customers.map((c) => (
                                            <option key={c.id} value={c.id}>
                                                {c.name}
                                            </option>
                                        ))}
                                    </select>
                                    <button
                                        type="button"
                                        className="btn btn-outline-primary"
                                        disabled={loading || !customerId}
                                        onClick={() => void onByCustomer(customerId)}
                                    >
                                        Buscar
                                    </button>
                                </div>
                            </div>

                            <div className="col-lg-5">
                                <label className="form-label mb-1">Contendo produto</label>
                                <div className="input-group">
                                    <select
                                        className="form-select"
                                        value={productId}
                                        onChange={(e) => setProductId(e.target.value)}
                                        disabled={loadingOptions}
                                    >
                                        <option value="">{loadingOptions ? "Carregando..." : "Selecione"}</option>
                                        {products.map((p) => (
                                            <option key={p.id} value={p.id}>
                                                {p.description} (#{p.id})
                                            </option>
                                        ))}
                                    </select>
                                    <button
                                        type="button"
                                        className="btn btn-outline-primary"
                                        disabled={loading || !productId}
                                        onClick={() => void onByProduct(productId)}
                                    >
                                        Buscar
                                    </button>
                                </div>
                            </div>
                        </div>

                        <hr className="my-3" />

                        {/* Período + Total */}
                        <div className="row g-2 align-items-end">
                            <div className="col-lg-6">
                                <div className="d-flex justify-content-between align-items-center">
                                    <label className="form-label mb-1">Por período</label>
                                    {(periodError || periodValidation.message) && (
                                        <span className="small text-danger">{periodError || periodValidation.message}</span>
                                    )}
                                </div>

                                <div className="row g-2">
                                    <div className="col-6">
                                        <input
                                            type="datetime-local"
                                            className="form-control"
                                            value={startLocal}
                                            onChange={(e) => {
                                                setStartLocal(e.target.value);
                                                setPeriodError("");
                                            }}
                                        />
                                    </div>
                                    <div className="col-6">
                                        <input
                                            type="datetime-local"
                                            className="form-control"
                                            value={endLocal}
                                            onChange={(e) => {
                                                setEndLocal(e.target.value);
                                                setPeriodError("");
                                            }}
                                        />
                                    </div>
                                    <div className="col-12 d-grid">
                                        <button
                                            type="button"
                                            className="btn btn-outline-primary"
                                            disabled={loading || !startLocal || !endLocal || !periodValidation.ok}
                                            onClick={() => {
                                                if (!periodValidation.ok) {
                                                    setPeriodError(periodValidation.message || "Período inválido.");
                                                    return;
                                                }
                                                void onByPeriod(startIso, endIso);
                                            }}
                                        >
                                            Buscar no período
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div className="col-lg-6">
                                <label className="form-label mb-1">Total de pedidos por cliente</label>
                                <div className="input-group">
                                    <select
                                        className="form-select"
                                        value={totalCustomerId}
                                        onChange={(e) => setTotalCustomerId(e.target.value)}
                                        disabled={loadingOptions}
                                    >
                                        <option value="">{loadingOptions ? "Carregando..." : "Selecione"}</option>
                                        {customers.map((c) => (
                                            <option key={c.id} value={c.id}>
                                                {c.name} (#{c.id})
                                            </option>
                                        ))}
                                    </select>

                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        disabled={loading || !totalCustomerId}
                                        onClick={() => void onTotalByCustomer(totalCustomerId)}
                                    >
                                        Consultar
                                    </button>
                                </div>

                                <div className="small text-muted mt-2">
                                    {totalByCustomer != null ? (
                                        <>
                                            Total: <strong>{formatPrice(totalByCustomer)}</strong>
                                        </>
                                    ) : (
                                        "—"
                                    )}
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}