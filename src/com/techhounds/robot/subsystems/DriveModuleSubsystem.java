package com.techhounds.robot.subsystems;

import com.sun.squawk.util.MathUtils;
import com.techhounds.robot.RobotMap;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Tiger Huang
 */
public class DriveModuleSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    private Jaguar driveMotor;
    private Victor turnMotor;
    
    // Count increases counter-clockwise
    private AnalogChannel turnEncoder;
    private DigitalInput homeSwitch;

    /**
     * The angle of the module
     * Measured in degrees
     */
    private double angle;
    
    private double x;
    private double y;
    private double r;
    private double a;
    
    private boolean switchPressed;
    
    private double driveMotorScale;
    private double turnMotorScale;
    private double turnEncoderOffset;
    private String descriptor;
    
    private int direction;
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public DriveModuleSubsystem(int turnEncoder, int homeSwitch,
                                int driveMotor, int turnMotor,
                                double driveMotorScale, double turnMotorScale,
                                double turnEncoderOffset, String descriptor) {
        super("DriveModuleSubsystem " + descriptor);
        this.turnEncoder = new AnalogChannel(turnEncoder);
        this.homeSwitch = new DigitalInput(homeSwitch);
        this.driveMotor = new Jaguar(driveMotor);
        this.turnMotor = new Victor(turnMotor);
        
        this.driveMotorScale = driveMotorScale;
        this.turnMotorScale = turnMotorScale;
        this.turnEncoderOffset = turnEncoderOffset;
        this.descriptor = descriptor;
        
        //this.turnEncoder.setAverageBits(1024);
        
        this.direction = 0;
    }
    
    /**
     * Checks if the module is done homing. Turns off turn motor if it is done
     * @return True if the module is done homing, false otherwise.
     */
    public boolean doneHomeModule() {
        if(switchPressed && 
           Math.abs(turnEncoderOffset - this.getTurnEncoderRaw()) <= RobotMap.turnTolerance) {
            angle = 0.0;
            turnMotor.set(0.0);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Marks this module as not homed.
     */
    public void homeModuleInit() {
        switchPressed = false;
    }
    
    /**
     * Runs the turn motor to home the module.
     */
    public void homeModule() {
        if(!switchPressed) {
            //Keep rotating until switch gets pressed once
            switchPressed = !homeSwitch.get();
            if(!switchPressed) {
                this.turn(1.0);
            }
            else {
                this.turn(0.0);
            }
        }
        else {
            //Move to encoder offset after switch gets pressed once
            this.turnToPositionRaw(turnEncoderOffset);
        }
    }
    
    /**
     * Stops all the motors
     */
    public void stopMotors(){
        turnMotor.stopMotor();
        driveMotor.stopMotor();
    }
    
    /**
     * Dumps a lot of data onto the smart dashboard.
     */
    public void updateDashboard() {
        SmartDashboard.putNumber(descriptor + " Raw Turn Encoder Value",
                this.getTurnEncoderRaw());
        SmartDashboard.putNumber(descriptor + " Raw Module Angle",
                this.getRawModuleAngle());
        SmartDashboard.putBoolean(descriptor + " Switch State",
                switchPressed);
        SmartDashboard.putNumber(descriptor + " Input X",
                x);
        SmartDashboard.putNumber(descriptor + " Input Y",
                y);
        SmartDashboard.putNumber(descriptor + " Input magnitude",
                r);
        SmartDashboard.putNumber(descriptor + " Input angle",
                a);
    }
    
    /**
     * Drives this module with the specified vector
     * This coordinate plane is relative to the robot
     * @param x The x component of the vector
     * @param y The y component of the vector
     */
    public void setDriveParams(double x, double y) {
        r = Math.sqrt(x * x + y * y);
        if(r >= RobotMap.driveDeadband) {
            a = MathUtils.atan2(y, -x) * 180 / Math.PI - 90;
            if(a < 0) {
                a += 360;
            }
            //a should now be in range of 0-360
        }
    }
    
    /**
     * Gets the magnitude of the requested driving vector for this module
     * @return The magnitude of the driving vector
     */
    public double getMagnitude() {
        return r;
    }
    
    /**
     * Drives the module with the selected parameters
     * @param scale The scale factor for the speed
     */
    public void drive(double scale) {
        this.turnToAngle(a);
        this.move(r / scale);
    }
    
    /**
     * Turns the module to a specific angle
     * Make sure target is within the range 0-360
     * May modify r if it sees that would reduce the angle turning required
     * @param target 
     */
    private void turnToAngle(double target) {
        
        //Make sure the modules do not turn more than 60 degrees between each cycle
        //Converts raw 0-5V to 0-60 degree reading
        double newAngle = (this.getTurnEncoderRaw() - turnEncoderOffset) * 
                360 / RobotMap.turnGearRatio / 5;
        //^This value cannot be trusted that much
        //Extra filtering must be done to get a final value
        
        //Moves the new angle within 30 degrees of the old angle
        while((angle - newAngle) > (360 / RobotMap.turnGearRatio / 2)) {
            newAngle += (360 / RobotMap.turnGearRatio);
        }
        
        //Finds the direction of rotation
        int newDirection = 0;
        if(newAngle - angle > RobotMap.angleTolerance) {
            newDirection = 1;
        }
        if(angle - newAngle > RobotMap.angleTolerance) {
            newDirection = -1;
        }
        
        //Add a bit of hysteresis to the system
        direction += newDirection;
        
        //Renormalize the direction
        if(direction > 1) {
            direction = 1;
        }
        if(direction < -1) {
            direction = -1;
        }
        
        //If it gets swung completely to the other side, change
        //Else, direction just remains zero and is easier to swing on the next cycle
        if(direction == newDirection || newDirection == 0) {
            angle = newAngle;
            if(angle > 360) {
                angle -= 360;
            }
            //Angle should now be in the range of 0-360
            
            //Set direction in the case that newDirection is zero
            direction = newDirection;
        }
        
        //Both target and angle are now in the range of 0 - 360
        //Or at least they should be
        SmartDashboard.putNumber(descriptor + " Target Angle", target);
        SmartDashboard.putNumber(descriptor + " Current Angle", angle);
        
        //Move angle and target so that they are within 180 of each other
        if((angle - target) > 180) {
            target += 360;
        }
        if((target - angle) > 180) {
            target -= 360;
        }
        
        //Turning logic code.
        if(Math.abs(target - angle) <= RobotMap.angleTolerance) {
            this.turn(0.0);
        }
        else {
            if(target > angle) {
                this.turn(1.0);
            }
            else {
                this.turn(-1.0);
            }
        }
    }
    
    /**
     * Turns the module to a specific position
     * @param target The target for the module. Must be from 0.0 to 5.0
     * @return If the module is within the tolerance range of the target
     */
    private boolean turnToPositionRaw(double target) {
        if((angle - this.getTurnEncoderRaw()) > 2.5) {
            angle -= 5;
        }
        if((target - this.getTurnEncoderRaw()) > 2.5) {
            angle += 5;
        }
        if(Math.abs(target - this.getTurnEncoderRaw()) <= RobotMap.turnTolerance) {
            this.turn(0.0);
            angle = 0.0;
            return true;
        }
        else {
            if(target > this.getTurnEncoderRaw()) {
                this.turn(0.1);
            }
            else {
                this.turn(-0.1);
            }
            return false;
        }   
    }
    
    /**
     * Turns the module
     * Positive is clockwise, negative is counter-clockwise
     * @param speed The speed at which to turn the module. Must be between -1.0 and 1.0
     */
    public void turn(double speed) {
        turnMotor.set(speed * turnMotorScale);
    }
    
    /**
     * Moves the module
     * Positive is forward, negative is reverse
     * Call this only after calling setDriveParams first
     * @param speed The speed at which to run the module. Must be between -1.0 and 1.0
     */
    public void move(double speed) {
        driveMotor.set(speed * driveMotorScale);
    }
    
    /**
     * Gets the raw encoder value
     * Increasing value is clockwise module motion when viewed from the top
     * @return The turn encoder value
     */
    private double getTurnEncoderRaw() {
        double raw = 5.0 - turnEncoder.getAverageVoltage();
        
        return raw;
    }
    
    /**
     * Gets the raw angle of the module
     * Warning, this angle is relative to a arbritrary angle
     * @return The raw angle of the module
     */
    private double getRawModuleAngle() {
        return angle;
    } 

    void saveOffset() {
        Preferences.getInstance().putDouble(descriptor + "Offset", getTurnEncoderRaw());
    }
}
