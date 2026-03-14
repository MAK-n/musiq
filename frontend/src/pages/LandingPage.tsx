import musiqClient from "../api/musiqClient";

export default function LandingPage() {
    const handleLogin = async () => {
        try {
            const response = await musiqClient.get('/auth/spotify/login');
            window.location.href = response.data.redirect_uri;
        } catch (err) {
            console.error('Login failed:', err);
        }
    };
    

    return (
        <div>
            <h1>Musiq</h1>
            <button onClick={handleLogin}>Login with Spotify</button>
        </div>
    );
}