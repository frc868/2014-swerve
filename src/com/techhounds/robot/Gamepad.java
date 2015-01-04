package com.techhounds.robot;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Convenience class that does gamepad-related calculations
 * @author John Du
 */
public class Gamepad extends Joystick{
    
    //Axis numbers for the gamepad
    private static final int LEFT_STICK_X = 1;
    private static final int LEFT_STICK_Y = 2;
    private static final int RIGHT_STICK_X = 3;
    private static final int RIGHT_STICK_Y = 4;
    private static final int DPAD_X = 5;
    private static final int DPAD_Y = 6;
    
    /** Port of the gamepad */
    public static final int PORT = 1;
    
    //Constants for the sticks
    public static final int LEFT_STICK = 0;
    public static final int RIGHT_STICK = 1;
    public static final int DPAD = 2;
    
    //Constants for X or Y
    public static final int X = 0;
    public static final int Y = 1;
    
    private static final double MINIMUM_MAGNITUDE_DPAD = 0.5;
    
    /**
     * Constructor, makes the gamepad
     * @param port Port of the gamepad
     */
    public Gamepad(int port){
        super(port);
    }
    
    /**
     * @return X value of the left stick
     */
    public double getLeftStickX(){
        return getXY(LEFT_STICK)[X];
    }
    
    /**
     * @return Y value of the left stick
     */
    public double getLeftStickY(){
        return getXY(LEFT_STICK)[Y];
    }
    
    /**
     * @return X value of the right stick
     */
    public double getRightStickX(){
        return getXY(RIGHT_STICK)[X];
    }
    
    /**
     * @return Y value of the right stick
     */
    public double getRightStickY(){
        return getXY(RIGHT_STICK)[Y];
    }
    
    public boolean getDPadRight(){
        return getXY(DPAD)[X] > MINIMUM_MAGNITUDE_DPAD;
    }
    
    public boolean getDPadLeft(){
        return getXY(DPAD)[X] < -MINIMUM_MAGNITUDE_DPAD;
    }
    
    public boolean getDPadUp(){
        return getXY(DPAD)[Y] > MINIMUM_MAGNITUDE_DPAD;
    }
    
    public boolean getDPadDown(){
        return getXY(DPAD)[Y] < -MINIMUM_MAGNITUDE_DPAD;
    }
    
    /**
     * Calculates angle of a given stick
     * @param stick Stick whose angle you desire
     * @return Angle of given stick
     */
    public double getAngle(int stick){
        double[] xy = getXY(stick);
        
        double angle = MathUtils.atan2(xy[Y], xy[X]);
        if(angle < 0){
            angle += 2 * Math.PI;//required for otherwise negative values
        }
        return angle;
    }
    
    /**
     * Calculates magnitude of a given stick
     * @param stick Stick whose magnitude you desire
     * @return Magnitude of given stick
     */
    public double getMagnitude(int stick){
        double[] xy = getXY(stick);
        double magnitude = Math.sqrt((xy[X] * xy[X]) + (xy[Y] * xy[Y]));
        return magnitude;
    }
    
    /**
     * Scales a magnitude to a new range
     * @param magnitude Magnitude to alter
     * @param min Minimum of magnitude range
     * @param max Maximum of magnitude range
     * @return Scaled magnitude
     */
    public static double scaleMagnitude(
        double magnitude,
        double min, double max){
        
        if(magnitude > max){
            return 1.0;
        }
        if(magnitude < -max){
            return -1.0;
        }
        if(magnitude < min && magnitude > -min){
            return 0.0;
        }
        double scaledValue = (Math.abs(magnitude) - min)/(max - min);
        if(magnitude < 0){
            return -scaledValue;
        }
        return scaledValue;
    }
    
    /**
     * Gives {x, y} of given stick
     * @param stick Stick to find x and y
     * @return {x, y} of given stick
     */
    private double[] getXY(int stick){
        int axisX = -1;
        int axisY = -1;
        
        if(stick == LEFT_STICK){
            axisX = LEFT_STICK_X;
            axisY = LEFT_STICK_Y;
        }else if(stick == RIGHT_STICK){
            axisX = RIGHT_STICK_X;
            axisY = RIGHT_STICK_Y;
        }else if(stick == DPAD){
            axisX = DPAD_X;
            axisY = DPAD_Y;
        }
        return new double[]{getRawAxis(axisX), -getRawAxis(axisY)};
    } 
}