package space;

import java.awt.EventQueue;
import java.io.File;

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
		
		EventQueue.invokeLater(() -> {
			NeuralNetwork fittest = PopulationEvo.importInitialChamp(14,
					new File("src/main/java/algoGenetico/NetworkChamp.txt"));
			showControllerPlaying(fittest, 2708);
		});

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
