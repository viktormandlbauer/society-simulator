package at.fhtw.society.backend.game.entity;

import at.fhtw.society.backend.game.entity.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "voting",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vote_round_player",
                columnNames = {"round_id", "player_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Voting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "round_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_voting_round")
    )
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "player_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_voting_player")
    )
    private Player player;

    @Column(name = "choice_id", nullable = false)
    private Integer choiceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Voting(Round round, Player player, Integer choiceId) {
        this.round = round;
        this.player = player;
        this.choiceId = choiceId;
    }
}
