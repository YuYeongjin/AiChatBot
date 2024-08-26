package yyj.project.aichatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WordList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String wordList;

    private String name;
    private LocalDateTime createdAt;

    @Override
        public String toString(){
       return "WordList => id : " + id + " / name : " + name+ " / wordList : " + wordList + " / createdAt : " + createdAt;   }
}
