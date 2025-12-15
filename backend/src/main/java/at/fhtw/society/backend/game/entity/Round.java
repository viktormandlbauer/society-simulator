package at.fhtw.society.backend.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(
        name = "round",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_round_game_number",
                columnNames = {"game_id", "number"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "game_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_round_game")
    )
    private Game game;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dilemma", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> dilemma = new HashMap<>();

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Voting> votes = new HashSet<>();

    public Round(Game game, Integer number) {
        this.game = game;
        this.number = number;
        this.active = true;
        this.dilemma = new HashMap<>();
    }
}
