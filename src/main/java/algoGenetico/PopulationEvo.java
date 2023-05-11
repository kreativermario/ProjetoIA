package algoGenetico;

import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Board;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PopulationEvo {

	private static final String BEST_NN_FILE = "mario_results.txt";

	private final Logger logger = LoggerFactory.getLogger(PopulationEvo.class);
	private List<NeuralNetwork> population = new ArrayList<>();
	private int curGeneration = 0;
	private final Hyperparameters hyperparameters;
	private final Random randomObject;
//	public static final int NR_GENERATIONS = 40;
//	public static final int NR_FIT_INDIVIDUALS = 20;
//	public static final int TOURNAMENT_SIZE = 4;
//	public  static final int SEED = 750;
//	public static final int HIDDEN_DIM_SIZE = 15;
//	public static final double MUTATION_PROB = 0.3;
//	public static final int POPULATION_SIZE = 650;

	public PopulationEvo(Hyperparameters hyperparameters){
		this.hyperparameters = hyperparameters;
		this.randomObject = new Random(hyperparameters.getSeed());
	}

	public void initFrom(NeuralNetwork champ){

		createPopulation(); //é basicamente o código do main
		population.remove(0);
		population.add(champ);
		logger.info("Thread: {} | {}", Thread.currentThread().getName(), hyperparameters.toString());
		long startTime = System.currentTimeMillis();


		while (curGeneration < hyperparameters.getNrGenerations()) {
			createNewGen();

			// Parallelize fitness evaluations
			for (NeuralNetwork nn : getPopulation()) {
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
	public void init(){
		createPopulation(); //é basicamente o código do main
		logger.info("Thread: {} | {}", Thread.currentThread().getName(), hyperparameters.toString());
		long startTime = System.currentTimeMillis();


		while (curGeneration < hyperparameters.getNrGenerations()) {
			createNewGen();

			// Parallelize fitness evaluations
			for (NeuralNetwork nn : getPopulation()) {
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


	private synchronized void saveBestNeuralNetwork() {
		logger.info("Thread: {} | GOING TO WRITE... writing file", Thread.currentThread().getName());
		NeuralNetwork currentBestNN = getFittest();
		double currentBestFitness = currentBestNN.getFitness();

		try (PrintWriter writer = new PrintWriter(new FileWriter(BEST_NN_FILE, true))) {
			LocalDateTime now = LocalDateTime.now();
			String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS"));
			writer.println("-------------------------------------------------");
			writer.println("Time: " + formattedDateTime);
			writer.println("Best Fitness: " + currentBestFitness);
			logger.info("Thread: {} | WROTE Fittest: {}", Thread.currentThread().getName(), currentBestFitness);
			writer.println(Arrays.toString(currentBestNN.getChromossome()));
			//logger.debug("Thread: {} wrote CHROMOSSOME\n{}", Thread.currentThread().getName(), currentBestNN.getChromossome());
			writer.println("Population Size: " + hyperparameters.getPopulationSize());
			writer.println("Elitism ratio: " + hyperparameters.getElitismRatio());
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
		Collections.sort(getPopulation());
		return getPopulation().get(0);
	}


	//create a population of individual  methods
	private void createPopulation() {

		setPopulation(new LinkedList<>());

		for (int i = 0; i < hyperparameters.getPopulationSize(); i++) {
			NeuralNetwork nn = new NeuralNetwork(hyperparameters.getHiddenDimSize());
			getPopulation().add(nn);
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


	private void createNewGen() {
		// Implementing Elitism. Directly copying the best individuals.
		Collections.sort(getPopulation());
		int elitismCount = (int)(hyperparameters.getElitismRatio() * hyperparameters.getPopulationSize());
		List<NeuralNetwork> newPopulation = new ArrayList<>(getPopulation().subList(0, elitismCount));

		while (getPopulation().size() < hyperparameters.getPopulationSize()) {
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
				getPopulation().add(child);
			}
		}
		this.setPopulation(newPopulation);
		this.curGeneration++;
	}


	private List<NeuralNetwork> crossover(NeuralNetwork parent1, NeuralNetwork parent2){
		int size = parent1.getChromossomeSize();
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
		List<NeuralNetwork> selected = new ArrayList<>();
		for (int i = 0; i < hyperparameters.getTournamentSize(); i++) {
			selected.add(getPopulation().get(randomObject.nextInt(getPopulation().size())));
		}
		selected.sort(Comparator.comparingDouble(NeuralNetwork::getFitness));
		return selected.get(0);
	}


	public void mutate(NeuralNetwork neuralNetwork) {
		// select two random genes to swap
		int gene1 = randomObject.nextInt(neuralNetwork.getChromossomeSize());
		int gene2 = randomObject.nextInt(neuralNetwork.getChromossomeSize());

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

	public List<NeuralNetwork> getPopulation() {
		return population;
	}

	public void setPopulation(List<NeuralNetwork> population) {
		this.population = population;
	}
}
