package yyj.project.aichatbot.component;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class Word2VecConfig {
    @Bean
    public WordVectors wordVectors() {
        return loadWordVectors("src/main/resources/GoogleNews-limited-50000.bin");
    }

    private WordVectors loadWordVectors(String path) {
        try {
            return WordVectorSerializer.loadStaticModel(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
