package main;

import algoGenetico.Hyperparameters;
import algoGenetico.PopulationEvo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HyperparameterTuning {

    private final Logger logger = LoggerFactory.getLogger(HyperparameterTuning.class);

    private void runMultiple() throws InterruptedException {
        int numRuns = 10; // number of runs with different settings
        int numThreads = 4; // number of threads to run algorithms in parallel
        Random random = new Random();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        // Create a countdown latch to wait for all threads to finish
        CountDownLatch latch = new CountDownLatch(numRuns);

        for (int i = 0; i < numRuns; i++) {
            // Randomize hyperparameters
            int populationSize = random.nextInt(900) + 100;
            int nrFitIndividuals = random.nextInt(populationSize / 2) + 1;
            double mutationProb = random.nextDouble();
            int nrGenerations = random.nextInt(100) + 1;
            int tournamentSize = random.nextInt(nrFitIndividuals) + 1;
            int hiddenDimSize = random.nextInt(50) + 1;
            int seed = random.nextInt(1000);

            Hyperparameters hyperparameters = new Hyperparameters();
            hyperparameters.setNrGenerations(nrGenerations);
            hyperparameters.setNrFitIndividuals(nrFitIndividuals);
            hyperparameters.setTournamentSize(tournamentSize);
            hyperparameters.setSeed(seed);
            hyperparameters.setHiddenDimSize(hiddenDimSize);
            hyperparameters.setMutationProb(mutationProb);
            hyperparameters.setPopulationSize(populationSize);

            PopulationEvo populationEvo = new PopulationEvo(hyperparameters);

            // Wrap the populationEvo in a new thread and submit it to the executor
            futures.add(executor.submit(() -> {
                populationEvo.start();
                latch.countDown();
            }));
        }

        // Wait for all tasks to complete
        latch.await();

        executor.shutdown();
        logger.debug("FINISHED");
    }

    private void runSingle() {
        Random random = new Random();
        // Randomize hyperparameters
        int populationSize = random.nextInt(900) + 100;
        int nrFitIndividuals = random.nextInt(populationSize / 2) + 1;
        double mutationProb = random.nextDouble();
        int nrGenerations = random.nextInt(100) + 1;
        int tournamentSize = random.nextInt(nrFitIndividuals) + 1;
        int hiddenDimSize = random.nextInt(50) + 1;
        int seed = random.nextInt(1000);

        Hyperparameters hyperparameters = new Hyperparameters();
        hyperparameters.setNrGenerations(nrGenerations);
        hyperparameters.setNrFitIndividuals(nrFitIndividuals);
        hyperparameters.setTournamentSize(tournamentSize);
        hyperparameters.setSeed(seed);
        hyperparameters.setHiddenDimSize(hiddenDimSize);
        hyperparameters.setMutationProb(mutationProb);
        hyperparameters.setPopulationSize(populationSize);

        PopulationEvo populationEvo = new PopulationEvo(hyperparameters);
        populationEvo.start();
    }


    public static void main(String[] args) throws InterruptedException {
        HyperparameterTuning hyperparameterTuning = new HyperparameterTuning();
        int numRuns = 5; // number of runs with different settings
//        hyperparameterTuning.runSingle();
        hyperparameterTuning.runMultiple();
    }
}
