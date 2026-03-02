import { useState } from "react";

export default function CustomerSearch({ loading, onListAll, onFindById, onSearchByName }) {
    const [collapsed, setCollapsed] = useState(false);
    const [id, setId] = useState("");
    const [name, setName] = useState("");

    const clear = () => {
        setId("");
        setName("");
    };

    return (
        <div className="card w-100 ui-card">
            <div className="card-body">
                <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-3">
                    <div>
                        <h5 className="card-title mb-0">Filtros</h5>
                        <div className="small text-muted">Busque clientes por identificador ou nome.</div>
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
                                clear();
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
                    <div className="row g-2">
                        <div className="col-lg-4">
                            <label className="form-label mb-1">ID do cliente</label>
                            <div className="input-group">
                                <input
                                    className="form-control"
                                    placeholder="Ex.: 10"
                                    type="number"
                                    min="0"
                                    value={id}
                                    onChange={(e) => setId(e.target.value)}
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-primary"
                                    disabled={loading || !id}
                                    onClick={() => void onFindById(id)}
                                >
                                    Buscar
                                </button>
                            </div>
                        </div>

                        <div className="col-lg-8">
                            <label className="form-label mb-1">Nome</label>
                            <div className="input-group">
                                <input
                                    className="form-control"
                                    placeholder="Ex.: Ana"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-primary"
                                    disabled={loading || !name.trim()}
                                    onClick={() => void onSearchByName(name)}
                                >
                                    Buscar
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}