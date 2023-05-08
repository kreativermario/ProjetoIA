package space;

import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.JFrame;

import algoGenetico.PopulationEvo;
import controllers.GameController;
import nn.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceInvaders extends JFrame {

	private static Logger logger = LoggerFactory.getLogger(SpaceInvaders.class);
	private Board board;

	public SpaceInvaders() {

		initUI();

	}

	private void initUI() {
		board = new Board();
		add(board);

		setTitle("Space Invaders");
		setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {

		PopulationEvo populationEvo = new PopulationEvo();
		populationEvo.start();
		try {
			populationEvo.join();
			NeuralNetwork fittest = populationEvo.getFittest();
			logger.info("BEST FITNESS: {}", fittest.getFitness());
			logger.info(Arrays.toString(fittest.getChromossome()));
			EventQueue.invokeLater(() -> {
				showControllerPlaying(fittest, PopulationEvo.SEED);
			});

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}



	public static void showControllerPlaying(GameController controller, long seed) {
		EventQueue.invokeLater(() -> {

			var ex = new SpaceInvaders();
			ex.setController(controller);
			ex.setSeed(seed);
			ex.setVisible(true);
		});
	}
	
	public void setController(GameController controller) {
		board.setController(controller);
	}

	public void setSeed(long seed) {
		board.setSeed(seed);

	}
}
