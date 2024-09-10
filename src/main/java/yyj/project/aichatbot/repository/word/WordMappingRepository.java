package yyj.project.aichatbot.repository.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.model.WordMapping;

import java.util.List;

public interface WordMappingRepository extends JpaRepository<WordMapping,Long> {


    @Query("SELECT m FROM WordMapping m WHERE m.listId = :listId ORDER BY m.id ASC")
    List<WordMapping> getListById(Long listId);
}
