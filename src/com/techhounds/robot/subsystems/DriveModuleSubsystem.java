package com.techhounds.robot.subsystems;

import com.sun.squawk.util.MathUtils;
import com.techhounds.robot.RobotMap;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
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
    private Encoder turnEncoder;
    private DigitalInput homeSwitch;
    
    private double x;
    private double y;
    private double r;
    private double a;
    
    private boolean switchPressed;
    private boolean doneHoming;
    
    private double driveMotorScale;
    private double turnMotorScale;
    private double turnEncoderOffset;
    private String descriptor;
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public DriveModuleSubsystem(int turnEncoderA, int turnEncoderB, int homeSwitch,
                                int driveMotor, int turnMotor,
                                double driveMotorScale, double turnMotorScale,
                                double turnEncoderOffset, String descriptor) {
        super("DriveModuleSubsystem " + descriptor);
        this.turnEncoder = new Encoder(turnEncoderA, turnEncoderB);
        this.homeSwitch = new DigitalInput(homeSwitch);
        this.driveMotor = new Jaguar(driveMotor);
        this.turnMotor = new Victor(turnMotor);
        
        this.driveMotorScale = driveMotorScale;
        this.turnMotorScale = turnMotorScale;
        this.turnEncoderOffset = turnEncoderOffset;
        this.descriptor = descriptor;
        
        this.turnEncoder.setDistancePerPulse(RobotMap.degreesPerPulse);
        this.turnEncoder.start();
    }
    
    public boolean doneHomeModule() {
        return doneHoming;
    }
    
    public void homeModuleInit() {
        switchPressed = false;
        doneHoming = false;
    }
    
    public void homeModule() {
        if(!switchPressed) {
            switchPressed = !homeSwitch.get();
            if(!switchPressed) {
                this.turn(0.5);
            }
            else {
                this.turn(0.0);
            }
        }
        else {
            doneHoming = this.turnToAngle(turnEncoderOffset);
        }
    }
    
    public void stopMotors(){
        turnMotor.stopMotor();
        driveMotor.stopMotor();
    }
    
    public void updateDashboard() {
        SmartDashboard.putNumber(descriptor + " Raw Turn Encoder Count",
                turnEncoder.getRaw());
        SmartDashboard.putNumber(descriptor + " Module Angle",
                this.getCurrentAngle());
        SmartDashboard.putBoolean(descriptor + " Switch State",
                !homeSwitch.get());
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
     * May modify r if it sees that would reduce the angle turning required
     * @param target 
     * @return If the module is within the turn tolerance
     */
    private boolean turnToAngle(double target) {
        double angle = getCurrentAngle();
        while(angle - target > 180) {
            angle -= 180;
        }
        while(target - angle > 180) {
            angle += 180;
        }
        
        //Both target and angle should be within 180 degrees of each other
        SmartDashboard.putNumber(descriptor + " Target Angle", target);
        SmartDashboard.putNumber(descriptor + " Current Angle", angle);
        
        if(Math.abs(target - angle) <= RobotMap.angleTolerance) {
            this.turn(0.0);
            return true;
        }
        else {
            if(target > angle) {
                this.turn(1.0);
            }
            else {
                this.turn(-1.0);
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
     * Gets the current rotation angle
     */
    private double getCurrentAngle() {
        double angle = turnEncoder.getDistance();
        return angle;
    }

    void saveOffset() {
        Preferences.getInstance().putDouble(descriptor + "Offset", getCurrentAngle());
    }
}
