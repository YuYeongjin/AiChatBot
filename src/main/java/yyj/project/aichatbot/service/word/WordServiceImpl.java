package yyj.project.aichatbot.service.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Word;
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
    @Override
    public Map<String, String> updateWord(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();


        try{
            if(req.get("word") != null){
                wordRepository.updateWordById(Long.valueOf(req.get("id")),req.get("word"));
            }
            if (req.get("mean") != null){
                wordRepository.updateMeanById(Long.valueOf(req.get("id")),req.get("mean"));
            }
            result.put("status","1");
        } catch (Exception e){
            result.put("error",e.getMessage());
        }


        return result;
    }

    @Override
    public Map<String, Object> createProblem(Map<String, String> req) {
        Map<String,Object> result = new HashMap<>();
        List<Word> wordList = new ArrayList<>();
        List<Word> count = wordRepository.findAll();
        Boolean[] check = new Boolean[count.size()];
        int amount = Integer.parseInt(req.get("amount"));
        int i =0;
        while (i!=amount){
            long random = (long) (Math.random()*count.size());
            System.out.println(" random :  "+ random);
            if(!check[((int) random)]){
                check[Math.toIntExact(random)] = true;
                wordList.add( wordRepository.findById(random).orElse(null));
                i++;
            }
        }
        result.put("wordList",wordList);
        return result;
    }
}
