public class Barometer {
    private double altitude;
    private double noiseLevel;

    public Barometer(double initialAltitude, double noiseLevel) {
        this.altitude = initialAltitude;
        this.noiseLevel = noiseLevel;
    }

    public double readAltitude() {
        // Simulate barometer noise
        double noise = (Math.random() - 0.5) * 2 * noiseLevel;
        return this.altitude + noise;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getAltitude() {
        return this.altitude;
    }
}