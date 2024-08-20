package yyj.project.aichatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class WordList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Word> word;

    private String name;
    private LocalDateTime createdAt;

    @Override
        public String toString(){
       return "WordList => id : " + id + " / name : " + name+ " / word : " + word + " / createdAt : " + createdAt;   }
}
