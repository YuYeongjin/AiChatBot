package yyj.project.aichatbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WordMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long wordId;
    private Long listId;


    @Override
        public String toString(){
        return "{\"id\":\"" + id + "\", \"wordId\":\"" + wordId +
                "\", \"listId\":\"" + listId +
                "\"}";
    }




}
