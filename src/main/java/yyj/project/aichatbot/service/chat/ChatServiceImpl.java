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
/*
 1. 영어 문장 입력 시 분석하여 영어 단어로 분해
 2. ai를 꼭 도입하지 않더라도 맞게 대응
 3. 머신러닝을 위해 학습은 시켜볼 것


 */




    @Override
    public Map<String, String> chatMassage(Map<String, String> req) {
        Map<String,String> result = new HashMap<>();
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(req.get("chat"));
        StringBuilder builder = new StringBuilder();

        builder.append("추출된 단어는 ");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            tokens.add(token);
            builder.append(token);
            builder.append(", ");
        }

        if (!tokens.isEmpty()) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("입니다.");
        result.put("result",builder.toString());
        return result;
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
     */
}
