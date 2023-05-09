package algoGenetico;

import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HyperparametersEvo {

    private static final String BEST_HP_FILE = "best_parameters.txt";
    private final Logger logger = LoggerFactory.getLogger(HyperparametersEvo.class);
    private int curGeneration = 0;
    private Random random;
    private List<Hyperparameters> population;
    private Hyperparameters hyperparameters;


    public HyperparametersEvo(Hyperparameters hyperparameters) {
        this.random = new Random(hyperparameters.getSeed());
        this.hyperparameters = hyperparameters;
        logger.info("Thread: {} | {}", Thread.currentThread().getName(), hyperparameters.toString());
        initializePopulation();
        init();
    }

    public void init() {
        long startTime = System.currentTimeMillis();
        while (curGeneration < hyperparameters.getNrGenerations()) {
            population = selectFit();
            createNewGen();
            for(Hyperparameters hp : population){
                PopulationEvo populationEvo = new PopulationEvo(hp);
                hyperparameters.setFitness(populationEvo.getFittest().getFitness());
                population.add(hp);
            }
            logger.info("Thread: {} | Generation no: {} out of {}", Thread.currentThread().getName(), curGeneration,
                    hyperparameters.getNrGenerations());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Thread: {} | Fittest: {}", Thread.currentThread().getName(), getFittest().getFitness());
        logger.info("Thread: {} | Init method took {} ms", Thread.currentThread().getName(), duration);

        saveBestHyperparameter();
    }

    private List<Hyperparameters> selectFit(){
        Collections.sort(population);
        return population.subList(0, hyperparameters.getNrFitIndividuals());
    }

    private void initializePopulation() {
        population = new LinkedList<>();

        for(int i = 0; i < hyperparameters.getPopulationSize(); i++){

            // Randomize hyperparameters
            int nrGenerations = random.nextInt(200) + 1;
            int populationSize = random.nextInt(900) + 100;
            int nrFitIndividuals = random.nextInt(populationSize / 2) + 1;
            int tournamentSize = random.nextInt(nrFitIndividuals) + 1;
            int seed = random.nextInt(6000);
            int hiddenDimSize = random.nextInt(50) + 1;
            double mutationProb = random.nextDouble();

            Hyperparameters hp = new Hyperparameters(nrGenerations, nrFitIndividuals, tournamentSize
            , seed, hiddenDimSize, mutationProb, populationSize);

            PopulationEvo populationEvo = new PopulationEvo(hp);
            hp.setFitness(populationEvo.getFittest().getFitness());
            population.add(hp);
        }
    }

    private void createNewGen(){
        while (population.size() < hyperparameters.getPopulationSize()) {
            // Pick 2 NeuralNetwork (parents) from fit population list
            Hyperparameters firstParent = selectParent();
            Hyperparameters secondParent = selectParent();

            // Make sure parents are not null
            while (firstParent == null) {
                firstParent = selectParent();
            }
            while (secondParent == null) {
                secondParent = selectParent();
            }

            // Make sure parents are different
            while (firstParent.equals(secondParent)) {
                secondParent = selectParent();
            }

            List<Hyperparameters> children = crossover(firstParent, secondParent);
            for(Hyperparameters child : children){
                // Mutate the child with a certain probability
                if (Math.random() < hyperparameters.getMutationProb()) {
                    mutate(child);
                }
                // Add the new child to the population
                population.add(child);
            }
        }
        this.curGeneration++;
    }

    private void mutate(Hyperparameters hyperparameters1) {
        Random randomObject = new Random(hyperparameters.getSeed());
        // select two random genes to swap
        int gene1 = randomObject.nextInt(0, Hyperparameters.CHROMOSSOME_SIZE);
        int gene2;
        do{
            gene2 = randomObject.nextInt(0, Hyperparameters.CHROMOSSOME_SIZE);
        }while(gene2 == gene1);

        // swap the values of the two selected genes
        double temp = hyperparameters1.getChromossome()[gene1];
        hyperparameters1.getChromossome()[gene1] = hyperparameters1.getChromossome()[gene2];
        hyperparameters1.getChromossome()[gene2] = temp;
    }

    private List<Hyperparameters> crossover(Hyperparameters parent1, Hyperparameters parent2){
        int size = Hyperparameters.CHROMOSSOME_SIZE;
        //pick a random point in the genome
        int randomVariable = random.nextInt(0, size); // generates a random number between 0 (inclusive) and size (exclusive)

        double[] firstGenes1 = Arrays.copyOfRange(parent1.getChromossome(), 0, randomVariable);
        double[] secondGenes1 = Arrays.copyOfRange(parent2.getChromossome(), randomVariable, size);

        double[] child1Genes = new double[size];
        if (randomVariable >= 0) System.arraycopy(firstGenes1, 0, child1Genes, 0, randomVariable);
        if (size - randomVariable >= 0) System.arraycopy(secondGenes1, 0, child1Genes, randomVariable, size - randomVariable);

        Hyperparameters child1 = new Hyperparameters(child1Genes);

        // Second child creation
        double[] firstGenes2 = Arrays.copyOfRange(parent2.getChromossome(), 0, randomVariable);
        double[] secondGenes2 = Arrays.copyOfRange(parent1.getChromossome(), randomVariable, size);

        double[] child2Genes = new double[size];
        if (randomVariable >= 0) System.arraycopy(firstGenes2, 0, child2Genes, 0, randomVariable);
        if (size - randomVariable >= 0) System.arraycopy(secondGenes2, 0, child2Genes, randomVariable, size - randomVariable);

        //create a new NeuralNetwork with the genes of the parents
        Hyperparameters child2 = new Hyperparameters(child2Genes);

        List<Hyperparameters> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        return children;
    }

    private Hyperparameters selectParent(){
        Collections.shuffle(population); // Randomizar a lista
        List<Hyperparameters> selected = population.subList(0, hyperparameters.getTournamentSize()); // escolher K randoms
        Hyperparameters progenitor = null;
        double maxFitness = Double.NEGATIVE_INFINITY;
        for (Hyperparameters hp : selected) {
            if (hp.getFitness() > maxFitness) {
                progenitor = hp;
                maxFitness = hp.getFitness();
            }
        }
        return progenitor;
    }

    public synchronized void saveBestHyperparameter() {
        logger.info("Thread: {} | GOING TO WRITE... writing file", Thread.currentThread().getName());
        Hyperparameters best = getFittest();
        double currentBestFitness = best.getFitness();

        try (PrintWriter writer = new PrintWriter(new FileWriter(BEST_HP_FILE, true))) {
            writer.println("-------------------------------------------------");
            writer.println("Best Fitness: " + currentBestFitness);
            logger.info("Thread: {} | WROTE Fittest: {}", Thread.currentThread().getName(), currentBestFitness);
            writer.println(Arrays.toString(best.getChromossome()));
            //logger.debug("Thread: {} wrote CHROMOSSOME\n{}", Thread.currentThread().getName(), currentBestNN.getChromossome());
            writer.println("Population Size: " + hyperparameters.getPopulationSize());
            writer.println("Nr Fittest Individuals: " + hyperparameters.getNrFitIndividuals());
            writer.println("Mutation Probability: " + hyperparameters.getMutationProb());
            writer.println("Number of Generations: " + hyperparameters.getNrGenerations());
            writer.println("Tournament Size: " + hyperparameters.getTournamentSize());
            writer.println("Hidden Layer Size: " + hyperparameters.getHiddenDimSize());
            writer.println("Seed: " + hyperparameters.getSeed());
        } catch (IOException e) {
            logger.error("Error appending best neural network and parameters to file", e);
        }
    }


    private Hyperparameters getFittest() {
        Collections.sort(population);
        return population.get(0);
    }

    public static void main(String[] args) {
        Random random = new Random(100);
        // Randomize hyperparameters
        int nrGenerations = random.nextInt(200) + 1;
        int populationSize = random.nextInt(900) + 100;
        int nrFitIndividuals = random.nextInt(populationSize / 2) + 1;
        int tournamentSize = random.nextInt(nrFitIndividuals) + 1;
        int seed = random.nextInt(6000);
        int hiddenDimSize = random.nextInt(50) + 1;
        double mutationProb = random.nextDouble();

        Hyperparameters hp = new Hyperparameters(nrGenerations, nrFitIndividuals, tournamentSize
                , seed, hiddenDimSize, mutationProb, populationSize);

        HyperparametersEvo hyperparametersEvo = new HyperparametersEvo(hp);
    }

}
