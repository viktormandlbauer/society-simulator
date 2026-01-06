package at.fhtw.society.backend.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class VoteResultDto {
    private int roundNumber;
    private boolean accepted;
    private boolean roundCompleted;
    private Map<Integer, Long> counts;
    
    private DilemmaDto nextDilemma;
}
