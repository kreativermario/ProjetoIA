package main;

import controllers.GameController;
import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        int populationSize = 100;
        int hiddenDim = 20;
        List<NeuralNetwork> population = new LinkedList<>();

        for (int i = 0; i < populationSize; i++) {
            NeuralNetwork neuralNetwork = new NeuralNetwork(hiddenDim);
            population.add(neuralNetwork);
            neuralNetwork.initializeWeights();
            Board board = new Board(neuralNetwork);
            board.setSeed(100);
            board.run();
            Double fitness = board.getFitness();
            neuralNetwork.setFitness(fitness);
            logger.info("Neural net: {} | Time alive: {} | Kills: {} | Fitness {}", i, board.getTime(), board.getDeaths(),
                    fitness);
        }
        Collections.sort(population);
        population.forEach(e -> logger.info("Neural Net: {} | Fitness: {}\n", e.getChromossome(), e.getFitness()));
    }

}
