import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { fetchTopTracks } from "../../api/userApi";
import styles from './TopTrackList.module.css';

interface Artist {
    name: string;
}

interface Track{
    spotifyId: string;
    name: string;
    imageUrl: string;
    albumName: string;
    artists: Artist[];
}

const RANGES = ['short_term', 'medium_term', 'long_term'];

const LABELS: Record<string, string> = {
    short_term: 'Short Term',
    medium_term: 'Medium Term',
    long_term: 'Long Term',
};

export default function TopTrackList(){
    const [range, setRange] = useState('medium_term');

    const {data, isLoading, isError} = useQuery<Track[]>({
        queryKey: ['top-tracks', range],
        queryFn: () => fetchTopTracks(range),
    });

    return (
        <div className={styles.card}>
            <div className={styles.header}>
                <h3 className={styles.title}>Top Tracks</h3>
                <div className={styles.tabs}>
                    {RANGES.map(r => (
                        <button
                            key={r}
                            className={`${styles.tab} ${range === r ? styles.tabActive : ''}`}
                            onClick={() => setRange(r)}>
                            {LABELS[r]}
                        </button>
                    ))}
                </div>
            </div>
            {isLoading && (
                <ul className={styles.list}>
                    {Array.from({ length: 6 }).map((_, i) => (
                        <li key={i} className={styles.skeletonRow}>
                            <div className={styles.skeletonThumb} />
                            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6 }}>
                                <div className={styles.skeletonLine} style={{ width: '60%' }} />
                                <div className={styles.skeletonLine} style={{ width: '40%' }} />
                            </div>
                        </li>
                    ))}
                </ul>
            )}
            {isError && <p className={styles.error}>Failed to load tracks.</p>}
            {data && (
                <ul className={styles.list}>
                    {data.map((track, i) => (
                        <li key={track.spotifyId} className={styles.item}>
                            <span className={styles.rank}>{i + 1}</span>
                            <img src={track.imageUrl} alt={track.name} className={styles.thumbnail} />
                            <div className={styles.info}>
                                <p className={styles.trackName}>{track.name}</p>
                                <p className={styles.artistName}>{track.artists.map(a => a.name).join(', ')}</p>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );

}