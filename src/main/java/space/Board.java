package space;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import controllers.GameController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.sprite.Alien;
import space.sprite.Player;
import space.sprite.Shot;

public class Board extends JPanel {

	private Logger logger = LoggerFactory.getLogger(Board.class);

	private Dimension d;
	private List<Alien> aliens;
	private Player player;
	private Shot shot;

	private int direction = -1;
	private int deaths = 0;

	private boolean inGame = true;
	private String explImg = "src/images/explosion.png";
	private String message = "Game Over";

	private Timer timer;
	private int time;

	double[] state;

	private GameController controller;
	private boolean headLess = false;
	Random generator = new Random();

	public void setSeed(long seed) {
		generator.setSeed(seed);
	}

	public Board() {
		initBoard();
		gameInit();
	}

	public Board(GameController controller) {
		this.headLess = true;
		this.controller = controller;
		gameInit();
	}

	private void initBoard() {

//		addKeyListener(new TAdapter());
		setFocusable(true);
		d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
		setBackground(Color.black);

		timer = new Timer(Commons.DELAY, new GameCycle());
		timer.start();

		gameInit(); //está duplicado.....
	}

	private void gameInit() {

		aliens = new ArrayList<>();

		for (int i = 0; i < Commons.NUMBER_OF_LINES; i++) {
			for (int j = 0; j < Commons.NUMBER_OF_ALIENS_TO_DESTROY / Commons.NUMBER_OF_LINES; j++) {

				var alien = new Alien(Commons.ALIEN_INIT_X + 18 * j, Commons.ALIEN_INIT_Y + 18 * i);
				aliens.add(alien);
			}
		}

		player = new Player();
		shot = new Shot();
	}

	private void drawAliens(Graphics g) {

		for (Alien alien : aliens) {

			if (alien.isVisible()) {

				g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
			}

			if (alien.isDying()) {

				alien.die();
			}
		}
	}

	private void drawPlayer(Graphics g) {

		if (player.isVisible()) {

			g.drawImage(player.getImage(), player.getX(), player.getY(), this);
		}

		if (player.isDying()) {

			player.die();
			inGame = false;
		}
	}

	private void drawShot(Graphics g) {

		if (shot.isVisible()) {

			g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
		}
	}

	private void drawBombing(Graphics g) {

		for (Alien a : aliens) {

			Alien.Bomb b = a.getBomb();

			if (!b.isDestroyed()) {

				g.drawImage(b.getImage(), b.getX(), b.getY(), this);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);
	}

	private void doDrawing(Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.green);

		if (inGame) {

			g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);

			drawAliens(g);
			drawPlayer(g);
			drawShot(g);
			drawBombing(g);

		} else {

			if (timer.isRunning()) {
				timer.stop();
			}

			gameOver(g);
		}

