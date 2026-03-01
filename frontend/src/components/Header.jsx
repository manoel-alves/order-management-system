function Header({ active, onNavigate }) {
    const items = [
        { key: "orders", label: "Pedidos" },
        { key: "customers", label: "Clientes" },
        { key: "products", label: "Produtos" },
    ];

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark py-3 sticky-top">
            <div className="container">
                <span className="navbar-brand fw-bold fs-4">Gerenciador de pedidos</span>

                <div className="collapse navbar-collapse">
                    <ul className="navbar-nav ms-3">
                        {items.map((item) => (
                            <li className="nav-item" key={item.key}>
                                <button
                                    type="button"
                                    className={`nav-link btn btn-link fs-5 px-3
                                    ${
                                        active === item.key ? "active" : ""
                                    }`}
                                    onClick={() => onNavigate(item.key)}
                                >
                                    {item.label}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;