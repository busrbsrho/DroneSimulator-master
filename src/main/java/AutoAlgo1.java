import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.util.logging.Logger;


public class AutoAlgo1 {
	boolean isAboveTable = false;
	int map_size = 3000;
	enum PixelState {blocked,explored,unexplored,visited};
	PixelState map[][];
	Drone drone;
	Point droneStartingPoint;
	Barometer barometer=new Barometer(0,0.1);

	ArrayList<Point> points;
	Timer timer = new Timer();

	int isRotating;
	ArrayList<Double> degrees_left;
	ArrayList<Func> degrees_left_func;

	boolean isSpeedUp = false;

	Graph mGraph = new Graph();
	private Map realmap;
	CPU ai_cpu;
	public AutoAlgo1(Map realMap) {
		degrees_left = new ArrayList<>();
		degrees_left_func =  new ArrayList<>();
		points = new ArrayList<Point>();
		drone = new Drone(realMap);
		this.realmap=realMap;
		if (SimulationWindow.simu_1==true) {
			drone.addLidar(0);
			drone.addLidar(90);
			drone.addLidar(-90);
			drone.addLidar(180);
		}
		if (SimulationWindow.simu_2==true)
		{
			drone.addLidar(0);
			drone.addLidar(90);
			drone.addLidar(-90);
			drone.addLidar(-30);
			drone.addLidar(30);
		}
		if (SimulationWindow.simu_3==true)
		{
			drone.addLidar(0);
			drone.addLidar(90);
			drone.addLidar(-90);
			drone.addLidar(100);
			drone.addLidar(-100);
		}

		initMap();


		isRotating = 0;
		ai_cpu = new CPU(200,"Auto_AI");
		ai_cpu.addFunction(this::update);
	}

	public void initMap() {
		map = new PixelState[map_size][map_size];
		for(int i=0;i<map_size;i++) {
			for(int j=0;j<map_size;j++) {
				map[i][j] = PixelState.unexplored;
			}
		}

		droneStartingPoint = new Point(map_size/2,map_size/2);
	}

	public void play() {
		drone.play();
		ai_cpu.play();
	}


	public void update(int deltaTime) {
		drone.adjustSpeedBasedOnLidar(); // Adjust speed based on Lidar before performing other operations

		updateVisited();
		updateMapByLidars();

		ai(deltaTime);

		if(isRotating != 0) {
			updateRotating(deltaTime);
		}
	}


	public void speedUp() {
		isSpeedUp = true;
	}

	public void speedDown() {
		isSpeedUp = false;
	}

