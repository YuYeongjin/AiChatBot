package yyj.project.aichatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yyj.project.aichatbot.model.Chat;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    Chat findByMessage(String message);
}
