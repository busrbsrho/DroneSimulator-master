import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QLearningDrone {
    private static final int GRID_SIZE = 10; // Example grid size
    private static final double ALPHA = 0.1; // Learning rate
    private static final double GAMMA = 0.9; // Discount factor
    private static final double EPSILON = 0.2; // Exploration rate

    private int startX, startY;
    private int currentX, currentY;
    private Map<String, Double> qTable;
    private Random random;

    public QLearningDrone(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        this.currentX = startX;
        this.currentY = startY;
        this.qTable = new HashMap<>();
        this.random = new Random();
    }

    public void moveTo(int x, int y) {
        this.currentX = x;
        this.currentY = y;
    }

    private String getState() {
        return currentX + "," + currentY;
    }

    private String getAction(int action) {
        switch (action) {
            case 0: return "UP";
            case 1: return "DOWN";
            case 2: return "LEFT";
            case 3: return "RIGHT";
            default: return "";
        }
    }

    private int[] performAction(int action) {
        int newX = currentX, newY = currentY;
        switch (action) {
            case 0: newY++; break; // UP
            case 1: newY--; break; // DOWN
            case 2: newX--; break; // LEFT
            case 3: newX++; break; // RIGHT
        }
        return new int[]{newX, newY};
    }

    private double getReward(int x, int y) {
        if (x == startX && y == startY) {
            return 100.0;
        } else {
            return -1.0;
        }
    }

    private double getMaxQ(String state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        for (int action = 0; action < 4; action++) {
            String key = state + ":" + getAction(action);
            maxQ = Math.max(maxQ, qTable.getOrDefault(key, 0.0));
        }
        return maxQ;
    }

    public void train(int episodes) {
        for (int episode = 0; episode < episodes; episode++) {
            while (!(currentX == startX && currentY == startY)) {
                String state = getState();
                int action;
                if (random.nextDouble() < EPSILON) {
                    action = random.nextInt(4);
                } else {
                    action = getBestAction(state);
                }

                int[] newPos = performAction(action);
                int newX = newPos[0], newY = newPos[1];

                double reward = getReward(newX, newY);
                String newState = newX + "," + newY;

                String key = state + ":" + getAction(action);
                double oldQ = qTable.getOrDefault(key, 0.0);
                double newQ = oldQ + ALPHA * (reward + GAMMA + getMaxQ(newState) - oldQ);
                qTable.put(key, newQ);

                moveTo(newX, newY);
            }

            // Reset to start position for next episode
            moveTo(startX, startY);
        }
    }

    private int getBestAction(String state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        int bestAction = 0;
        for (int action = 0; action < 4; action++) {
            String key = state + ":" + getAction(action);
            double qValue = qTable.getOrDefault(key, 0.0);
            if (qValue > maxQ) {
                maxQ = qValue;
                bestAction = action;
            }
        }
        return bestAction;
    }

    public static void main(String[] args) {
        QLearningDrone drone = new QLearningDrone(0, 0);
        drone.train(1000);
        System.out.println("Training completed");
    }
}
