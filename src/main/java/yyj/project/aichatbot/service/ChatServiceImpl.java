package yyj.project.aichatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Chat;
import yyj.project.aichatbot.repository.ChatRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService{
   @Autowired
   private ChatRepository chatRepository;

    @Override
    public Map<String, String> chatMassage(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();

        System.out.println("Welcome!");
        Chat chat = new Chat();
        chat.setMessage(req.get("chat"));
        chatRepository.save(chat);

        Chat answerChat = chatRepository.findByMessage(req.get("chat"));
        answerChat.setAnswer(req.get("chat") + "의 답변은 : " + req.get("chat") +"@@@");

        chatRepository.save(answerChat);

        result.put("text",answerChat.getAnswer());

        return result;
    }
}
