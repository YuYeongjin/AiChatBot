package yyj.project.aichatbot.controller.word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yyj.project.aichatbot.service.word.WordService;

import java.util.Map;

@RestController
@RequestMapping("/api/word")
public class WordController {

    @Autowired
    private WordService wordService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertWord(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(wordService.insertWord(req));
    }
    @PostMapping("/getAll")
    private ResponseEntity<?> totalWord(){
        return ResponseEntity.ok(wordService.totalWord());
    }

    @PostMapping("/delete")
    private ResponseEntity<?> deleteWord(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(wordService.deleteWord(req));
    }
}