import { useState } from "react";
import type { PresetRange, TimeRange } from "../../api/userApi";
import styles from './TimeRangeSelector.module.css';

interface Props{
    value: TimeRange;
    onChange: (value: TimeRange) => void;
}

const PRESETS: PresetRange[] = ['day', 'week', 'month', 'year', 'all_time'];
const PRESET_LABELS: Record<PresetRange, string> = {
    day: 'Day',
    week: 'Week',
    month: 'Month',
    year: 'Year',
    all_time: 'All Time',
}
const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export default function TimeRangeSelector({value, onChange}: Props){
    const [showPanel, setShowPanel] = useState(false);
    const [customMode, setCustomMode] = useState<'month' | 'range'>('month');
    const [pickerYear, setPickerYear] = useState(new Date().getFullYear());
    const [fromDate, setFromDate] = useState('');
    const [toDate, setToDate] = useState('');
    
    const handlePreset = (preset: PresetRange) => {
        onChange({
            mode: 'preset',
            preset,
        });
        setShowPanel(false);
    };

    const applyMonth = (month: number) => {
        const from = new Date(pickerYear, month, 1).toISOString();
        const to = new Date(pickerYear, month + 1, 0).toISOString();
        onChange({
            mode: 'custom',
            from,
            to,
        });
        setShowPanel(false);
    };
    
    const applyRange = () => {{
        if(!fromDate) return;
        onChange({
            mode: 'custom',
            from: new Date(fromDate).toISOString(),
            to: toDate ? new Date(toDate + 'T23:59:59Z').toISOString() : new Date().toISOString(),
        });
        setShowPanel(false);
    }};

    const customLabel = () => {
        if (value.mode !== 'custom' || !value.from) return 'Custom';
        const from = new Date(value.from);
        const to = value.to ? new Date(value.to) : new Date();
        const lastOfMonth = new Date(from.getFullYear(), from.getMonth() + 1, 0).getDate();
        if (from.getDate() === 1 && to.getDate() === lastOfMonth) {
            return from.toLocaleDateString('en', { month: 'short', year: 'numeric' });
        }
        return `${from.toLocaleDateString('en', { month: 'short', day: 'numeric' })} – ${to.toLocaleDateString('en', { month: 'short', day: 'numeric' })}`;
    };

    const isCustomActive = value.mode === 'custom';

    return (
        <div className={styles.wrapper}>
            <div className={styles.bar}>
                {PRESETS.map(p => (
                    <button
                        key={p}
                        className={`${styles.presetBtn} ${value.preset === p ? styles.presetBtnActive : ''}`}
                        onClick={() => handlePreset(p)}>
                        {PRESET_LABELS[p]}
                    </button>
                ))}
                <button
                    className={`${styles.btn} ${isCustomActive || showPanel ? styles.btnActive : ''}`}
                    onClick={() => setShowPanel(p => !p)}>
                    {customLabel()} ▾
                </button>
            </div>
            {showPanel && (
                <div className={styles.panel}>
                    <div className={styles.modeTabs}>
                        <button
                            className={`${styles.modeTab} ${customMode === 'month' ? styles.modeTabActive : ''}`}
                            onClick={() => setCustomMode('month')}>
                            By Month
                        </button>
                        <button
                            className={`${styles.modeTab} ${customMode === 'range' ? styles.modeTabActive : ''}`}
                            onClick={() => setCustomMode('range')}>
                            Date Range
                        </button>
                    </div>
                    {customMode === 'month' && (
                        <div className={styles.monthPicker}>
                            <div className={styles.yearRow}>
                                <button className={styles.yearBtn} onClick={() => setPickerYear(y => y - 1)}>‹</button>
                                <span className={styles.yearLabel}>{pickerYear}</span>
                                <button className={styles.yearBtn} onClick={() => setPickerYear(y => y + 1)}>›</button>
                            </div>
                            <div className={styles.monthGrid}>
                                {MONTHS.map((m, i) => (
                                    <button key={m} className={styles.monthBtn} onClick={() => applyMonth(i)}>
                                        {m}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                    {customMode === 'range' && (
                        <div className={styles.rangePicker}>
                            <label className={styles.rangeLabel}>
                                From
                                <input type="date" className={styles.dateInput}
                                    value={fromDate} onChange={e => setFromDate(e.target.value)} />
                            </label>
                            <label className={styles.rangeLabel}>
                                To
                                <input type="date" className={styles.dateInput}
                                    value={toDate} onChange={e => setToDate(e.target.value)} />
                            </label>
                            <button className={styles.applyBtn} onClick={applyRange}>Apply</button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );

}