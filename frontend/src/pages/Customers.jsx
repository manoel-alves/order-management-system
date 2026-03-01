import { useState } from "react";
import { useCustomers } from "./customers/useCustomers";

function formatDate(value) {
    if (!value) return "-";
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "-" : date.toLocaleDateString("pt-BR");
}

export default function Customers() {
    const {
        items,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        searchByName,
        createOne
    } = useCustomers();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [search, setSearch] = useState("");

    return (
        <>
            <div className="mx-2 my-4">
                <h2 className="mb-4">Clientes</h2>

                {/* Mensagens */}
                {error && <div className="alert alert-danger">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                <div className="row">
                    <div className="col-md-6">
                        {/* Cadastro */}
                        <div className="card mb-4">
                            <div className="card-body">
                                <h5 className="card-title">Cadastrar cliente</h5>

                                <form
                                    className="row g-2"
                                    onSubmit={(e) => {
                                        e.preventDefault();
                                        void createOne({ name, email });
                                        setName("");
                                        setEmail("");
                                    }}
                                >
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
                                </form>
                            </div>
                        </div>
                    </div>

                    <div className="col-md-6">
                        {/* Busca */}
                        <div className="card mb-4">
                            <div className="card-body">
                                <h5 className="card-title">Buscar</h5>

                                <div className="row g-2">
                                    <div className="col-md-10">
                                        <input
                                            className="form-control"
                                            placeholder="Ex.: Manoel"
                                            value={search}
                                            onChange={(e) => setSearch(e.target.value)}
                                        />
                                    </div>

                                    <div className="col-md-2 d-grid">
                                        <button
                                            className="btn btn-outline-primary"
                                            disabled={loadingList}
                                            onClick={() => void searchByName(search)}
                                        >
                                            {loadingList ? "Buscando..." : "Buscar"}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Lista */}
                <div className="card mb-4">
                    <div className="card-body">
                        <div className="row g-2">
                            <h5 className="card-title col-sm-10 align-content-center justify-content-center">Lista de clientes</h5>

                            <div className="col-sm-2 mt-2 d-grid">
                                <button
                                    className="btn btn-sm btn-outline-primary"
                                    onClick={() => void refreshAll()}
                                >
                                    Listar todos
                                </button>
                            </div>
                        </div>

                        <hr/>

                        {loadingList ? (
                            <p className="text-muted">Carregando...</p>
                        ) : items.length === 0 ? (
                            <p className="text-muted">Nenhum cliente encontrado.</p>
                        ) : (
                            <div className="list-scroll">
                                <table className="table table-hover  mb-0">
                                    <thead>
                                    <tr>
                                        <th style={{ width: 80 }}>ID</th>
                                        <th>Nome</th>
                                        <th>Email</th>
                                        <th>Data de cadastro</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {items.map((customer) => (
                                        <tr key={customer.id}>
                                            <td>{customer.id}</td>
                                            <td>{customer.name}</td>
                                            <td>{customer.email}</td>
                                            <td>{formatDate(customer.createdAt)}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}