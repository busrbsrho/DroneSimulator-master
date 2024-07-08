import java.util.Timer;
import java.util.TimerTask;

public class Battery {
    private double batteryLifeMinutes;
    private Timer timer;

    public void setBatteryLifeMinutes(double batteryLifeMinutes) {
        this.batteryLifeMinutes = batteryLifeMinutes;
    }

    private boolean isDraining;

    // Constructor to initialize the battery life
    public Battery(int initialBatteryLife) {
        this.batteryLifeMinutes = initialBatteryLife;
        this.timer = new Timer();
        this.isDraining = false;
    }

    // Method to add battery life
    public void addBatteryLife(int minutes) {
        if (minutes > 0) {
            this.batteryLifeMinutes += minutes;
            System.out.println("Added " + minutes + " minutes of battery life.");
        } else {
            System.out.println("Invalid value. Battery life can only be increased by a positive number of minutes.");
        }
    }

    // Method to get the current battery life
    public double getBatteryLife() {
        if(batteryLifeMinutes < 0.05)
            {return 0;}

        return this.batteryLifeMinutes;

    }

    // Method to start draining the battery
    public void startDraining() {
        if (!isDraining) {
            isDraining = true;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (batteryLifeMinutes > 0) {
                        batteryLifeMinutes -= 0.0001;
                    } else {
                        System.out.println("Battery is empty.");
                        stopDraining();
                    }
                }
            }, 0, 10);
        } else {
            System.out.println("Battery is already draining.");
        }
    }

    // Method to stop draining the battery
    public void stopDraining() {
        if (isDraining) {
            timer.cancel();
            timer = new Timer();
            isDraining = false;
            System.out.println("Battery draining stopped.");
        }
    }

}
