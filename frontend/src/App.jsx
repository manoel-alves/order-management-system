import {useState} from "react";
import AppLayout from "./layout/AppLayout.jsx";
import Customers from "./pages/Customers.jsx";
import Products from "./pages/Products.jsx";

function App() {
    const [page, setPage] = useState('orders');

    const renderPage = () => {
        switch (page) {
            case "orders":
                return <Page title="Pedidos" />;
            case "customers":
                return <Customers/>
            case "products":
                return <Products/>;
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
