package com.musiq.listening;

import java.time.Instant;

import com.musiq.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "play_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "track_id", nullable = false)
    private String trackId;

    @Column(name = "played_at", nullable = false)
    private Instant playedAt;
}
