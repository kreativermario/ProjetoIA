package main;

import controllers.GameController;
import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;
import space.SpaceInvaders;

public class Main {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        int populationSize = 10;
        int hiddenDim = 50;
        NeuralNetwork[] population = new NeuralNetwork[populationSize];

//        for (int i = 0; i < populationSize; i++) {
//            population[i] = new NeuralNetwork(hiddenDim);
//            population[i].initializeWeights();
//            Board board = new Board(population[i]);
//            board.setSeed(100);
//        }
        NeuralNetwork neuralNetwork = new NeuralNetwork(hiddenDim);
        neuralNetwork.initializeWeights();
        Board board = new Board(neuralNetwork);
        board.setSeed(100);
        board.run();
        logger.info("Time alive: {}", board.getTime());
        logger.info("Kills: {}", board.getDeaths());
        logger.info("Fitness: {}", board.getFitness());
    }

}
