package yyj.project.aichatbot.service.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.repository.word.WordRepository;

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

        try{
            wordRepository.deleteById(Long.valueOf(req.get("id")));
            result.put("status", "1");
        } catch (Exception e){
            result.put("status","0");
        }


        return result;
    }

    @Override
    public Map<String, String> insertList(Map<String, List<Map<String, String>>> req) {
        Map<String,String> result = new HashMap<>();

        for(int i = 0; i < req.get("parsedWords").size();i++){
            Word word = new Word();

            word.setMean(req.get("parsedWords").get(i).get("mean"));
            word.setWord(req.get("parsedWords").get(i).get("word"));

            result.put("status", String.valueOf(wordRepository.save(word)));
        }



        return result;
    }
}