	public void updateMapByLidars() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);

		for(int i=0;i<drone.lidars.size();i++) {
			Lidar lidar = drone.lidars.get(i);
			double rotation = drone.getGyroRotation() + lidar.degrees;
			//rotation = Drone.formatRotation(rotation);
			for(int distanceInCM=0;distanceInCM < lidar.current_distance;distanceInCM++) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, distanceInCM);
				setPixel(p.x,p.y,PixelState.explored);
			}

			if(lidar.current_distance > 0 && lidar.current_distance < WorldParams.lidarLimit - WorldParams.lidarNoise) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, lidar.current_distance);
				setPixel(p.x,p.y,PixelState.blocked);
				//fineEdges((int)p.x,(int)p.y);
			}


		}


	}

	public void updateVisited() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);

		setPixel(fromPoint.x,fromPoint.y,PixelState.visited);

	}

	public void setPixel(double x, double y,PixelState state) {
		int xi = (int)x;
		int yi = (int)y;

		if(state == PixelState.visited) {
			map[xi][yi] = state;
			return;
		}

		if(map[xi][yi] == PixelState.unexplored) {
			map[xi][yi] = state;
		}
	}
	/*

	public void fineEdges(int x,int y) {
		int radius = 6;

		for(int i=y-radius;i<y+radius;i++) {
			for(int j=x-radius;j<x+radius;j++) {
				if(Math.abs(y-i) <= 1 && Math.abs(x-j) <= 1) {
					continue;
				}
				if(map[i][j] == PixelState.blocked) {
					blockLine(x,y,j,i);
				}
			}
		}
	}
	*/
	/*
	public void blockLine(int x0,int y0,int x1,int y1) {
		if(x0 > x1) {
			int tempX = x0;
			int tempY = y0;
			x0 = x1;
			y0 = y1;
			x1 = tempX;
			y1 = tempY;
		}

	     double deltax = x1 - x0;
	     double deltay = y1 - y0;
	     double deltaerr = Math.abs(deltay / deltax);    // Assume deltax != 0 (line is not vertical),
	     double error = 0.0; // No error at start
	     int y = y0;
	     for (int x=x0;x<x1;x++) {
	    	 setPixel(x,y,PixelState.blocked);
	         error = error + deltaerr;
	         if( 2*error >= deltax ) {
                y = y + 1;
                error=error - deltax;
	        }
	     }

	}
	*/

	public void paintBlindMap(Graphics g) {
		Color c = g.getColor();

		int i = (int)droneStartingPoint.y - (int)drone.startPoint.x;
		int startY = i;
		for(;i<map_size;i++) {
			int j = (int)droneStartingPoint.x - (int)drone.startPoint.y;
			int startX = j;
			for(;j<map_size;j++) {
				if(map[i][j] != PixelState.unexplored)  {
					if(map[i][j] == PixelState.blocked) {
						g.setColor(Color.RED);
					}
					else if(map[i][j] == PixelState.explored) {
						g.setColor(Color.YELLOW);
					}
					else if(map[i][j] == PixelState.visited) {
						g.setColor(Color.BLUE);
					}
					g.drawLine(i-startY, j-startX, i-startY, j-startX);
				}
			}
		}
		g.setColor(c);
	}

	public void paintPoints(Graphics g) {
		for(int i=0;i<points.size();i++) {
			Point p = points.get(i);
			g.drawOval((int)p.x + (int)drone.startPoint.x - 10, (int)p.y + (int)drone.startPoint.y-10, 20, 20);
		}

	}

	public void paint(Graphics g) {
		if(SimulationWindow.toogleRealMap) {
			drone.realMap.paint(g);
		}

		paintBlindMap(g);
		paintPoints(g);

		drone.paint(g);


	}

	boolean is_init = true;

	int leftOrRight = 1;


	double max_rotation_to_direction = 20;
	boolean  is_finish = true;
	boolean isLeftRightRotationEnable = true;


	boolean is_risky = false;
	int max_risky_distance = 150;
	boolean try_to_escape = false;
	double  risky_dis = 0;
	int max_angle_risky = 10;

	boolean is_lidars_max = false;

	double save_point_after_seconds = 3;

	double max_distance_between_points = 70;

	boolean start_return_home = false;

	Point init_point;



	private final Set<Point> visitedPoints = new HashSet<>();

	public void ai(int deltaTime) {
		Random rand = new Random();

		if (!SimulationWindow.toogleAI) {
			return;
		}

		if (is_init) {
			speedUp();
			Point dronePoint = drone.getOpticalSensorLocation();
			init_point = new Point(dronePoint);
			points.add(dronePoint);
			mGraph.addVertex(dronePoint);
			is_init = false;
		}

		Point dronePoint = drone.getOpticalSensorLocation();
		if (drone.battery.getBatteryLife() / 5 * 100 <= 60) {
			SimulationWindow.return_home = true;
		}

		if (drone.battery.getBatteryLife() / 5 * 100 < 0.05) {
			CPU.stopAllCPUS();
		}

		for (Lidar lidar : drone.lidars) {
			// Adjust altitude based on lidar sensing and grayer area
			if (lidar.isSensingGrayerArea() && drone.barometer.readAltitude() < 2) {
				drone.barometer.setAltitude(drone.barometer.getAltitude() + 0.2); // Increase altitude
			} else if ((lidar.isSensingGrayerArea() || realmap.isAboveGrayerColor((int) dronePoint.x, (int) dronePoint.y)) && drone.barometer.readAltitude() == 2) {
				drone.barometer.setAltitude(2);
			} else if (!lidar.isSensingGrayerArea() && !realmap.isAboveGrayerColor((int) dronePoint.x, (int) dronePoint.y) && drone.barometer.readAltitude() > 1.1) {
				drone.barometer.setAltitude(drone.barometer.readAltitude() - 0.1);
			}
		}

		if (SimulationWindow.return_home) {
			Point lastPoint = getLastPoint();
			double maxDistanceBetweenPoints = max_distance_between_points + rand.nextGaussian() * max_distance_between_points * 0.06; // Adding some noise

			if (Tools.getDistanceBetweenPoints(lastPoint, dronePoint) < maxDistanceBetweenPoints) {
				if (points.size() <= 1 && Tools.getDistanceBetweenPoints(lastPoint, dronePoint) < max_distance_between_points / 5) {
					speedDown();
					System.out.println("drone is home");
					drone.battery.setBatteryLifeMinutes(5);
					SimulationWindow.return_home = false; // Reset return home flag
					points.clear(); // Clear points to look for new ones
					is_init = true; // Reinitialize for new points
				} else {
					removeLastPoint();
				}
			}
		} else {
			if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >= max_distance_between_points) {
				// Check if the point has already been visited
				if (!visitedPoints.contains(dronePoint)) {
					points.add(dronePoint);
					mGraph.addVertex(dronePoint);
					visitedPoints.add(dronePoint); // Add new point to visited points list
				}
			}
		}


		if (!is_risky) {
			if (SimulationWindow.simu_1==true) {
				Lidar lidar = drone.lidars.get(0);
				if (lidar.current_distance <= max_risky_distance) {
					is_risky = true;
					risky_dis = lidar.current_distance;
				}

				Lidar lidar1 = drone.lidars.get(1);
				if (lidar1.current_distance <= max_risky_distance / 3) {
					is_risky = true;
				}

				Lidar lidar2 = drone.lidars.get(2);
				if (lidar2.current_distance <= max_risky_distance / 3) {
					is_risky = true;
				}
			}else if (SimulationWindow.simu_2==true || SimulationWindow.simu_3==true) {

				Lidar lidar = drone.lidars.get(0);
				if (lidar.current_distance <= max_risky_distance && !lidar.isSensingGrayerArea()) {
					is_risky = true;
					risky_dis = lidar.current_distance;
				}

				Lidar lidar1 = drone.lidars.get(1);
				if (lidar1.current_distance <= max_risky_distance / 3&& !lidar1.isSensingGrayerArea()) {
					is_risky = true;
				}

				Lidar lidar2 = drone.lidars.get(2);
				if (lidar2.current_distance <= max_risky_distance / 3&& !lidar2.isSensingGrayerArea()) {
					is_risky = true;
				}

				Lidar lidar3 = drone.lidars.get(3);
				if (lidar2.current_distance <= max_risky_distance / 3&& !lidar3.isSensingGrayerArea()) {
					is_risky = true;
				}
				Lidar lidar4 = drone.lidars.get(4);
				if (lidar2.current_distance <= max_risky_distance / 3&& !lidar4.isSensingGrayerArea()) {
					is_risky = true;
				}
			}







		} else {
			if (!try_to_escape) {
				try_to_escape = true;
				Lidar lidar1 = drone.lidars.get(1);
				double a = lidar1.current_distance;

				Lidar lidar2 = drone.lidars.get(2);
				double b = lidar2.current_distance;

				int spin_by = max_angle_risky;

				if (a > 270 && b > 270) {
					is_lidars_max = true;
					Point l1 = Tools.getPointByDistance(dronePoint, lidar1.degrees + drone.getGyroRotation(), lidar1.current_distance);
					Point l2 = Tools.getPointByDistance(dronePoint, lidar2.degrees + drone.getGyroRotation(), lidar2.current_distance);
					Point last_point = getAvgLastPoint();
					double dis_to_lidar1 = Tools.getDistanceBetweenPoints(last_point, l1);
					double dis_to_lidar2 = Tools.getDistanceBetweenPoints(last_point, l2);

					if (SimulationWindow.return_home) {
						if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) < max_distance_between_points) {
							removeLastPoint();
						}
					} else {
						if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >= max_distance_between_points) {
							// Check if the point has already been visited
							if (!visitedPoints.contains(dronePoint)) {
								points.add(dronePoint);
								mGraph.addVertex(dronePoint);
								visitedPoints.add(dronePoint); // Add new point to visited points list
							}
						}
					}

					spin_by = 90;
					if (SimulationWindow.return_home) {
						spin_by *= -1;
					}

					if (dis_to_lidar1 < dis_to_lidar2) {
						spin_by *= (-1);
					}
				} else {
					if (a < b) {
						spin_by *= (-1);
					}
				}

				spinBy(spin_by, true, new Func() {
					@Override
					public void method() {
						try_to_escape = false;
						is_risky = false;
					}
				});
			}
		}
	}



	int counter = 0;

	public void doLeftRight() {
		if(is_finish) {
			leftOrRight *= -1;
			counter++;
			is_finish = false;

			spinBy(max_rotation_to_direction*leftOrRight,false,new Func() {
				@Override
				public void method() {
					is_finish = true;
				}
			});
		}
	}


	double lastGyroRotation = 0;
	public void updateRotating(int deltaTime) {

		if(degrees_left.size() == 0) {
			return;
		}

		double degrees_left_to_rotate = degrees_left.get(0);
		boolean isLeft = true;
		if(degrees_left_to_rotate > 0) {
			isLeft = false;
		}

		double curr =  drone.getGyroRotation();
		double just_rotated = 0;

		if(isLeft) {

			just_rotated = curr - lastGyroRotation;
			if(just_rotated > 0) {
				just_rotated = -(360 - just_rotated);
			}
		} else {
			just_rotated = curr - lastGyroRotation;
			if(just_rotated < 0) {
				just_rotated = 360 + just_rotated;
			}
		}



		lastGyroRotation = curr;
		degrees_left_to_rotate-=just_rotated;
		degrees_left.remove(0);
		degrees_left.add(0,degrees_left_to_rotate);

		if((isLeft && degrees_left_to_rotate >= 0) || (!isLeft && degrees_left_to_rotate <= 0)) {
			degrees_left.remove(0);

			Func func = degrees_left_func.get(0);
			if(func != null) {
				func.method();
			}
			degrees_left_func.remove(0);


			if(degrees_left.size() == 0) {
				isRotating = 0;
			}
			return;
		}

		int direction = (int)(degrees_left_to_rotate / Math.abs(degrees_left_to_rotate));
		drone.rotateLeft(deltaTime * direction);

	}

	public void spinBy(double degrees,boolean isFirst,Func func) {
		lastGyroRotation = drone.getGyroRotation();
		if(isFirst) {
			degrees_left.add(0,degrees);
			degrees_left_func.add(0,func);


		} else {
			degrees_left.add(degrees);
			degrees_left_func.add(func);
		}

		isRotating =1;
	}

	public void spinBy(double degrees,boolean isFirst) {
		lastGyroRotation = drone.getGyroRotation();
		if(isFirst) {
			degrees_left.add(0,degrees);
			degrees_left_func.add(0,null);


		} else {
			degrees_left.add(degrees);
			degrees_left_func.add(null);
		}

		isRotating =1;
	}

	public void spinBy(double degrees) {
		lastGyroRotation = drone.getGyroRotation();

		degrees_left.add(degrees);
		degrees_left_func.add(null);
		isRotating = 1;
	}

	public Point getLastPoint() {
		if(points.size() == 0) {
			return init_point;
		}

		Point p1 = points.get(points.size()-1);
		return p1;
	}

	public Point removeLastPoint() {
		if(points.isEmpty()) {
			return init_point;
		}

		return points.remove(points.size()-1);
	}


	public Point getAvgLastPoint() {
		if(points.size() < 2) {
			return init_point;
		}

		Point p1 = points.get(points.size()-1);
		Point p2 = points.get(points.size()-2);
		return new Point((p1.x + p2.x) /2, (p1.y + p2.y) /2);
	}



	public void updatePosition( double dAltitude) {

		this.drone.altitude += dAltitude;
		drone.barometer.setAltitude(this.drone.altitude);
	}

	public void printStatus() {
		System.out.println("Barometer reading: " + drone.barometer.readAltitude());
	}

	public void takeOffTo(double targetAltitude) {


		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (drone.altitude < targetAltitude) {
					drone.altitude+=0.003;
					drone.barometer.setAltitude(drone.altitude);

				}
			}
		}, 0, 10);
	}






}
