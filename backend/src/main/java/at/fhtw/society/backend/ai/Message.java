package at.fhtw.society.backend.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Message {
     String role;
     String content;

     public Message(String role, String content) {
          this.role = role;
          this.content = content;
     }
}
