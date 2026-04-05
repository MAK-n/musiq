import { useQuery } from "@tanstack/react-query";
import { fetchProfile } from "../../api/userApi";
import styles from './ProfileHeader.module.css';

interface Profile {
    spotifyId: string;
    displayName: string;
    email: string;
    avatarUrl: string;
}

export default function ProfileHeader(){
    const {data, isLoading, isError} = useQuery({
        queryKey: ['profile'],
        queryFn: fetchProfile,
    });

    if (isLoading) return (
        <div className={styles.card}>
            <div className={styles.skeletonAvatar} />
            <div className={styles.info}>
                <div className={styles.skeletonText} />
                <div className={styles.skeletonTextSm} />
            </div>
        </div>
    );

    if (isError) return <p className={styles.error}>failed to load profile.</p>;

    const profile = data as Profile;

    
    return (
        <div className={styles.card}>
            <img src={profile.avatarUrl} alt={profile.displayName} className={styles.avatar} />
            <div className={styles.info}>
                <h1 className={styles.name}>{profile.displayName}</h1>
                <p className={styles.email}>{profile.email}</p>
                <p className={styles.spotifyId}>{profile.spotifyId}</p>
            </div>
        </div>
    );
}