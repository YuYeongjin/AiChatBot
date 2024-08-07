package yyj.project.aichatbot.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService{
    @Override
    public Map<String, String> chatMassage(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();

        System.out.println("Welcome!");

        result.put("text",req.get("chat"));

        return result;
    }
}
