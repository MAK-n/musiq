import musiqClient from "./musiqClient";

export type PresetRange = 'day' | 'week' | 'month' | 'year' | 'all_time';

export interface TimeRange {
    mode: 'preset' | 'custom';
    preset?: PresetRange;
    from?: string;  // ISO string
    to?: string;    // ISO string
}

const buildParams = (tr: TimeRange) =>
    tr.mode === 'preset'
        ? { range: tr.preset }
        : { from: tr.from, to: tr.to };

export const timeRangeKey = (tr: TimeRange): string =>
    tr.mode === 'preset' ? tr.preset! : `${tr.from}~${tr.to}`;

export const fetchProfile = () => musiqClient.get('/api/me').then(r => r.data);
export const fetchTopTracks = (tr: TimeRange) =>
    musiqClient.get('/api/me/top-tracks', { params: buildParams(tr) }).then(r => r.data);
export const fetchTopArtists = (tr: TimeRange) =>
    musiqClient.get('/api/me/top-artists', { params: buildParams(tr) }).then(r => r.data);
export const fetchRecentlyPlayed = () =>
    musiqClient.get('/api/me/recently-played').then(r => r.data);