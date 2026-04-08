import { useQuery } from '@tanstack/react-query';
import { useLayoutEffect, useRef } from 'react';
import { fetchRecentlyPlayed } from '../../api/userApi';
import styles from './RecentlyPlayed.module.css';

interface Track {
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
    const listRef = useRef<HTMLUListElement>(null);

    const { data, isLoading, isError } = useQuery<RecentlyPlayedItem[]>({
        queryKey: ['recently-played'],
        queryFn: fetchRecentlyPlayed,
    });

    useLayoutEffect(() => {
        const el = listRef.current;
        if (!el) return;

        // Derive slot width from the grid: (listWidth - 11 gaps) / 12 + 1 gap
        const cardSlot = (el.clientWidth - 88) / 12 + 8;
        let locked = false;

        const handler = (e: WheelEvent) => {
            e.preventDefault();
            if (locked) return;

            const delta = Math.abs(e.deltaY) >= Math.abs(e.deltaX) ? e.deltaY : e.deltaX;
            const direction = delta > 0 ? 1 : -1;
            const currentIndex = Math.round(el.scrollLeft / cardSlot);
            const targetLeft = Math.max(0, (currentIndex + direction * 2) * cardSlot);

            locked = true;
            el.scrollTo({ left: targetLeft, behavior: 'smooth' });
            setTimeout(() => { locked = false; }, 450);
        };

        el.addEventListener('wheel', handler, { passive: false });
        return () => el.removeEventListener('wheel', handler);
    }, [data]);

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
                <ul ref={listRef} className={styles.list}>
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