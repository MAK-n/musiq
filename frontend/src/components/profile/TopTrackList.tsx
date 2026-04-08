import { useQuery } from "@tanstack/react-query";
import { fetchTopTracks, type TimeRange, timeRangeKey } from "../../api/userApi";
import styles from './TopTrackList.module.css';

interface Artist { name: string; }
interface Track { spotifyId: string; name: string; imageUrl: string; albumName: string; artists: Artist[]; playCount: number; }
interface Props { timeRange: TimeRange; }

export default function TopTrackList({ timeRange }: Props) {
    const { data, isLoading, isError } = useQuery<Track[]>({
        queryKey: ['top-tracks', timeRangeKey(timeRange)],
        queryFn: () => fetchTopTracks(timeRange),
    });

    return (
        <div className={styles.card}>
            <div className={styles.header}>
                <h3 className={styles.title}>Top Tracks</h3>
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
                                <span className={styles.playCount}>
                                    {(track.playCount ?? 0)} {(track.playCount ?? 0) === 1 ? 'play' : 'plays'}
                                </span>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}