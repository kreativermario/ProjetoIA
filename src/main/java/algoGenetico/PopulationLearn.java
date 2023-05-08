package algoGenetico;

import nn.NeuralNetwork;
import space.Board;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PopulationLearn {

	List<NeuralNetwork> population = new LinkedList<>();


	public void startSim() {
		//createPopulation(); //é basicamente o código do main
		//currentPopulation = selectFit(); // é o fim do código do main
		while (gens < NR_GENERATIONS)
			createNewGen();
		System.out.println("Final Population:\n" + this);
	}

	public List<Board> selectFit(){
		Collections.sort(population);
		return population.subList(0, NR_INDIV_FIT); //vai buscar os FIT_PRCTG individuos mais fit
	}

	public void createNewGen(){
		//crossPop();
		for (NeuralNetwork nn: population) {
			if (Math.random()<MUTATION_PROB)
				//mutateBoard(nn);
		}
		//uncomment para debug
		//System.out.println("\n\n----------POPULATION NR " + gens + "--------------\n" + this);
	}


	/*
	public void crossPop(){


        //adiciona a populacao até que esta chegue a NR_INDIVIDUOS

		while (population.size() < NR_INDIVIDUALS) {
			//pick 2 boards (parents) from fit population list
			Board firstRand = population.get((int) (Math.random() * NR_INDIV_FIT));
			Board secondRand = population.get((int) (Math.random() * NR_INDIV_FIT));

			//make sure they are different
			while (firstRand.equals(secondRand)) {
				secondRand = population.get((int) (Math.random() * NR_INDIV_FIT));
			}

			//select random gene (between 0 and board size)
			int random = (int) (Math.random() * Board.SIZE);

			//get first part of genome from one parent and the second part of genome from second parent
			List<Integer> firstGenes = firstRand.getBoard().subList(0, random);
			List<Integer> secondGenes = secondRand.getBoard().subList(random, Board.SIZE);

			//create new board with those genes
			Board filho= new Board(firstGenes, secondGenes);                        //just for debug
			population.add(filho);

			//uncomment fot debug
            /*System.out.println("\n\n\n " + filho + "\n\n\n");
            System.out.println("novo board: " + firstRand + " Mixed with " + secondRand + "has index: " + population.indexOf(filho) + "\n\n\n");
            */

		}
		this.gens++;
	}*/

		/*
	public void mutateBoard(NeuralNetwork ){
		//select random gene (between 0 and board size)
		int random = (int) (Math.random() * Board.SIZE);
		//replace it with a random number between 0 e board size tb (mas é outro int)
		nn.setController(random, (int) (Math.random() * Board.SIZE));

		//uncomment for debug
		//System.out.println(population.indexOf(board) + " mutated");
	}
	*/

	//TODO: definir o que é fit no board (jogador ganha pontos por matar inimigos, por exemplo)
	//TODO: definir o que é fit na rede neural (quanto mais tempo o jogador sobrevive, mais fit é a rede)
	//TODO: adaptar para usar os genes da rede neuronal (pesos)
}
