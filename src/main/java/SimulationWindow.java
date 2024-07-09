import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SimulationWindow {

	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationWindow window = new SimulationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SimulationWindow() {
		initialize();
	}

	public static JLabel info_label;
	public static boolean return_home = false;
	public static boolean simu_1 = false;
	public static boolean simu_2 = false;
	public static boolean simu_3 = true;

	boolean toogleStop = true;
	private void initialize() {
		frame = new JFrame();
		frame.setSize(1800,700);
		frame.setTitle("Drone Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);


		/*
		 * Stop\Resume
		 */


		JButton stopBtn = new JButton("Start/Pause");
		stopBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(toogleStop) {
					algo1.drone.battery.stopDraining();
					CPU.stopAllCPUS();


				} else {
					CPU.resumeAllCPUS();
					algo1.drone.battery.startDraining();
					algo1.takeOffTo(1);
				}
				toogleStop = !toogleStop;
			}
		});
		stopBtn.setBounds(1300, 0, 170, 50);
		frame.getContentPane().add(stopBtn);
		/*
		 * Speeds
		 */


		JButton speedBtn1 = new JButton("speedUp");
		speedBtn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.speedUp();
			}
		});
		speedBtn1.setBounds(1300, 100, 100, 50);
		frame.getContentPane().add(speedBtn1);

		JButton speedBtn2 = new JButton("speedDown");
		speedBtn2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.speedDown();
			}
		});
		speedBtn2.setBounds(1400, 100, 100, 50);
		frame.getContentPane().add(speedBtn2);

		/*
		 * Spins
		 */

		JButton spinBtn1 = new JButton("spin180");
		spinBtn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(180);
			}
		});
		spinBtn1.setBounds(1300, 200, 100, 50);
		frame.getContentPane().add(spinBtn1);

		JButton spinBtn2 = new JButton("spin90");
		spinBtn2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(90);
			}
		});
		spinBtn2.setBounds(1400, 200, 100, 50);
		frame.getContentPane().add(spinBtn2);

		JButton spinBtn3 = new JButton("spin60");
		spinBtn3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(60);
			}
		});
		spinBtn3.setBounds(1500, 200, 100, 50);
		frame.getContentPane().add(spinBtn3);

//		JButton spinBtn4 = new JButton("spin45");
//		spinBtn4.addActionListener(new ActionListener()
//		{
//			  public void actionPerformed(ActionEvent e)
//			  {
//				  algo1.spinBy(60);
//			  }
//		});
//		spinBtn4.setBounds(1300, 300, 100, 50);
//		frame.getContentPane().add(spinBtn4);

		JButton spinBtn5 = new JButton("spin45");
		spinBtn5.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(45);
			}
		});
		spinBtn5.setBounds(1400, 300, 100, 50);
		frame.getContentPane().add(spinBtn5);

		JButton spinBtn6 = new JButton("spin-45");
		spinBtn6.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(-45);
			}
		});
		spinBtn6.setBounds(1500, 300, 100, 50);
		frame.getContentPane().add(spinBtn6);

