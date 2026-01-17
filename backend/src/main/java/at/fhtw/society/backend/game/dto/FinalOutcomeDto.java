package at.fhtw.society.backend.game.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class FinalOutcomeDto {
    private String gameId;
    private int totalRounds;
    private String finalSummary;
    private List<RoundSummary> roundSummaries;
    private Map<Integer, Long> totalVotesByChoice;

    @Getter
    @Setter
    @Builder
    public static class RoundSummary {
        private int roundNumber;
        private String dilemmaTitle;
        private Map<Integer, Long> voteCounts;
        private Integer winningChoiceId;
    }
}
