package yyj.project.aichatbot.service.word;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.model.WordList;
import yyj.project.aichatbot.repository.word.WordListRepository;
import yyj.project.aichatbot.repository.word.WordRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WordServiceImpl implements WordService {
   @Autowired
   private WordRepository wordRepository;
    @Autowired
    private WordListRepository wordListRepository;

    @Override
    public Map<String, String> insertWord(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();
        Word word = new Word();

        word.setMean(req.get("mean"));
        word.setWord(req.get("word"));

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
    @Transactional
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
        Arrays.fill(check, false);
        int i =0;
        while (i!=amount){
            long random = (long) (Math.random()*count.size());
            if(!check[((int) random)]){
                check[Math.toIntExact(random)] = true;
                wordList.add( wordRepository.findById(random+1).orElse(null));
                i++;
            }
        }
        result.put("wordList",wordList);
        return result;
    }

    @Override
    public Map<String, Object> saveList(Map<String, Object> req) {
        Map<String,Object> result = new HashMap<>();
        try{
            List<Word> wordLists = new ArrayList<>();
            wordLists = (List<Word>) req.get("ownList");
            String name = (String) req.get("name");

            WordList wordList = new WordList();
            wordList.setName(name);
            wordList.setCreatedAt(LocalDateTime.now());
            wordList.setWordList(wordLists.toString());

            wordListRepository.save(wordList);
            result.put("status","1");
        } catch ( Exception e){
            result.put("status","0");
            result.put("error",e.getMessage());

            e.printStackTrace();
        }


        return result;
    }

    @Override
    public Map<String, Object> totalWordList() {
        Map<String,Object> result = new HashMap<>();

        result.put("wordLists",wordListRepository.findAll());
        return result;
    }
 /// TODO ++++

    @Override
    public Map<String, Object> loadSaveList(Map<String, Object> req) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> resultList = new ArrayList<>();

        // req.get("list")가 LinkedHashMap인 경우 처리
        LinkedHashMap wordReq = (LinkedHashMap) req.get("list");

        // wordList를 List<LinkedHashMap<String, Object>>로 캐스팅
        List<LinkedHashMap<String, Object>> wordLists = (List<LinkedHashMap<String, Object>>) wordReq.get("wordList");

        // wordLists를 순회하며 데이터를 추출
        for (LinkedHashMap<String, Object> wordMap : wordLists) {
            Map<String, String> word = new HashMap<>();
            word.put("id", wordMap.get("id").toString()); // id를 String으로 변환
            word.put("word", (String) wordMap.get("word"));
            word.put("mean", (String) wordMap.get("mean"));
            resultList.add(word);
        }

        // 결과 리스트를 result 맵에 추가
        result.put("wordLists", resultList);
        return result;
    }

}
