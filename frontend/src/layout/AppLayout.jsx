import Header from "../components/Header";

function AppLayout({ active, onNavigate, children }) {
    return (
        <>
            <Header active={active} onNavigate={onNavigate} />

            <main className="container py-3">
                {children}
            </main>
        </>
    );
}

export default AppLayout;