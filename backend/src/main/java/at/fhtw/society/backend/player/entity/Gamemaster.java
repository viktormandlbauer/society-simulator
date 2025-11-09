package at.fhtw.society.backend.player.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gamemaster")
@Getter @Setter @NoArgsConstructor
public class Gamemaster extends User {
}