		Toolkit.getDefaultToolkit().sync();
	}

	private void gameOver(Graphics g) {

		g.setColor(Color.black);
		g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

		g.setColor(new Color(0, 32, 48));
		g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
		g.setColor(Color.white);
		g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);

		var small = new Font("Helvetica", Font.BOLD, 14);
		var fontMetrics = this.getFontMetrics(small);

		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(message + "-->" + getFitness(), (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
				Commons.BOARD_WIDTH / 2);
	}

	public BufferedImage createImage(JPanel panel) {

		int w = panel.getWidth();
		int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		panel.paint(g);
		g.dispose();
		return bi;
	}

	private double[] createState() {
		double[] state = new double[aliens.size() * 3 * 2 + 1 + 3];
		int index = 0;
		for (Alien a : aliens) {
			state[index++] = (a.getX() * 1.0) / Commons.BOARD_WIDTH;
			state[index++] = (a.getY() * 1.0) / Commons.BOARD_HEIGHT;
			state[index++] = a.isDying() ? -1 : 1;
		}
		for (Alien a : aliens) {
			// state[index++] = a.getBomb().isDestroyed()?-1:1;
			if (!a.getBomb().isDestroyed()) {
				state[index++] = (a.getBomb().getX() * 1.0) / Commons.BOARD_WIDTH;
				state[index++] = (a.getBomb().getY() * 1.0) / Commons.BOARD_HEIGHT;
			}
		}
		state[index++] = (player.getX() * 1.0) / Commons.BOARD_WIDTH;
		if( !shot.isDying()) {
			state[index++] = (shot.getX() * 1.0) / Commons.BOARD_WIDTH;
			state[index++] = (shot.getY() * 1.0) / Commons.BOARD_HEIGHT;
			//state[index++] = shot.isDying() ? -1 : 1;
		}
		
		return state;
	}

	private void update() {
		time++;
		if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {

			inGame = false;
			if (!headLess)
				timer.stop();
			message = "Game won!";
		}

		// player

		double[] d = createState();
		double[] output = controller.nextMove(d);

		player.act(output);
		if (output[3] > 0.5) { //pq [3]?
			if (inGame) {
				//logger.warn("Shooting...");
				if (!shot.isVisible()) {
					shot = new Shot(player.getX(), player.getY());
				}
			}
		}

		// shot
		if (shot.isVisible()) {

			int shotX = shot.getX();
			int shotY = shot.getY();

			for (Alien alien : aliens) {

				int alienX = alien.getX();
				int alienY = alien.getY();

				if (alien.isVisible() && shot.isVisible()) {
					if (shotX >= (alienX) && shotX <= (alienX + Commons.ALIEN_WIDTH) && shotY >= (alienY)
							&& shotY <= (alienY + Commons.ALIEN_HEIGHT)) {

						var ii = new ImageIcon(explImg);
						alien.setImage(ii.getImage());
						alien.setDying(true);
						deaths++;
						shot.die();
					}
				}
			}

			int y = shot.getY();
			y -= 4;

			if (y < 0) {
				shot.die();
			} else {
				shot.setY(y);
			}
		}

		// aliens

		for (Alien alien : aliens) {

			int x = alien.getX();

			if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {

				direction = -1;

				Iterator<Alien> i1 = aliens.iterator();

				while (i1.hasNext()) {

					Alien a2 = i1.next();
					a2.setY(a2.getY() + Commons.GO_DOWN);
				}
			}

			if (x <= Commons.BORDER_LEFT && direction != 1) {

				direction = 1;

				Iterator<Alien> i2 = aliens.iterator();

				while (i2.hasNext()) {

					Alien a = i2.next();
					a.setY(a.getY() + Commons.GO_DOWN);
				}
			}
			if (alien.isDying()) {

				alien.die();
			}

		}

		Iterator<Alien> it = aliens.iterator();

		while (it.hasNext()) {

			Alien alien = it.next();

			if (alien.isVisible()) {

				int y = alien.getY();

				if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
					inGame = false;
					message = "Invasion!";
				}

				alien.act(direction);
			}
		}

		// bombs

		for (Alien alien : aliens) {

			int shot = generator.nextInt(400);
			int cornerShot = 0;

			if (alien.getX() == Commons.BOARD_WIDTH - Commons.BORDER_RIGHT || alien.getX() == Commons.BORDER_LEFT) {
				cornerShot = generator.nextInt(10); //alterar p 1 para disparar sempre nos cantos
			}

			Alien.Bomb bomb = alien.getBomb();

			if ((cornerShot == Commons.CHANCE || shot == Commons.CHANCE || alien.getX() == player.getX()) && alien.isVisible() && bomb.isDestroyed()) {

				bomb.setDestroyed(false);
				bomb.setX(alien.getX());
				bomb.setY(alien.getY());
			}

			int bombX = bomb.getX();
			int bombY = bomb.getY();
			int playerX = player.getX();
			int playerY = player.getY();

			if (player.isVisible() && !bomb.isDestroyed()) {

				if (bombX >= (playerX) && bombX <= (playerX + Commons.PLAYER_WIDTH) && bombY >= (playerY)
						&& bombY <= (playerY + Commons.PLAYER_HEIGHT)) {

					ImageIcon ii = new ImageIcon(explImg);
					player.setImage(ii.getImage());
					player.setDying(true);
					bomb.setDestroyed(true);
				}
			}

			if (!bomb.isDestroyed()) {

				bomb.setY(bomb.getY() + 1);

				if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {

					bomb.setDestroyed(true);
				}
			}
		}
		if (player.isDying()) {

			player.die();
			inGame = false;
		}

	}

	public int getDeaths() {
		return deaths;
	}

	public int getTime() {
		return time;
	}

	private void doGameCycle() {

		update();
		repaint();
	}

	private class GameCycle implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			doGameCycle();
		}
	}

	public void run() {
		while (inGame) {
			update();
		}
	}

	public Double getFitness() {
		double fitness = (double) (getDeaths() * 10000 + getTime());
		// System.out.println(fitness);
		return fitness;
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}
}
