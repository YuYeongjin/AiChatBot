package yyj.project.aichatbot.service.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Chat;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.repository.chat.ChatRepository;
import yyj.project.aichatbot.repository.word.WordRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordServiceImpl implements WordService {
   @Autowired
   private WordRepository wordRepository;


    @Override
    public Map<String, String> insertWord(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();
        Word word = new Word();

        word.setMean(req.get("mean"));
        word.setWord(req.get("mean"));

        result.put("status", String.valueOf(wordRepository.save(word)));

        return result;
    }

    @Override
    public Map<String, Object> totalWord() {
        Map<String,Object> result = new HashMap<>();

        result.put("wordList",wordRepository.findAll());

        return result;
    }

    @Override
    public Map<String, String> deleteWord(Map<String, String> req) {
        Map<String, String> result = new HashMap<>();

        if(req.get("mean").isEmpty()){
            result.put("status", String.valueOf(wordRepository.deleteByWord(req.get("word"))));
        } else {
            result.put("status", String.valueOf(wordRepository.deleteByMean(req.get("mean"))));
        }

        return result;
    }
}
