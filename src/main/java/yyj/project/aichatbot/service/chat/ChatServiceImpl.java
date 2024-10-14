package yyj.project.aichatbot.service.chat;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Chat;
import yyj.project.aichatbot.repository.chat.ChatRepository;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService{
   @Autowired
   private ChatRepository chatRepository;


    @Autowired
    private WordVectors wordVectors;

    @Override
    public Map<String, String> chatMassage(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();
        // 토큰화 및 벡터화 결과 저장
        List<INDArray> wordVectorsList = new ArrayList<>();
        List<Integer> difficulties = new ArrayList<>();
        Map<String, Integer> difficultyMap = new HashMap<>();

        difficultyMap.put("I", 1);
        difficultyMap.put("like", 1);
        difficultyMap.put("programming", 3);
        difficultyMap.put("AI", 3);
        difficultyMap.put("is", 1);
        difficultyMap.put("the", 1);
        difficultyMap.put("future", 4);
        difficultyMap.put("but", 1);
        difficultyMap.put("want", 2);
        difficultyMap.put("money", 2);
        difficultyMap.put("Java", 2);
        difficultyMap.put("great", 2);
        difficultyMap.put("language", 2);



        Chat chat = new Chat();
        chat.setMessage(req.get("chat"));
        chatRepository.save(chat);

        Chat answerChat = chatRepository.findByMessage(req.get("chat"));

        // test -> ai 러닝

        List<String> sentences = new ArrayList<>();
        sentences.add(req.get("chat"));

        // 토큰화 및 벡터화
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        for (String sentence : sentences) {
            List<String> tokens = tokenizerFactory.create(sentence).getTokens();
            for (String token : tokens) {
                if (wordVectors.hasWord(token)) {
                    INDArray wordVector = wordVectors.getWordVectorMatrix(token);
                    wordVectorsList.add(wordVector);
                    difficulties.add(difficultyMap.getOrDefault(token, 1));
                    System.out.println("Word: " + token + " -> Vector: " + wordVector);
                } else {
                    System.out.println("Word: " + token + " not in vocabulary");
                }
            }
        }
        // 입력 벡터와 레이블을 INDArray로 변환
        INDArray input = Nd4j.vstack(wordVectorsList);
        // 원-핫 인코딩을 위해 난이도 리스트를 변환
        INDArray labels = Nd4j.zeros(difficulties.size(), 5); // 5는 난이도 범위
        for (int i = 0; i < difficulties.size(); i++) {
            labels.putScalar(new int[]{i, difficulties.get(i) - 1}, 1.0); // 0부터 시작하므로 -1
        }

        // 데이터셋 생성
        DataSet dataSet = new DataSet(input, labels);
        DataSetIterator iterator = new ListDataSetIterator(dataSet.asList(), 10); // 배치 크기 10으로 설정

        // 모델 구성
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123) // 랜덤 시드
                .updater(new org.nd4j.linalg.learning.config.Adam(0.01)) // 옵티마이저
                .list()
                .layer(0, new DenseLayer.Builder().nIn(input.size(1)).nOut(10).activation(Activation.RELU).build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(10).nOut(5).activation(Activation.SOFTMAX).build())
                .build();

        // 모델 생성 및 학습
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100)); // 학습 진행 상황 출력
        model.fit(iterator); // 모델 학습

        // 예측하기: 특정 단어에 대한 난이도 예측
        INDArray averageWordVector = Nd4j.mean(input, 0).reshape(1, input.size(1)); // 입력 단어 벡터의 평균을 2D로 변환
        INDArray predictedDifficulty = model.output(averageWordVector);
        int predictedIndex = Nd4j.argMax(predictedDifficulty, 1).getInt(0) + 1;
        String resultString = "Predicted difficulty for " + req.get("chat") + ": " + predictedIndex;
        System.out.println(resultString);

        answerChat.setAnswer(resultString);
        chatRepository.save(answerChat);

        result.put("text",resultString);

        return result;
    }
}
