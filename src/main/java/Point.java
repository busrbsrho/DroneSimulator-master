import java.text.DecimalFormat;

public class Point {
	public double x;
	public double y;
	
	public Point(double x,double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public Point() {
		x = 0;
		y = 0;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		
		return " x: " + df.format(x) + " , y: " + df.format(y) ;
	}

	public int getX() {
		return (int)this.x;
	}

	public int getY() {
		return (int)this.y;
	}
}