//		JButton spinBtn7 = new JButton("spin-45");
//		spinBtn7.addActionListener(new ActionListener()
//		{
//			  public void actionPerformed(ActionEvent e)
//			  {
//				  algo1.spinBy(-45);
//			  }
//		});
//		spinBtn7.setBounds(1600, 300, 100, 50);
//		frame.getContentPane().add(spinBtn7);

		JButton spinBtn8 = new JButton("spin-60");
		spinBtn8.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.spinBy(-60);
			}
		});
		spinBtn8.setBounds(1700, 300, 100, 50);
		frame.getContentPane().add(spinBtn8);



		//take-off


		JButton takeOffBtn = new JButton("takeoff");
		takeOffBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.takeOffTo(1);
			}
		});
		takeOffBtn.setBounds(1300, 300, 100, 50);
		frame.getContentPane().add(takeOffBtn);


		///

		/*
		 * Toogle real map
		 */

		JButton showBattery = new JButton("show battery");
		showBattery.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				double battery = algo1.drone.battery.getBatteryLife();
				System.out.println(battery/8 * 100);
			}
		});
		showBattery.setBounds(1300, 400, 120, 50);
		frame.getContentPane().add(showBattery);

		/*
		 * Toogle AI
		 */

		JButton toogleAIBtn = new JButton("toogle AI");
		toogleAIBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				toogleAI = !toogleAI;
			}
		});
		toogleAIBtn.setBounds(1400, 400, 120, 50);
		frame.getContentPane().add(toogleAIBtn);

		/*
		 * RETURN TO HOME
		 */


		JButton returnBtn = new JButton("Return Home");
		returnBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				return_home = !return_home;
				algo1.speedDown();
				algo1.spinBy(180, true, new Func() {
					@Override
					public void method() {
						algo1.speedUp();
					}
				});
			}
		});
		returnBtn.setBounds(1500, 400, 120, 50);
		frame.getContentPane().add(returnBtn);

		JButton Graph = new JButton("Open Graph");
		Graph.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algo1.mGraph.drawGraph();
			}
		});
		Graph.setBounds(1600, 400, 120, 50);
		frame.getContentPane().add(Graph);

		/*
		 * Info label
		 */


		info_label = new JLabel();
		info_label.setBounds(1300, 500, 300, 200);
		frame.getContentPane().add(info_label);

		/*
		 * Info label
		 */


		info_label2 = new JLabel();
		info_label2.setBounds(1400, 450, 300, 200);
		frame.getContentPane().add(info_label2);

		main();
	}
	public JLabel info_label2;
	public static boolean toogleRealMap = true;
	public static boolean toogleAI = false;

	public static AutoAlgo1 algo1;



	public void main() {

		int map_num = 5;
		Point[] startPoints = {
				new Point(100,50),
				new Point(50,60),
				new Point(73,68),
				new Point(84,73),
				new Point(92,100)};

		Map map = new Map("Maps/p1" + map_num + ".png",startPoints[map_num-1]);
		// Adding tables
		map.addTable(202, 115, 20, 50); // x, y, width, height
		map.addTable(100, 150, 30, 15);
		map.addTable(200, 115, 60, 15);
		map.addTable(300, 115, 60, 15);
		map.addTable(400, 150, 60, 15);
		map.addTable(192, 330, 15, 60);
		map.addTable(220, 486, 40, 15);
		map.addTable(92, 286, 60, 15);
		map.addTable(92, 286, 60, 15);
		map.addTable(92, 286, 60, 15);




		// Coordinates to check
		int[][] coordinates = {
				{100, 225}, // Table 1
				{150, 160}, // Table 2
				{300, 140}  // Table 3
		};

		for (int[] coord : coordinates) {
			boolean result = map.isAboveGrayerColor(coord[0] - 92, coord[1] - 100);
			System.out.println("Is point (" + coord[0] + ", " + coord[1] + ") above a grayer area? " + result);
		}

		algo1 = new AutoAlgo1(map);

		Painter painter = new Painter(algo1);
		painter.setBounds(0, 0, 2000, 2000);
		frame.getContentPane().add(painter);

		CPU painterCPU = new CPU(200,"painter"); // 60 FPS painter
		painterCPU.addFunction(frame::repaint);
		painterCPU.play();

		algo1.play();

		CPU updatesCPU = new CPU(60,"updates");
		updatesCPU.addFunction(algo1.drone::update);
		updatesCPU.play();

		CPU infoCPU = new CPU(6,"update_info");
		infoCPU.addFunction(this::updateInfo);
		infoCPU.play();
		algo1.drone.battery.startDraining();
		algo1.takeOffTo(1);
		toogleAI= !toogleAI;


	}

	public void updateInfo(int deltaTime) {
		info_label.setText(algo1.drone.getInfoHTML());
		info_label2.setText("<html>" + String.valueOf(algo1.counter) + " <BR>isRisky:" + String.valueOf(algo1.is_risky) +
				"<BR>" + String.valueOf(algo1.risky_dis) + "</html>");

	}

	public void stopCPUS() {
		CPU.stopAllCPUS();
	}

	public void resumseCPUS() {
		CPU.stopAllCPUS();
	}




}
