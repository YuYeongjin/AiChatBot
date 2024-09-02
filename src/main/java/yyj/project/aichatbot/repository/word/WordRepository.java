package yyj.project.aichatbot.repository.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yyj.project.aichatbot.model.Word;

public interface WordRepository extends JpaRepository<Word,Long> {

    Word findWordByMean(String mean);

    void deleteByWord(String word);
    void deleteByMean(String mean);
    @Modifying
    @Query("UPDATE Word w SET w.word = :word WHERE w.id = :id")
    void updateWordById(@Param("id") Long id, @Param("word") String word);

    @Modifying
    @Query("UPDATE Word w SET w.mean = :mean WHERE w.id = :id")
    void updateMeanById(@Param("id") Long id, @Param("mean") String mean);

//
//    @Modifying
//    @Query("UPDATE Word w SET w.word = :word WHERE w.id = :id")
//    void updateWordById(@Param("id") Long id, @Param("word") String word);
//
//    @Modifying
//    @Query("UPDATE Word w SET w.mean = :mean WHERE w.id = :id")
//    void updateMeanById(@Param("id") Long id, @Param("mean") String mean);
}
