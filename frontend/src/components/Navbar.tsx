import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import musiqClient from "../api/musiqClient";
import styles from './Navbar.module.css';

export const handleLogin = async () => {
    try {
        const response = await musiqClient.get('/auth/spotify/login');
        window.location.href = response.data.redirect_uri;
    } catch (err) {
        console.error('Login failed:', err);
    }
};

export default function Navbar() {
    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className={styles.nav}>

            <span onClick={() => navigate('/')} className={styles.logo}>
                Musiq
            </span>

            <div>
                {isAuthenticated 
                    ? <button onClick = {handleLogout} className={styles.btn}>Logout</button>
                    : <button onClick = {handleLogin} className={styles.btn}>Login</button>
                }
            </div>
                
        </nav>
    );
}