package space.sprite;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.Commons;

public class Player extends Sprite {

	private Logger logger = LoggerFactory.getLogger(Player.class);
	private int width;
	private int points;
	private int leftCornerTicks = 0;
	private int rightCornerTicks = 0;


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

		points = 0;
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
			//logger.info("Moving Left...");
			dx = -2;
		}
		if (key == 2) {
			//logger.info("Moving Right...");
			dx = 2;
		}
		if (key == 0) {
			//logger.info("Moving Stop...");
			removePoints(Commons.PENALTY_POINTS/2);
			dx = 0;
		}
	}

	public void act(double[] output) {

		applyOrder(output);

		x += dx;
		int cornerZone = (int)((Commons.BOARD_WIDTH-Commons.BORDER_LEFT-Commons.BORDER_RIGHT) * 0.03); // 3% of board width
		int distanceFromCenter = Math.abs((Commons.BOARD_WIDTH-Commons.BORDER_LEFT-Commons.BORDER_RIGHT)/2 - getX());
		// Left hotzone
		if(x <= cornerZone){
			if(x <= 2) x = 2;
			leftCornerTicks++;
			//logger.info("Corner ticks: {}", leftCornerTicks);
			if(leftCornerTicks >= Commons.MAX_CORNER_TICKS) {
				//logger.info("Left corner for too long -> removing points {}", distanceFromCenter);
				removePoints(Commons.PENALTY_POINTS*distanceFromCenter);
				//logger.info("Points: {}", getPoints());
				leftCornerTicks = 0; // reset counter
			}
			return;
		// Right hotzone
		} else if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT - cornerZone - width) {
			if(x >= Commons.BOARD_WIDTH - 2 * width) x = Commons.BOARD_WIDTH - 2 * width;

			rightCornerTicks++;
			//logger.info("Right corner ticks: {}", rightCornerTicks);
			if(rightCornerTicks >= Commons.MAX_CORNER_TICKS) {
				//logger.info("Right corner for too long -> removing points {}", distanceFromCenter);
				removePoints(Commons.PENALTY_POINTS*distanceFromCenter);
				//logger.info("Points: {}", getPoints());
				rightCornerTicks = 0; // reset counter
			}
			return;
		}
		if(dx != 0){
			//logger.info("Moving... adding points");
			addPoints(Commons.POINTS_PER_MOVEMENT);
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


	public void addPoints(int value){
		this.points += value;
	}

	public void removePoints(int value){
		this.points -= value;
	}

	public synchronized int getPoints() {
		return points;
	}

	public int getWidth() {
		return width;
	}
}
