import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { fetchTopArtists, type TimeRange } from "../../api/userApi";
import styles from './TopArtistsList.module.css';

interface Artist {
    spotifyId: string;
    name: string;
    imageUrl: string;
}

interface Props{
    timeRange: TimeRange;
}

type GridSize = '2x2' | '4x4';

export default function TopArtistsList({timeRange}: Props) {
    const [gridSize, setGridSize] = useState<GridSize>('4x4');
    const artistCount = gridSize === '2x2' ? 4 : 16;
    const gridClass = gridSize === '2x2' ? styles.grid2x2 : styles.grid4x4;

    const { data, isLoading, isError } = useQuery<Artist[]>({
        queryKey: ['top-artists', timeRange],
        queryFn: () => fetchTopArtists(timeRange),
    });

    return (
        <div className={styles.card}>
            <div className={styles.header}>
                <div className={styles.titleRow}>
                    <h3 className={styles.title}>Top Artists</h3>
                    <div className={styles.gridToggle}>
                        <button className={`${styles.gridBtn} ${gridSize === '2x2' ? styles.gridBtnActive : ''}`}
                            onClick={() => setGridSize('2x2')}>2×2</button>
                        <button className={`${styles.gridBtn} ${gridSize === '4x4' ? styles.gridBtnActive : ''}`}
                            onClick={() => setGridSize('4x4')}>4×4</button>
                    </div>
                </div>
            </div>
            {isLoading && (
                <ul className={gridClass}>
                    {Array.from({ length: artistCount }).map((_, i) => (
                        <li key={i} className={styles.skeletonCard} />
                    ))}
                </ul>
            )}
            {isError && <p className={styles.error}>Failed to load artists.</p>}
            {data && (
                <ul className={gridClass}>
                    {data.slice(0, artistCount).map((artist, i) => (
                        <li key={artist.spotifyId} className={styles.artistCard}>
                            <img src={artist.imageUrl} alt={artist.name} className={styles.artistImage} />
                            <span className={styles.badge}>{i + 1}</span>
                            <span className={styles.artistName}>{artist.name}</span>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}