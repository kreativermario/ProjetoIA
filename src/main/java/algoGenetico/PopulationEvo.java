package algoGenetico;

import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;

import java.io.*;
import java.util.*;

public class PopulationEvo {

	private static final String BEST_NN_FILE = "best_neural_network.txt";

	private final Logger logger = LoggerFactory.getLogger(PopulationEvo.class);
	private List<NeuralNetwork> population = new LinkedList<>();
	private int curGeneration = 0;
	private final Hyperparameters hyperparameters;
//	public static final int NR_GENERATIONS = 40;
//	public static final int NR_FIT_INDIVIDUALS = 20;
//	public static final int TOURNAMENT_SIZE = 4;
//	public  static final int SEED = 750;
//	public static final int HIDDEN_DIM_SIZE = 15;
//	public static final double MUTATION_PROB = 0.3;
//	public static final int POPULATION_SIZE = 650;

	public PopulationEvo(Hyperparameters hyperparameters){
		this.hyperparameters = hyperparameters;
		createPopulation(); //é basicamente o código do main
		//enquanto condição de parar não for atingida, cruzar e mutar
		//Collections.sort(population);
		//population.forEach(e -> logger.info("Inicial Fitness: {}", e.getFitness()));
		logger.info("Thread: {} | {}", Thread.currentThread().getName(), hyperparameters.toString());
		init();
	}

	private void init(){
		long startTime = System.currentTimeMillis();

		while (curGeneration < hyperparameters.getNrGenerations()) {
			population = selectFit();
			createNewGen();
			for (NeuralNetwork nn : population) {
				Board board = new Board(nn);
				board.setSeed(hyperparameters.getSeed());
				board.run();
				Double fitness = board.getFitness();
				nn.setFitness(fitness);
			}
			logger.info("Thread: {} | Generation no: {} out of {}", Thread.currentThread().getName(), curGeneration,
					hyperparameters.getNrGenerations());
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		logger.info("Thread: {} | Fittest: {}", Thread.currentThread().getName(), getFittest().getFitness());
		logger.info("Thread: {} | Init method took {} ms", Thread.currentThread().getName(), duration);

		saveBestNeuralNetwork();
	}


	public synchronized void saveBestNeuralNetwork() {
		logger.info("Thread: {} | GOING TO WRITE... writing file", Thread.currentThread().getName());
		NeuralNetwork currentBestNN = getFittest();
		double currentBestFitness = currentBestNN.getFitness();

		try (PrintWriter writer = new PrintWriter(new FileWriter(BEST_NN_FILE, true))) {
			writer.println("-------------------------------------------------");
			writer.println("Best Fitness: " + currentBestFitness);
			logger.info("Thread: {} | WROTE Fittest: {}", Thread.currentThread().getName(), currentBestFitness);
			writer.println(Arrays.toString(currentBestNN.getChromossome()));
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


	public NeuralNetwork getFittest(){
		Collections.sort(population);
		return population.get(0);
	}


	//create a population of individual  methods
	public void createPopulation() {

		population = new LinkedList<>();

		for (int i = 0; i < hyperparameters.getPopulationSize(); i++) {
			NeuralNetwork nn = new NeuralNetwork(hyperparameters.getHiddenDimSize());
			population.add(nn);
			nn.initializeWeights();
			Board board = new Board(nn);
			board.setSeed(hyperparameters.getSeed());
			board.run();
			Double fitness = board.getFitness();
			nn.setFitness(fitness);

//			logger.info("Neural net: {} | Time alive: {} | Kills: {} | Fitness {}", i, board.getTime(), board.getDeaths(),
//					fitness);
		}
	}


	public List<NeuralNetwork> selectFit(){
		Collections.sort(population);
		return population.subList(0, hyperparameters.getNrFitIndividuals()); //vai buscar os FIT_PRCTG individuos mais fit
	}


	public void createNewGen() {
		while (population.size() < hyperparameters.getPopulationSize()) {
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

			List<NeuralNetwork> children = crossover(firstParent, secondParent);
			for(NeuralNetwork child : children){
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


	private List<NeuralNetwork> crossover(NeuralNetwork parent1, NeuralNetwork parent2){
		int size = parent1.getChromossomeSize();
		//pick a random point in the genome
		Random randomObject = new Random(hyperparameters.getSeed());
		int random = randomObject.nextInt(0, size); // generates a random number between 0 (inclusive) and size (exclusive)

		double[] firstGenes1 = Arrays.copyOfRange(parent1.getChromossome(), 0, random);
		double[] secondGenes1 = Arrays.copyOfRange(parent2.getChromossome(), random, parent2.getChromossomeSize());

		double[] child1Genes = new double[size];
		if (random >= 0) System.arraycopy(firstGenes1, 0, child1Genes, 0, random);
		if (size - random >= 0) System.arraycopy(secondGenes1, 0, child1Genes, random, size - random);

		//create a new NeuralNetwork with the genes of the parents
		NeuralNetwork child1 = new NeuralNetwork(hyperparameters.getHiddenDimSize(), child1Genes);

		// Second child creation
		double[] firstGenes2 = Arrays.copyOfRange(parent2.getChromossome(), 0, random);
		double[] secondGenes2 = Arrays.copyOfRange(parent1.getChromossome(), random, parent1.getChromossomeSize());

		double[] child2Genes = new double[size];
		if (random >= 0) System.arraycopy(firstGenes2, 0, child2Genes, 0, random);
		if (size - random >= 0) System.arraycopy(secondGenes2, 0, child2Genes, random, size - random);

		//create a new NeuralNetwork with the genes of the parents
		NeuralNetwork child2 = new NeuralNetwork(hyperparameters.getHiddenDimSize(), child2Genes);

		List<NeuralNetwork> children = new ArrayList<>();
		children.add(child1);
		children.add(child2);

		return children;
	}


	private NeuralNetwork selectParent(){
		Collections.shuffle(population); // Randomizar a lista
		List<NeuralNetwork> selected = population.subList(0, hyperparameters.getTournamentSize()); // escolher K randoms
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
		Random randomObject = new Random(hyperparameters.getSeed());
		// select two random genes to swap
		int gene1 = randomObject.nextInt(0, neuralNetwork.getChromossomeSize());
		int gene2;
		do{
			gene2 = randomObject.nextInt(0, neuralNetwork.getChromossomeSize());
		}while(gene2 == gene1);

		// swap the values of the two selected genes
		double temp = neuralNetwork.getChromossome()[gene1];
		neuralNetwork.getChromossome()[gene1] = neuralNetwork.getChromossome()[gene2];
		neuralNetwork.getChromossome()[gene2] = temp;

	}



	public static NeuralNetwork importInitialChamp(int hiddenDimSize, File path){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			if ((line = reader.readLine()) != null) {
				String genome = line.replace("[", "").replace("]", "");
				String[] values = genome.split(",");
				System.out.println("Values" + Arrays.toString(values));
				double[] genes = new double[values.length];
				System.out.println("Genes:" + Arrays.toString(genes));
				for (int i = 0; i < values.length; i++) {
					genes[i] = Double.parseDouble(values[i]);
				}
				System.out.println("Genes:" + Arrays.toString(genes));

				return new NeuralNetwork(hiddenDimSize, genes);

			}
		} catch (IOException e) {
			System.err.println("Error importing initial population"+ e);
		}
		return null;
	}

}
