package yyj.project.aichatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String mean;


    public Word() {}
    public Word(Long id, String word, String mean) {
        this.id = id;
        this.word = word;
        this.mean = mean;
    }

    @Override
        public String toString(){
        return "{\"id\":\"" + id + "\", \"word\":\"" + word +
                "\", \"mean\":\"" + mean +
                "\"}";
    }




}
