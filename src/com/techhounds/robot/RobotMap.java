package com.techhounds.robot;

import edu.wpi.first.wpilibj.Preferences;

/**
 * @author Tiger Huang
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    
    public static final int DRIVER_GAMEPAD = 1;
    static int HOME_MODULES = 6;
    
    static int ELEVATOR_UP = 4;
    static int ELEVATOR_DOWN = 2;
    
    public static final int elevatorMotor = 9;
    public static final int elevatorBottomSwitch = 7;
    public static final int elevatorTopSwitch = 8;
    
    public static final double elevatorUpSpeed = 1.0;
    public static final double elevatorDownSpeed = 1.0;

    //What units are these in?
    public static final double ROBOT_WIDTH = 20/15;
    public static final double ROBOT_LENGTH = 30/15;
    
    //Range of 0-360 degrees for turning to angle
    public static final double angleTolerance = 12;
    //Joystick deadband
    public static final double driveDeadband = .05;
    
    public static final double degreesPerPulse = 360./(2048.*3);
    
    // Drive motors are Jags
    // Turn motors are victors
    
    public static final int frontLeftModuleTurnEncoderA = 7;
    public static final int frontLeftModuleTurnEncoderB = 8;
    public static final int frontLeftModuleHomeSwitch = 4;
    public static final int frontLeftModuleDriveMotor = 6;
    public static final int frontLeftModuleTurnMotor = 2;
    public static final double frontLeftModuleDriveMotorScale = 1;
    public static final double frontLeftModuleTurnMotorScale = 1;
    public static final double frontLeftModuleTurnEncoderOffset = getConfig("FrontLeftOffset",0);
    
    public static final int frontRightModuleTurnEncoderA = 9;
    public static final int frontRightModuleTurnEncoderB = 10;
    public static final int frontRightModuleHomeSwitch = 3;
    public static final int frontRightModuleDriveMotor = 5;
    public static final int frontRightModuleTurnMotor = 1;
    public static final double frontRightModuleDriveMotorScale = -1;
    public static final double frontRightModuleTurnMotorScale = -1;
    public static final double frontRightModuleTurnEncoderOffset = getConfig("FrontRightOffset",0);
    
    public static final int backLeftModuleTurnEncoderA = 11;
    public static final int backLeftModuleTurnEncoderB = 12;
    public static final int backLeftModuleHomeSwitch = 6;
    public static final int backLeftModuleDriveMotor = 7;
    public static final int backLeftModuleTurnMotor = 4;
    public static final double backLeftModuleDriveMotorScale = 1;
    public static final double backLeftModuleTurnMotorScale = -1;
    public static final double backLeftModuleTurnEncoderOffset = getConfig("BackLeftOffset",0);
    
    public static final int backRightModuleTurnEncoderA = 13;
    public static final int backRightModuleTurnEncoderB = 14;
    public static final int backRightModuleHomeSwitch = 1;
    public static final int backRightModuleDriveMotor = 8;
    public static final int backRightModuleTurnMotor = 3;
    public static final double backRightModuleDriveMotorScale = -1;
    public static final double backRightModuleTurnMotorScale = -1;
    public static final double backRightModuleTurnEncoderOffset = getConfig("BackRightOffset",0);

    private static double getConfig(String key, double d) {
        return Preferences.getInstance().getDouble(key, d);
    }
    
}
