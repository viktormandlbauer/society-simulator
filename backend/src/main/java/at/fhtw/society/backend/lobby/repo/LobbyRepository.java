package at.fhtw.society.backend.lobby.repo;

import at.fhtw.society.backend.lobby.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LobbyRepository extends JpaRepository<Lobby, UUID> {
    /**
     * Custom query to retrieve a list of lobby items with selected fields.
     * This method uses a query to fetch specific columns from the Lobby entity
     * along with related Theme information and counts of members.
     *
     * @return A list of {@link LobbyListItemRow} projections containing lobby details.
     */
    @Query("""
                SELECT
                    l.id AS lobbyId,
                    l.name AS name,
                    t.id AS themeId,
                    t.theme AS themeName,
                    COUNT(DISTINCT m.id) AS playersCount,
                    l.maxPlayers AS maxPlayers,
                    (CASE WHEN l.passwordHash IS NOT NULL AND l.passwordHash <> '' THEN TRUE ELSE FALSE END) AS hasPassword,
                    l.status AS status
                FROM Lobby l
                JOIN l.theme t
                LEFT JOIN l.members m
                GROUP BY
                    l.id, l.name, t.id, t.theme, l.maxPlayers, l.passwordHash, l.status
                ORDER BY 
                    l.name ASC
            """)
    List<LobbyListItemRow> findLobbyList();
}
