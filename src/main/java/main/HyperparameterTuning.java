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
        int numRuns = 100; // number of runs with different settings
        int numThreads = 20; // number of threads in the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
        Random random = new Random();
        List<Integer> seedList = new ArrayList<>();

        for (int i = 0; i < numRuns; i++) {
            seedList.add(random.nextInt(6000));
        }
        Collections.shuffle(seedList);
        for(int seed : seedList) {
            for (int i = 0; i < numRuns; i++) {
                futures.add(executor.submit(() -> {
                    // Randomize hyperparameters
                    int populationSize = random.nextInt(900) + 100;
                    int nrFitIndividuals = random.nextInt(populationSize / 2) + 1;
                    double mutationProb = random.nextDouble();
                    int nrGenerations = random.nextInt(400) + 1;
                    int tournamentSize = random.nextInt(nrFitIndividuals) + 1;
                    int hiddenDimSize = random.nextInt(50) + 1;

                    Hyperparameters hyperparameters = new Hyperparameters(nrGenerations, nrFitIndividuals, tournamentSize,
                            seed, hiddenDimSize, mutationProb, populationSize);

                    PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
                }));
            }
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
