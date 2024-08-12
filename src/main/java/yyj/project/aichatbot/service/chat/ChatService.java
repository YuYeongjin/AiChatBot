package yyj.project.aichatbot.service.chat;

import org.springframework.stereotype.Service;

import java.util.Map;


public interface ChatService {
    Map<String,String> chatMassage(Map<String, String> req);
}
