package yyj.project.aichatbot.service.word;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.model.WordList;
import yyj.project.aichatbot.model.WordMapping;
import yyj.project.aichatbot.repository.word.WordListRepository;
import yyj.project.aichatbot.repository.word.WordMappingRepository;
import yyj.project.aichatbot.repository.word.WordRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl implements WordService {
   @Autowired
   private WordRepository wordRepository;
    @Autowired
    private WordListRepository wordListRepository;
    @Autowired
    private WordMappingRepository wordMappingRepository;
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
//            wordLists = (List<Word>) req.get("ownList");
            String name = (String) req.get("name");
            List<Map<String,Object>> lists = new ArrayList<>();
            lists= (List<Map<String,Object>>) req.get("ownList");
            WordList wordList = new WordList();
            wordList.setName(name);
            wordList.setCreatedAt(LocalDateTime.now());
            wordListRepository.save(wordList);

            int listId = wordListRepository.findAll().size();
            for(Map<String,Object> item : lists){
                WordMapping wordMapping = new WordMapping();
                wordMapping.setWordId(Long.valueOf(item.get("id").toString()));
                wordMapping.setListId((long) listId);
                wordMappingRepository.save(wordMapping);
            }

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
 /// TODO ++++ 파싱하기

    @Override
    public Map<String, Object> loadSaveList(Map<String, Object> req) {
        Map<String, Object> result = new HashMap<>();
        List<Word> wordRes = new ArrayList<>();
        System.out.println("  req.get(\"list\"): " +  req.get("list"));
         System.out.println(" req.get(\"list\") class : " +  req.get("list").getClass().toString());
        WordList wordList = wordListRepository.getReferenceById(Long.valueOf(req.get("list").toString()));

        List<WordMapping> mappingList = new ArrayList<>();
        mappingList = wordMappingRepository.getListById(Long.valueOf(req.get("list").toString()));

        // TODO@@@@@@@@@@@@
        // mappingList foreach -> wordRes add

        System.out.println(wordRes);


        result.put("wordLists", wordRes);
        return result;
    }

}
