package org.ayosynk.voidBounty;

public class JoinMessage {

    private double min;
    private double max;
    private String message;

    public JoinMessage(double min, double max, String message) {
        this.min = min;
        this.max = max;
        this.message = message;
    }

    // Getter for the min value
    public double getMin() {
        return min;
    }

    // Setter for the min value
    public void setMin(double min) {
        this.min = min;
    }

    // Getter for the max value
    public double getMax() {
        return max;
    }

    // Setter for the max value
    public void setMax(double max) {
        this.max = max;
    }

    // Getter for the message
    public String getMessage() {
        return VoidBounty.ChatUtils.colorize(message);
    }

    // Setter for the message
    public void setMessage(String message) {
        this.message = message;
    }
}
