package yyj.project.aichatbot.repository.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yyj.project.aichatbot.model.Word;
import yyj.project.aichatbot.model.WordList;

public interface WordListRepository extends JpaRepository<WordList,Long> {


}
