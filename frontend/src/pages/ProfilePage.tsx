import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import musiqClient from "../api/musiqClient";
import { useNavigate } from "react-router-dom";

interface User {
    id: number;
    displayName: string;
    email: string;
    spotifyId: string;
}

export default function ProfilePage() {
    const { isAuthenticated, isLoading } = useAuth();
    const [user, setUser] = useState<User | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if(isLoading) return;
        if(!isAuthenticated) {
            navigate('/');
            return;
        }
        musiqClient.get('/api/test/me')
        .then((res) => {
            setUser(res.data);
        })
        .catch((err) => {
            console.error(err);
            navigate('/');
        });
    }, [isAuthenticated, isLoading]);

    if(!user) return <div>Loading...</div>;
    
    return (
        <div>
            <h1>Profile</h1>
            <p>Welcome, {user.displayName}</p>
            <p>Email: {user.email}</p>
            <p>Spotify ID: {user.spotifyId}</p>
        </div>
    );
}