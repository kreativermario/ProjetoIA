package nn;

import controllers.GameController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Commons;

import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork implements GameController, Comparable<NeuralNetwork> {
    private Logger logger = LoggerFactory.getLogger(NeuralNetwork.class);
    public static final int INPUT_DIM = Commons.STATE_SIZE;
    public static final int OUTPUT_DIM = Commons.NUM_ACTIONS;
    private int hiddenDim;
    private double fitness;
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
        if (chromossome != null) return chromossome;

        chromossome = new double[getChromossomeSize()];
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

    public void setFitness(double fitness){
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }



    @Override
    public double[] nextMove(double[] currentState) {
        return forward(currentState);
    }


    @Override
    public int compareTo(NeuralNetwork other) {
        return Double.compare(other.getFitness(), this.getFitness());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NeuralNetwork other)) {
            return false;
        }
        return Arrays.deepEquals(inputWeights, other.inputWeights)
                && Arrays.equals(hiddenBiases, other.hiddenBiases)
                && Arrays.deepEquals(outputWeights, other.outputWeights)
                && Arrays.equals(outputBiases, other.outputBiases);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.deepHashCode(inputWeights);
        result = 31 * result + Arrays.hashCode(hiddenBiases);
        result = 31 * result + Arrays.deepHashCode(outputWeights);
        result = 31 * result + Arrays.hashCode(outputBiases);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NeuralNetwork:\n");
        sb.append("  Fitness: ").append(fitness).append("\n");
        sb.append("  Input weights:\n");
        for (double[] inputWeight : inputWeights) {
            for (int j = 0; j < inputWeights[0].length; j++) {
                sb.append(String.format("%.2f ", inputWeight[j]));
            }
            sb.append("\n");
        }
        sb.append("  Hidden biases:\n");
        for (double hiddenBiase : hiddenBiases) {
            sb.append(String.format("%.2f ", hiddenBiase));
        }
        sb.append("\n");
        sb.append("  Output weights:\n");
        for (double[] outputWeight : outputWeights) {
            for (int j = 0; j < outputWeights[0].length; j++) {
                sb.append(String.format("%.2f ", outputWeight[j]));
            }
            sb.append("\n");
        }
        sb.append("  Output biases:\n");
        for (double outputBiase : outputBiases) {
            sb.append(String.format("%.2f ", outputBiase));
        }
        sb.append("\n");
        return sb.toString();
    }

}