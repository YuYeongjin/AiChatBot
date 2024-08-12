package yyj.project.aichatbot.repository.word;

import org.springframework.data.jpa.repository.JpaRepository;
import yyj.project.aichatbot.model.Word;

public interface WordRepository extends JpaRepository<Word,Long> {

    Word findWordByMean(String mean);

    String deleteByWord(String word);
    String deleteByMean(String mean);

}
