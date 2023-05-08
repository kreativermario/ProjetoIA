package algoGenetico;

import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;

import java.util.*;

public class PopulationEvo extends Thread {

	private Logger logger = LoggerFactory.getLogger(PopulationEvo.class);
	List<NeuralNetwork> population = new LinkedList<>();
	private static final int NR_GENERATIONS = 30;
	private int curGeneration = 0;

	private static final int NR_FIT_INDIVIDUALS = 100;
	private static final int TOURNAMENT_SIZE = 15;
	public  static final int SEED = 750;
	private static final int HIDDEN_DIM_SIZE = 10;
	private static final double MUTATION_PROB = 0.2;
	private static final int POPULATION_SIZE = 750;
	private static final Random RANDOM = new Random();

	public PopulationEvo(){
		createPopulation(); //é basicamente o código do main
		//enquanto condição de parar não for atingida, cruzar e mutar
		Collections.sort(population);
		population.forEach(e -> logger.info("Inicial Fitness: {}", e.getFitness()));

	}

	@Override
	public void run(){
		while (curGeneration < NR_GENERATIONS) {
			population = selectFit();
			createNewGen();
			for (NeuralNetwork nn : population) {
				Board board = new Board(nn);
				board.setSeed(SEED);
				board.run();
				Double fitness = board.getFitness();
				nn.setFitness(fitness);
			}
			logger.info("Generation no: {}", curGeneration);
		}
		Collections.sort(population);
		population.forEach(e -> logger.info("Fim Fitness: {}", e.getFitness()));
	}

	public NeuralNetwork getFittest(){
		Collections.sort(population);
		return population.get(0);
	}


	//create a population of individual  methods
	public void createPopulation() {

		population = new LinkedList<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			NeuralNetwork nn = new NeuralNetwork(HIDDEN_DIM_SIZE);
			population.add(nn);
			nn.initializeWeights();
			Board board = new Board(nn);
			board.setSeed(SEED);
			board.run();
			Double fitness = board.getFitness();
			nn.setFitness(fitness);

//			logger.info("Neural net: {} | Time alive: {} | Kills: {} | Fitness {}", i, board.getTime(), board.getDeaths(),
//					fitness);
		}
	}


	public List<NeuralNetwork> selectFit(){
		Collections.sort(population);
		return population.subList(0, NR_FIT_INDIVIDUALS); //vai buscar os FIT_PRCTG individuos mais fit
	}


	public void createNewGen() {
		while (population.size() < POPULATION_SIZE) {
			// Pick 2 NeuralNetwork (parents) from fit population list
			NeuralNetwork firstParent = selectParent();
			NeuralNetwork secondParent = selectParent();

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

			// Generate a new child using crossover
			NeuralNetwork child = crossover(firstParent, secondParent);


			// Mutate the child with a certain probability
			if (Math.random() < MUTATION_PROB) {
				mutate(child);
			}

			// Add the new child to the population
			population.add(child);
		}
		this.curGeneration++;
	}


	private NeuralNetwork crossover(NeuralNetwork parent1, NeuralNetwork parent2){
		int size = parent1.getChromossomeSize();
		//pick a random point in the genome
		int random = RANDOM.nextInt(0, size); // generates a random number between 0 (inclusive) and size (exclusive)

		double[] firstGenes1 = Arrays.copyOfRange(parent1.getChromossome(), 0, random);
		double[] secondGenes1 = Arrays.copyOfRange(parent2.getChromossome(), random, parent2.getChromossomeSize());

		double[] child1Genes = new double[size];
		if (random >= 0) System.arraycopy(firstGenes1, 0, child1Genes, 0, random);
		if (size - random >= 0) System.arraycopy(secondGenes1, 0, child1Genes, random, size - random);


		//create a new NeuralNetwork with the genes of the parents
		NeuralNetwork child1 = new NeuralNetwork(HIDDEN_DIM_SIZE, child1Genes);

		//TODO: perguntar ao professor fazer 1 ou 2 childs?

		//logger.info(population.indexOf(parent1) + " and " + population.indexOf(parent2) + " crossed");
		return child1;
	}


	private NeuralNetwork selectParent(){
		Collections.shuffle(population); // Randomizar a lista
		List<NeuralNetwork> selected = population.subList(0, TOURNAMENT_SIZE); // escolher K randoms
		NeuralNetwork progenitor = null;
		double maxFitness = Double.NEGATIVE_INFINITY;
		for (NeuralNetwork nn : selected) {
			if (nn.getFitness() > maxFitness) {
				progenitor = nn;
				maxFitness = nn.getFitness();
			}
		}
		return progenitor;

	}


	public void mutate(NeuralNetwork neuralNetwork) {
		// select two random genes to swap
		int gene1 = RANDOM.nextInt(0, neuralNetwork.getChromossomeSize());
		int gene2;
		do{
			gene2 = RANDOM.nextInt(0, neuralNetwork.getChromossomeSize());
		}while(gene2 == gene1);

		// swap the values of the two selected genes
		double temp = neuralNetwork.getChromossome()[gene1];
		neuralNetwork.getChromossome()[gene1] = neuralNetwork.getChromossome()[gene2];
		neuralNetwork.getChromossome()[gene2] = temp;

	}



}
