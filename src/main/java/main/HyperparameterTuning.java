package main;

import algoGenetico.Hyperparameters;
import algoGenetico.PopulationEvo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class HyperparameterTuning {

    private final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(HyperparameterTuning.class);

    private void runMultipleSameSeed(int numRuns, int numThreads, int seed){
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numRuns; i++) {
            futures.add(executor.submit(() -> {
                // Randomize hyperparameters
                int populationSize = random.nextInt(900) + 100;
                double elitismRatio = 0.15 + (0.2 - 0.15) * random.nextDouble(); // selecionar x %
                double mutationProb = 0.01 + (0.5 - 0.01) * random.nextDouble(); // Range between 0.01 and 0.1
                int nrGenerations = random.nextInt(500) + 50; // Range between 50 and 500
                int tournamentSize = random.nextInt(25) + 2; // Range between 2 and 7
                int hiddenDimSize = random.nextInt(50) + 1;

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


    private void runMultiple(){
        int numRuns = 1000000; // number of runs with different settings
        int numThreads = 5; // number of threads in the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numRuns; i++) {
            futures.add(executor.submit(() -> {
                // Randomize hyperparameters
                int populationSize = random.nextInt(2000) + 100;
                double elitismRatio = 0.15 + (0.1 - 0.15) * random.nextDouble();
                double mutationProb = 0.01 + (0.03 - 0.01) * random.nextDouble();
                int nrGenerations = random.nextInt(1400) + 200;
                int tournamentSize = random.nextInt(35) + 5;
                int hiddenDimSize = random.nextInt(26) + 1;
                int seed = random.nextInt(6000);

                Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, elitismRatio, tournamentSize,
                        seed, hiddenDimSize, mutationProb, populationSize);

                PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
                populationEvo.init();
                //populationEvo.initFrom(PopulationEvo.importInitialChamp(8, new File("src/main/java/algoGenetico/NetworkChamp.txt")));
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

    public void runSingle() {
        int populationSize = 500;
        double elitismRatio = 0.2;
        double mutationProb = 0.05;
        int nrGenerations =  50;
        int tournamentSize = 4;
        int hiddenDimSize = 22;
        int seed = 6942;
        Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, elitismRatio, tournamentSize,
                 seed, hiddenDimSize, mutationProb, populationSize);
        PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
        populationEvo.init();
    }

    public static void main(String[] args) throws InterruptedException {
        HyperparameterTuning hyperparameterTuning = new HyperparameterTuning();
        hyperparameterTuning.runMultiple();
        //hyperparameterTuning.runSingle();
        //hyperparameterTuning.runMultipleSameSeed(100, 25, 2065);
    }
}
