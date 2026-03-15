import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav>
            <span onClick={() => navigate('/')} style={{
                fontSize: '24px',
                fontWeight: 'bold',
                cursor: 'pointer'}}>
                Musiq
            </span>

            <div>
                {isAuthenticated 
                    ? <button onClick = {handleLogout}>Logout</button>
                    : <button onClick = {() => navigate('/')}>Login</button>
                }
            </div>
                
        </nav>
    );
}