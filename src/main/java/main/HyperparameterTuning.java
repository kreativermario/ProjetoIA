package main;

import algoGenetico.Hyperparameters;
import algoGenetico.PopulationEvo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

public class HyperparameterTuning {

    private final Logger logger = LoggerFactory.getLogger(HyperparameterTuning.class);

    private void runMultiple(){
        int numRuns = 1000; // number of runs with different settings
        int numThreads = 15; // number of threads in the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numRuns; i++) {
            futures.add(executor.submit(() -> {
                // Randomize hyperparameters
                int populationSize = random.nextInt(900) + 100;
                double elitismRatio = 0.05 + (0.2 - 0.05) * random.nextDouble(); // selecionar x %
                double mutationProb = 0.01 + (0.5 - 0.01) * random.nextDouble(); // Range between 0.01 and 0.1
                int nrGenerations = random.nextInt(450) + 50; // Range between 50 and 500
                int tournamentSize = random.nextInt(25) + 2; // Range between 2 and 7
                int hiddenDimSize = random.nextInt(50) + 1;
                int seed = random.nextInt(6000);

                Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, elitismRatio, tournamentSize,
                        seed, hiddenDimSize, mutationProb, populationSize);

                PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
                populationEvo.init();
            }));
        }

        // Wait for all tasks to finish
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

    }



    public static void main(String[] args) throws InterruptedException {
        HyperparameterTuning hyperparameterTuning = new HyperparameterTuning();
//        hyperparameterTuning.runSingle();
        hyperparameterTuning.runMultiple();
    }
}
