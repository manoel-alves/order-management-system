import {useState} from "react";

export default function ProductSearch({ loadingList, searchByDescription }) {

    const [search, setSearch] = useState("");

    return (
        <div className="card w-100 h-100">
            <div className="card-body">
                <h5 className="card-title">Buscar</h5>

                <div className="d-flex flex-column">
                    <div className="row g-3">
                        <div className="col-md-10">
                            <input
                                className="form-control"
                                placeholder="Ex.: Notebook"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                            />
                        </div>

                        <div className="col-md-2 d-grid">
                            <button
                                className="btn btn-outline-primary"
                                disabled={loadingList}
                                onClick={() => {
                                    void searchByDescription(search)
                                    setSearch("");
                                }}
                            >
                                {loadingList ? "Buscando..." : "Buscar"}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}