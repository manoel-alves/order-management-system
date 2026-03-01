import Header from '../components/Header.jsx';

function AppLayout({ active, onNavigate, children }) {
    return (
        <div>
            <Header active={active} onNavigate={onNavigate} />

            <main className="bg-light py-4">
                <div className="container">
                    {children}
                </div>
            </main>
        </div>
    );
}

export default AppLayout;