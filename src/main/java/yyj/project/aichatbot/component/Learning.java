package yyj.project.aichatbot.component;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Learning {
    public void leaning(){
        INDArray input = Nd4j.create(new double[][] {
                {0, 0}, {0, 1}, {1, 0}, {1, 1}
        });
        INDArray labels = Nd4j.create(new double[][] {
                {0}, {1}, {1}, {0}
        });

        // 신경망 구성 설정
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Sgd(0.1)) // 학습률 설정
                .list()
                .layer(0, new DenseLayer.Builder().nIn(2).nOut(4)
                        .activation(Activation.RELU).build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(4).nOut(1)
                        .activation(Activation.SIGMOID)
                        .lossFunction(LossFunctions.LossFunction.XENT).build())
                .build();

        // 신경망 모델 생성
        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        // 데이터셋 학습
        DataSet dataSet = new DataSet(input, labels);
        for (int i = 0; i < 1000; i++) {
            model.fit(dataSet);
        }

        // 테스트 데이터 예측
        INDArray output = model.output(input);
        System.out.println("예측 결과: ");
        System.out.println(output);
    }
}
