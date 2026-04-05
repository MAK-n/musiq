import styles from './LandingPage.module.css';
import { handleLogin } from '../components/Navbar';
const features = [
    {
        icon: '🎵',
        title: 'Top Tracks',
        desc: 'See what you\'ve been playing most.'
    },
    {
        icon: '🎤',
        title: 'Top Artists',
        desc: 'Discover your favourite artists.'
    },
    {
        icon: '🕓',
        title: 'Recent Plays',
        desc: 'Relive your listening history.'
    }
];
export default function LandingPage() {
    return (
        <div className={styles.page}>
            <div className={styles.hero}>
                <h1 className={styles.title}>Musiq</h1>
                <p className={styles.subtitle}>
                    Your personal Spotify stats hub. See your top tracks, favourite artists,
                    and full listening history — all in one place.
                </p>
            </div>
            <div className={styles.features}>
                {features.map(f => (
                    <div key={f.title} className={styles.card}>
                        <div className={styles.cardIcon}>{f.icon}</div>
                        <p className={styles.cardTitle}>{f.title}</p>
                        <p className={styles.cardDesc}>{f.desc}</p>
                    </div>
                ))}
            </div>
            <button className={styles.cta} onClick={handleLogin}>
                Connect with Spotify
            </button>
        </div>
    );
}