import { useQuery } from '@tanstack/react-query';
import { fetchRecentlyPlayed } from '../../api/userApi';
import styles from './RecentlyPlayed.module.css';

interface Track {
    spotifyId: string;
    name: string;
    imageUrl: string;
    artists: { name: string }[];
}
interface RecentlyPlayedItem {
    track: Track;
    playedAt: string;
}

function timeAgo(dateStr: string): string {
    const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
    if (diff < 60)   return `${diff}s ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
}

export default function RecentlyPlayed() {
    const { data, isLoading, isError } = useQuery<RecentlyPlayedItem[]>({
        queryKey: ['recently-played'],
        queryFn: fetchRecentlyPlayed,
    });

    return (
        <div className={styles.card}>
            <h3 className={styles.title}>Recently Played</h3>

            {isLoading && (
                <ul className={styles.list}>
                    {Array.from({ length: 10 }).map((_, i) => (
                        <li key={i} className={styles.skeletonRow}>
                            <div className={styles.skeletonThumb} />
                            <div className={styles.skeletonLine} style={{ width: '70%' }} />
                            <div className={styles.skeletonLine} style={{ width: '50%' }} />
                        </li>
                    ))}
                </ul>
            )}

            {isError && <p className={styles.error}>Failed to load history.</p>}

            {data && (
                <ul className={styles.list}>
                    {data.map((item, i) => (
                        <li key={i} className={styles.item}>
                            <img src={item.track.imageUrl} alt={item.track.name} className={styles.thumbnail} />
                            <div className={styles.info}>
                                <p className={styles.trackName}>{item.track.name}</p>
                                <p className={styles.artistName}>{item.track.artists.map(a => a.name).join(', ')}</p>
                                <span className={styles.timeAgo}>{timeAgo(item.playedAt)}</span>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}