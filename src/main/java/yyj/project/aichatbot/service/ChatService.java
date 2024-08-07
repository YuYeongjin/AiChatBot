package yyj.project.aichatbot.service;

import org.springframework.stereotype.Service;

import java.util.Map;


public interface ChatService {
    Map<String,String> chatMassage(Map<String, String> req);
}
