import {useState} from "react";
import AppLayout from "./layout/AppLayout.jsx";

function App() {
    const [page, setPage] = useState('customers');

    const renderPage = () => {
        switch (page) {
            case "customers":
                return <Page title="Clientes" />;
            case "products":
                return <Page title="Produtos" />;
            case "orders":
                return <Page title="Pedidos" />;
            default:
                return null;
        }
    };

    return (
        <AppLayout active={page} onNavigate={setPage}>
            {renderPage()}
        </AppLayout>
    );
}

function Page({ title }) {
    return (
        <div className="card shadow-sm">
            <div className="card-body">
                <h4 className="card-title">{title}</h4>
                <p className="text-muted mb-0">Em implementação.</p>
            </div>
        </div>
    );
}

export default App;
