import {useState} from "react";

export default function CustomerCreate({ loadingCreate, createOne }) {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");

    return (
        <div className="card w-100 h-100">
            <div className="card-body">
                <h5 className="card-title">Cadastrar cliente</h5>

                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        void createOne({ name, email });
                        setName("");
                        setEmail("");
                    }}
                >

                    <div className="row g-3">
                        <div className="col-md-5">
                            <input
                                className="form-control"
                                placeholder="Nome"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="col-md-5">
                            <input
                                className="form-control"
                                placeholder="E-mail"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="col-md-2 d-grid">
                            <button
                                className="btn btn-primary"
                                disabled={loadingCreate}
                                type="submit"
                            >
                                {loadingCreate ? "Salvando..." : "Cadastrar"}
                            </button>
                        </div>

                    </div>



                </form>
            </div>
        </div>
    );
}