package yyj.project.aichatbot.service.word;

import java.util.Map;


public interface WordService {

    Map<String,String> insertWord(Map<String, String> req);

    Map<String,Object> totalWord();

    Map<String,String> deleteWord(Map<String, String> req);
}
