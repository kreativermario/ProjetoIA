package space.sprite;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Commons;

import java.awt.event.KeyEvent;

public class Player extends Sprite {

	private Logger logger = LoggerFactory.getLogger(Player.class);
	private int width;

	public Player() {

		initPlayer();
	}

	private void initPlayer() {

		var playerImg = "src/images/player.png";
		var ii = new ImageIcon(playerImg);

		width = ii.getImage().getWidth(null);
		setImage(ii.getImage());

		int START_X = 270;
		setX(START_X);

		int START_Y = 280;
		setY(START_Y);
	}

	private int maxIndex(double[] output) {
		double max = output[0];
		int maxI = 0;
		for (int i = 1; i < output.length; i++) {
			if (max < output[i]) {
				maxI = i;
				max = output[i];
			}
		}
		return maxI;
	}

	private void applyOrder(double[] output) {
		int key = maxIndex(output);
		if (key == 1) {
			logger.info("Moving Left...");
			dx = -2;
		}
		if (key == 2) {
			logger.info("Moving Right...");
			dx = 2;
		}
		if (key == 0) {
			logger.info("Moving Stop...");
			dx = 0;
		}
	}

	public void act(double[] output) {

		applyOrder(output);

		x += dx;

		if (x <= 2) {

			x = 2;
		}

		if (x >= Commons.BOARD_WIDTH - 2 * width) {

			x = Commons.BOARD_WIDTH - 2 * width;
		}
	}

//	public void keyPressed(KeyEvent e) {
//
//		int key = e.getKeyCode();
//
//		if (key == KeyEvent.VK_LEFT) {
//
//			dx = -2;
//		}
//
//		if (key == KeyEvent.VK_RIGHT) {
//
//			dx = 2;
//		}
//	}
//
//	public void keyReleased(KeyEvent e) {
//
//		int key = e.getKeyCode();
//
//		if (key == KeyEvent.VK_LEFT) {
//
//			dx = 0;
//		}
//
//		if (key == KeyEvent.VK_RIGHT) {
//
//			dx = 0;
//		}
//	}

	public void moveLeft() {
		dx = -2;
	}

	public void moveRight() {
		dx = 2;
	}

	public void stop() {
		dx = 0;
	}
}
