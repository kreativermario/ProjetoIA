package nn;

import controllers.GameController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;
import space.Commons;

import java.util.Random;

public class NeuralNetwork implements GameController {
    private Logger logger = LoggerFactory.getLogger(NeuralNetwork.class);
    public static final int INPUT_DIM = Commons.STATE_SIZE;
    public static final int OUTPUT_DIM = Commons.NUM_ACTIONS;
    private int hiddenDim;
    private double[][] inputWeights;
    private double[] hiddenBiases;
    private double[][] outputWeights;
    private double[] outputBiases;

    public NeuralNetwork(int hiddenDim) {
        this.hiddenDim = hiddenDim;
        this.inputWeights = new double[INPUT_DIM][hiddenDim];
        this.hiddenBiases = new double[hiddenDim];
        this.outputWeights = new double[hiddenDim][OUTPUT_DIM];
        this.outputBiases = new double[OUTPUT_DIM];
    }

    /**
     * Construtor usado para as próximas gerações
     * @param INPUT_DIM
     * @param hiddenDim
     * @param OUTPUT_DIM
     * @param values
     */
    public NeuralNetwork(int INPUT_DIM, int hiddenDim, int OUTPUT_DIM, double[] values) {
        this(hiddenDim);
        int offset = 0;
        for (int i = 0; i < INPUT_DIM; i++) {
            for (int j = 0; j < hiddenDim; j++) {
                inputWeights[i][j] = values[i * hiddenDim + j];
            }
        }
        offset = INPUT_DIM * hiddenDim;
        for (int i = 0; i < hiddenDim; i++) {
            hiddenBiases[i] = values[offset + i];
        }
        offset += hiddenDim;
        for (int i = 0; i < hiddenDim; i++) {
            for (int j = 0; j < OUTPUT_DIM; j++) {
                outputWeights[i][j] = values[offset + i * OUTPUT_DIM + j];
            }
        }
        offset += hiddenDim * OUTPUT_DIM;
        for (int i = 0; i < OUTPUT_DIM; i++) {
            outputBiases[i] = values[offset + i];
        }

    }

    public int getChromossomeSize() {
        return inputWeights.length * inputWeights[0].length + hiddenBiases.length
                + outputWeights.length * outputWeights[0].length + outputBiases.length;
    }

    public double[] getChromossome() {
        double[] chromossome = new double[getChromossomeSize()];
        int offset = 0;
        for (int i = 0; i < INPUT_DIM; i++) {
            for (int j = 0; j < hiddenDim; j++) {
                chromossome[i * hiddenDim + j] = inputWeights[i][j];
            }
        }
        offset = INPUT_DIM * hiddenDim;
        for (int i = 0; i < hiddenDim; i++) {
            chromossome[offset + i] = hiddenBiases[i];
        }
        offset += hiddenDim;
        for (int i = 0; i < hiddenDim; i++) {
            for (int j = 0; j < OUTPUT_DIM; j++) {
                chromossome[offset + i * OUTPUT_DIM + j] = outputWeights[i][j];
            }
        }
        offset += hiddenDim * OUTPUT_DIM;
        for (int i = 0; i < OUTPUT_DIM; i++) {
            chromossome[offset + i] = outputBiases[i];
        }

        return chromossome;

    }

    public void initializeWeights() {
        // Randomly initialize weights and biases
        Random random = new Random();
        for (int i = 0; i < INPUT_DIM; i++) {
            for (int j = 0; j < hiddenDim; j++) {
                inputWeights[i][j] = random.nextDouble() - 0.5;
            }
        }
        for (int i = 0; i < hiddenDim; i++) {
            hiddenBiases[i] = random.nextDouble() - 0.5;
            for (int j = 0; j < OUTPUT_DIM; j++) {
                outputWeights[i][j] = random.nextDouble() - 0.5;
            }
        }
        for (int i = 0; i < OUTPUT_DIM; i++) {
            outputBiases[i] = random.nextDouble() - 0.5;
        }
    }

    public double[] forward(double[] d2) {
        // Compute output given input
        double[] hidden = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            double sum = 0.0;
            for (int j = 0; j < INPUT_DIM; j++) {
                double d = d2[j];
                sum += d * inputWeights[j][i];
            }
            hidden[i] = Math.max(0.0, sum + hiddenBiases[i]);
        }
        double[] output = new double[OUTPUT_DIM];
        for (int i = 0; i < OUTPUT_DIM; i++) {
            double sum = 0.0;
            for (int j = 0; j < hiddenDim; j++) {
                sum += hidden[j] * outputWeights[j][i];
            }
            output[i] = Math.exp(sum + outputBiases[i]);
        }
        double sum = 0.0;
        for (int i = 0; i < OUTPUT_DIM; i++) {
            sum += output[i];
        }
        for (int i = 0; i < OUTPUT_DIM; i++) {
            output[i] /= sum;
        }
        return output;
    }


    @Override
    public double[] nextMove(double[] currentState) {
        return forward(currentState);
    }
}