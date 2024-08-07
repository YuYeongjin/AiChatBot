package yyj.project.aichatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yyj.project.aichatbot.service.ChatService;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatController {

    @Autowired
    public ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<?> chatMessage(@RequestBody Map<String,String> req){
        return ResponseEntity.ok( chatService.chatMassage(req));
    }


}
