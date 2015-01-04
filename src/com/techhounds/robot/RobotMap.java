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

    
    public static final double ROBOT_WIDTH = 20/15;
    public static final double ROBOT_LENGTH = 30/15;
    
    public static final double turnTolerance = .50;
    public static final double angleTolerance = 12;
    public static final double driveDeadband = .05;
    
    public static final double turnGearRatio = 3;
    
    // Drive motors are Jags
    // Turn motors are victors
    
    public static final int frontLeftModuleTurnEncoder = 4;
    public static final int frontLeftModuleHomeSwitch = 4;
    public static final int frontLeftModuleDriveMotor = 4;
    public static final int frontLeftModuleTurnMotor = 8;
    public static final double frontLeftModuleDriveMotorScale = 1;
    public static final double frontLeftModuleTurnMotorScale = 1;
    public static final double frontLeftModuleTurnEncoderOffset = getConfig("FrontLeftOffset",3.802);
    
    public static final int frontRightModuleTurnEncoder = 3;
    public static final int frontRightModuleHomeSwitch = 3;
    public static final int frontRightModuleDriveMotor = 3;
    public static final int frontRightModuleTurnMotor = 7;
    public static final double frontRightModuleDriveMotorScale = -1;
    public static final double frontRightModuleTurnMotorScale = -1;
    public static final double frontRightModuleTurnEncoderOffset = getConfig("FrontRightOffset",2.315);
    
    public static final int backLeftModuleTurnEncoder = 2;
    public static final int backLeftModuleHomeSwitch = 2;
    public static final int backLeftModuleDriveMotor = 2;
    public static final int backLeftModuleTurnMotor = 6;
    public static final double backLeftModuleDriveMotorScale = 1;
    public static final double backLeftModuleTurnMotorScale = -1;
    public static final double backLeftModuleTurnEncoderOffset = getConfig("BackLeftOffset",3.386);
    
    public static final int backRightModuleTurnEncoder = 1;
    public static final int backRightModuleHomeSwitch = 1;
    public static final int backRightModuleDriveMotor = 1;
    public static final int backRightModuleTurnMotor = 5;
    public static final double backRightModuleDriveMotorScale = -1;
    public static final double backRightModuleTurnMotorScale = -1;
    public static final double backRightModuleTurnEncoderOffset = getConfig("BackRightOffset",4.231);

    private static double getConfig(String key, double d) {
        //return Preferences.getInstance().getDouble(key, d);
        return d;
    }
    
}
