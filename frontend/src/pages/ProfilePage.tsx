import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import musiqClient from "../api/musiqClient";
import { useNavigate } from "react-router-dom";
import ProfileHeader from "../components/profile/ProfileHeader";
import TopTrackList from "../components/profile/TopTrackList";
import TopArtistsList from "../components/profile/TopArtistsList";
import RecentlyPlayed from "../components/profile/RecentlyPlayed";
import styles from './ProfilePage.module.css';

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
        <div className={styles.page}>
            <ProfileHeader />
            <div className={styles.panel}>
                <RecentlyPlayed />
                <div className={styles.row}>
                    <TopArtistsList />
                    <TopTrackList />
                </div>
            </div>
        </div>
    );
}