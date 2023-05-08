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

    private void runMultiple(){
        int numRuns = 160; // number of runs with different settings
        Random random = new Random();

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
            populationEvo.start();
            try {
                populationEvo.join();
                logger.info("Num Run: {}", numRuns--);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
//        hyperparameterTuning.runSingle();
        hyperparameterTuning.runMultiple();
    }
}
