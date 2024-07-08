import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Map {
	private boolean[][] map;
	private BufferedImage img_map;
	private BufferedImage compositeImage;
	Drone drone;
	Point drone_start_point;
	private List<Rectangle> tables = new ArrayList<>();

	public Map(String path, Point drone_start_point) {
		try {
			this.drone_start_point = drone_start_point;
			img_map = ImageIO.read(new File(path));
			this.map = render_map_from_image_to_boolean(img_map);
			createCompositeMap(); // Create the composite image initially
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean[][] render_map_from_image_to_boolean(BufferedImage map_img) {
		int w = map_img.getWidth();
		int h = map_img.getHeight();
		boolean[][] map = new boolean[w][h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int clr = map_img.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				if (red != 0 && green != 0 && blue != 0) { // think black
					map[x][y] = true;
				}
			}
		}
		return map;
	}

	boolean isCollide(int x, int y) {
		return !map[x][y];
	}

	// Method to add a table (rectangle area)
	public void addTable(int x, int y, int width, int height) {
		tables.add(new Rectangle(x, y, width, height));
		createCompositeMap(); // Update the composite image when a table is added
	}

	private void createCompositeMap() {
		// Create a copy of the original image to draw the tables on
		compositeImage = new BufferedImage(img_map.getWidth(), img_map.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = compositeImage.createGraphics();
		g2d.drawImage(img_map, 0, 0, null);

		// Draw the tables on the composite image
		g2d.setColor(new Color(120, 120, 120)); // Example gray color for tables
		for (Rectangle table : tables) {
			g2d.fillRect(table.x, table.y, table.width, table.height);
		}
		g2d.dispose();
	}

	public boolean isAboveGrayerColor(int x, int y) {
		// Adjust coordinates
		x = x + 92;
		y = y + 100;

		// Use the composite image for color detection
		if (x >= 0 && x < compositeImage.getWidth() && y >= 0 && y < compositeImage.getHeight()) {
			int clr = compositeImage.getRGB(x, y);
			int red = (clr & 0x00ff0000) >> 16;
			int green = (clr & 0x0000ff00) >> 8;
			int blue = clr & 0x000000ff;

			boolean isGrayer = (red >= 100 && red <= 150) && (green >= 100 && green <= 150) && (blue >= 100 && blue <= 150);

//			System.out.println("Checking point (" + x + ", " + y + "): R=" + red + ", G=" + green + ", B=" + blue + ", isGrayer=" + isGrayer);

			return isGrayer;
		} else {
			System.out.println("Point (" + x + ", " + y + ") is out of bounds");
		}
		return false;
	}

	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.GRAY);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (!map[i][j]) {
					g.drawLine(i, j, i, j);
				}
			}
		}
		// Draw tables in a grayer color
		g.setColor(Color.DARK_GRAY);
		for (Rectangle table : tables) {
			g.fillRect(table.x, table.y, table.width, table.height);
		}
		g.setColor(c);
	}

	// Rectangle class to define table areas
	private static class Rectangle {
		int x, y, width, height;

		Rectangle(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
}
