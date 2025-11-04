package at.fhtw.society.backend.player.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gamemaster")
public class GameMaster extends User {
    public void setId(Long id) { super.setId(id); }
}