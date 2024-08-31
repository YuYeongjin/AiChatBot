package yyj.project.aichatbot.service.word;

import yyj.project.aichatbot.model.Word;

import java.util.List;
import java.util.Map;


public interface WordService {

    Map<String,String> insertWord(Map<String, String> req);

    Map<String,Object> totalWord();

    Map<String,String> deleteWord(Map<String, String> req);

    Map<String,String> insertList(Map<String, List<Map<String, String>>> req);
    
    Map<String,String> updateWord(Map<String, String> req);

    Map<String,Object> createProblem(Map<String, String> req);

    Map<String,Object> saveList(Map<String, Object> req);

    Map<String,Object> totalWordList();
}
