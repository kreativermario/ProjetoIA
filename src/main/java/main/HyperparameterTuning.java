package main;

import algoGenetico.Hyperparameters;
import algoGenetico.PopulationEvo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class HyperparameterTuning {

    private final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(HyperparameterTuning.class);

    private void runMultiple(){
        int numRuns = 1000000; // número de runs com settings diferentes
        int numThreads = 1; // número de threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numRuns; i++) {
            futures.add(executor.submit(() -> {

                int populationSize = random.nextInt(1000) + 100;
                double elitismRatio = 0.15 + (0.1 - 0.15) * random.nextDouble();
                double mutationProb = 0.01 + (0.03 - 0.01) * random.nextDouble();
                int nrGenerations = random.nextInt(2000) + 200;
                int tournamentSize = random.nextInt(35) + 5;
                int hiddenDimSize = random.nextInt(26) + 1;
                int seed = random.nextInt(6000);

                Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, elitismRatio, tournamentSize,
                        seed, hiddenDimSize, mutationProb, populationSize);

                PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
                populationEvo.init();
            }));
        }

        executor.shutdown();

    }

    public void runSingle() {
        int populationSize =  400;
        double elitismRatio = 0.15;
        double mutationProb = 0.02;
        int nrGenerations =  4000;
        int tournamentSize = 20;
        int hiddenDimSize = 12;
        int seed = 2708;
        Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, elitismRatio, tournamentSize,
                 seed, hiddenDimSize, mutationProb, populationSize);
        PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
        populationEvo.init();
    }

    public static void main(String[] args) {
        HyperparameterTuning hyperparameterTuning = new HyperparameterTuning();
        //hyperparameterTuning.runMultiple();
        hyperparameterTuning.runSingle();
    }
}
