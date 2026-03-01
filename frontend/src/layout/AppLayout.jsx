import Header from "../components/Header";

function AppLayout({ active, onNavigate, children }) {
    return (
        <>
            <Header active={active} onNavigate={onNavigate} />

            <main className="app-main">
                <div className="container py-4">{children}</div>
            </main>
        </>
    );
}

export default AppLayout;