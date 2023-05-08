package space;

import java.awt.EventQueue;

import javax.swing.JFrame;

import algoGenetico.PopulationEvo;
import controllers.GameController;
import nn.NeuralNetwork;

public class SpaceInvaders extends JFrame {

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

			var ex = new SpaceInvaders();
			ex.setVisible(true);
			PopulationEvo populationEvo = new PopulationEvo();
			populationEvo.start();
			try {
				populationEvo.join();
				NeuralNetwork fittest = populationEvo.getFittest();
				ex.setController(fittest);
				showControllerPlaying(fittest, 100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
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
