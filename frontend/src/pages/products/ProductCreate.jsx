import {useState} from "react";

export default function ProductCreate({ loadingCreate, createOne }) {

    const [description, setDescription] = useState("");
    const [price, setPrice] = useState("");
    const [stockQuantity, setStockQuantity] = useState("");

    return (
        <div className="card w-100 h-100">
            <div className="card-body">
                <h5 className="card-title">Cadastrar produto</h5>

                <form
                    className=""
                    onSubmit={(e) => {
                        e.preventDefault();

                        const normalizedPrice = String(price).replace(",", ".");
                        void createOne({
                            description,
                            price: normalizedPrice,
                            stockQuantity
                        });

                        setDescription("");
                        setPrice("");
                        setStockQuantity("");
                    }}
                >
                    <div className="d-flex flex-column gap-2">
                        <input
                            className="form-control"
                            placeholder="Descrição"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            required
                        />

                        <div className="row g-3">
                            <div className="col-md-4">
                                <input
                                    className="form-control"
                                    placeholder="Preço (R$)"
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="col-md-4">
                                <input
                                    className="form-control"
                                    placeholder="Estoque"
                                    type="number"
                                    min="0"
                                    value={stockQuantity}
                                    onChange={(e) => setStockQuantity(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="col-md-4 d-grid">
                                <button
                                    className="btn btn-primary"
                                    disabled={loadingCreate}
                                    type="submit"
                                >
                                    {loadingCreate ? "Salvando..." : "Cadastrar"}
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}