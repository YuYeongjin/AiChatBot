package yyj.project.aichatbot.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yyj.project.aichatbot.model.Chat;
import yyj.project.aichatbot.repository.chat.ChatRepository;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService{
   @Autowired
   private ChatRepository chatRepository;


    @Autowired
    private WordVectors wordVectors;

    @Override
    public Map<String, String> chatMassage(Map<String, String> req) {
        Map<String, String> result = new HashMap<>();
        List<INDArray> wordVectorsList = new ArrayList<>();

        // 새로운 단어 생성에 대한 난이도 설정
        Map<String, Integer> difficultyMap = new HashMap<>();
        difficultyMap.put("I", 1);
        difficultyMap.put("like", 1);
        difficultyMap.put("programming", 3);
        difficultyMap.put("AI", 3);
        difficultyMap.put("is", 1);
        difficultyMap.put("the", 1);
        difficultyMap.put("future", 4);
        difficultyMap.put("want", 2);
        difficultyMap.put("Java", 2);
        difficultyMap.put("great", 2);
        difficultyMap.put("language", 2);

        // 입력 메시지 저장
        Chat chat = new Chat();
        chat.setMessage(req.get("chat"));
        chatRepository.save(chat);

        // 예시 문장
        List<String> sentences = List.of(
                "I love programming",
                "Deep learning is a key technology for AI",
                "Java is a great language"
        );
//        List<String> sentences = new ArrayList<>();
//        sentences = Arrays.asList(req.get("chat").split(" "));
        try{

            // init value
            // Word2Vec 벡터 크기 (예: 300차원 벡터)
            int vectorSize = 300;
            // 난이도 레벨의 개수 (0~4의 5개 레벨로 가정)
            int numClasses = 5;
            int batchSize = sentences.size();
            int sequenceLength = sentences.stream().mapToInt(s -> s.split(" ").length).max().orElse(0);
            // 입력 데이터 (배치 크기, 시퀀스 길이, 벡터 크기)
            System.out.println(" input-> batchSize : " + batchSize + " / sequenceLength : " +sequenceLength + " / vectorSize : " + vectorSize );
            INDArray input = Nd4j.zeros(batchSize, sequenceLength, vectorSize);
            // 레이블 (난이도) 3D 배열 (배치 크기, 시퀀스 길이, 클래스 수)
            INDArray labels = Nd4j.zeros(batchSize, numClasses, vectorSize);
//            INDArray labels = Nd4j.zeros(batchSize, sequenceLength, numClasses);
            for (int i = 0; i < sentences.size(); i++) {
                String sentence = sentences.get(i);
                List<String> tokens = List.of(sentence.split(" "));

                for (int j = 0; j < tokens.size(); j++) {
                    String token = tokens.get(j);
    // TODO ->>  "Nd4j.create(data, **new int[]{1, vectorSizeCount}**)"  in Google
                    // Word2Vec 벡터 가져오기
                    if (wordVectors.hasWord(token)) {
                        INDArray wordVector = wordVectors.getWordVectorMatrix(token);
                        System.out.println("input insert info : {NDArrayIndex.point(i) :" + NDArrayIndex.point(i).length()+ " //  NDArrayIndex.point(j) : "+ NDArrayIndex.point(j).length()+ " // wordVector : " + wordVector );
                        input.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.point(j)}, wordVector);
//                        input.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.point(j), NDArrayIndex.all()}, wordVector.reshape(1, vectorSize));
//                        input.put(new INDArrayIndex[]{Nd4j.create(NDArrayIndex.point(i), new int[]{1, vectorSize}));
                    }

                    // 난이도 레이블 설정
                    if (difficultyMap.containsKey(token)) {
                        int difficulty = difficultyMap.get(token);
                        labels.putScalar(new int[]{i, j, difficulty}, 1.0);
                    }
                }
            }

// 모델 구성
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(123) // 랜덤 시드
                    .updater(new Adam(0.01))
                    .weightInit(WeightInit.XAVIER)
                    .list()
                    .layer(0, new LSTM.Builder().nIn(vectorSize).nOut(numClasses).activation(Activation.TANH).build())
                    .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                            .nIn(numClasses).nOut(numClasses).activation(Activation.SOFTMAX).build()) // RNN 출력 레이어
                    .build();

// 모델 생성 및 학습
            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(100)); // 학습 진행 상황 출력

            List<DataSet> dataSetList = new ArrayList<>();
            dataSetList.add(new DataSet(input, labels));
            ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(dataSetList, batchSize);

            model.fit(iterator); // 모델 학습

