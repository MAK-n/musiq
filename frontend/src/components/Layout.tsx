import Navbar from "./Navbar";

export default function Layout({children} : {children: React.ReactNode}) {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />
            <main style={{ flex: 1 }}>{children}</main>
        </div>
    );
}