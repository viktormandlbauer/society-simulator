package at.fhtw.society.backend.game.dto;

import lombok.Data;

import java.util.List;

@Data
public class DilemmaDto {
    private int id;
    private String title;
    private String context;
    private List<ChoiceDto> choices;
}