// 모델 저장
//        try {
//            ModelSerializer.writeModel(model, "word_difficulty_model.zip", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
            // 예측
            List<String> newTokens = List.of(req.get("chat").split(" "));
            INDArray newInput = Nd4j.zeros(1, newTokens.size(), vectorSize);
            INDArray output = model.output(newInput);
            StringBuilder answer = new StringBuilder();
            for (int i = 0; i < newTokens.size(); i++) {
                int predictedClass = Nd4j.argMax(output.get(NDArrayIndex.point(0), NDArrayIndex.point(i)), 1).getInt(0);
                System.out.println("Word: " + newTokens.get(i) + ", Predicted Difficulty: " + predictedClass);
                answer.append("Word: " + newTokens.get(i) + ", Predicted Difficulty: " + predictedClass + "\n");
            }

            Chat answerChat = chatRepository.findByMessage(req.get("chat"));
            answerChat.setAnswer(String.valueOf(answer));
            chatRepository.save(answerChat);

            result.put("text", String.valueOf(answer));
        }catch (Exception e){
            e.printStackTrace();
            result.put("text", e.getMessage());
        }
        return result;
    }
}

/*
  Map<String, String> result = new HashMap<>();
        List<INDArray> wordVectorsList = new ArrayList<>();

        // 새로운 단어 생성에 대한 난이도 설정
        Map<String, Integer> difficultyMap = new HashMap<>();
        difficultyMap.put("I", 1);
        difficultyMap.put("like", 1);
        difficultyMap.put("programming", 3);
        difficultyMap.put("AI", 3);
        difficultyMap.put("is", 1);
        difficultyMap.put("the", 1);
        difficultyMap.put("future", 4);
        difficultyMap.put("want", 2);
        difficultyMap.put("Java", 2);
        difficultyMap.put("great", 2);
        difficultyMap.put("language", 2);

        // 입력 메시지 저장
        Chat chat = new Chat();
        chat.setMessage(req.get("chat"));
        chatRepository.save(chat);

        List<String> sentences = new ArrayList<>();
        sentences = Arrays.asList(req.get("chat").split(" "));

        // 토큰화 및 벡터화
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        for (String sentence : sentences) {
            List<String> tokens = tokenizerFactory.create(sentence).getTokens();
            for (String token : tokens) {
                if (wordVectors.hasWord(token)) {
                    INDArray wordVector = wordVectors.getWordVectorMatrix(token);
                    System.out.println("token wordVector : " +wordVector);
                    wordVectorsList.add(wordVector);
                }
            }
        }
// 입력 벡터와 레이블을 INDArray로 변환
        INDArray input = Nd4j.vstack(wordVectorsList);

// RNN 레이어를 사용하려면 3D 입력이어야 하므로 입력 벡터를 reshape
        INDArray reshapedInput = input.reshape(input.size(0), input.size(1), 5); // 3D로 변환


// 원-핫 인코딩을 위해 난이도 리스트를 변환3
        INDArray labels = Nd4j.zeros(difficultyMap.size(), 5); // 5는 난이도 범위
              int idx = 0;
        for (String key :difficultyMap.keySet()) {
            labels.putScalar(new int[]{idx, difficultyMap.get(key)}, 1.0);
            idx++;
        }
        labels = labels.reshape(labels.size(0), 1, labels.size(1));

// 데이터셋 생성
        DataSet dataSet = new DataSet(reshapedInput, labels);
        DataSetIterator iterator = new ListDataSetIterator(dataSet.asList(), 10); // 배치 크기 10으로 설정

// 모델 구성
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123) // 랜덤 시드
                .updater(new org.nd4j.linalg.learning.config.Adam(0.01)) // 옵티마이저
                .list()
                .layer(0, new LSTM.Builder().nIn(reshapedInput.size(1)).nOut(100).activation(Activation.TANH).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .nIn(100).nOut(5).activation(Activation.SOFTMAX).build()) // RNN 출력 레이어
                .build();

// 모델 생성 및 학습
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100)); // 학습 진행 상황 출력
        model.fit(iterator); // 모델 학습

        // 예측된 단어들을 바탕으로 새로운 문장 생성
        StringBuilder generatedSentence = new StringBuilder();
        String startWord = "I"; // 임의의 시작 단어
        generatedSentence.append(startWord);

        for (int i = 0; i < 20; i++) { // 최대 20단어 생성
            INDArray inputWordVector = wordVectors.getWordVectorMatrix(startWord).reshape(1, input.size(1));
            INDArray output = model.rnnTimeStep(inputWordVector);
            int predictedWordIdx = Nd4j.argMax(output, 1).getInt(0);
            String nextWord = wordVectors.vocab().wordAtIndex(predictedWordIdx);
            generatedSentence.append(" ").append(nextWord);
            startWord = nextWord;

            // 중간에 예측된 단어가 없을 경우 멈추기
            if (!wordVectors.hasWord(nextWord)) {
                break;
            }
        }

        String generatedText = generatedSentence.toString();
        Chat answerChat = chatRepository.findByMessage(req.get("chat"));
        answerChat.setAnswer(generatedText);
        chatRepository.save(answerChat);

        result.put("text", generatedText);

        return result;
 */