import musiqClient from "./musiqClient";

export const fetchProfile = () => musiqClient.get('/api/me').then(r => r.data);
export const fetchTopTracks = (range: string) => musiqClient.get(`/api/me/top-tracks?range=${range}`).then(r => r.data);
export const fetchTopArtists = (range: string) => musiqClient.get(`/api/me/top-artists?range=${range}`).then(r => r.data);
export const fetchRecentlyPlayed = () => musiqClient.get('/api/me/recently-played').then(r => r.data);