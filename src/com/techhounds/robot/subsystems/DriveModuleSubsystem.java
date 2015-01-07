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
    private double angleRaw;
    private double angleSlope;
    private double angleOldSlope;
    
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
        
        this.turnEncoder.setAverageBits(1024);
        
        this.direction = 0;
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
                this.turn(1.0);
            }
            else {
                this.turn(0.0);
            }
        }
        else {
            doneHoming = this.turnToPositionRaw(turnEncoderOffset);
        }
    }
    
    public void stopMotors(){
        turnMotor.stopMotor();
        driveMotor.stopMotor();
    }
    
    public void updateDashboard() {
        SmartDashboard.putNumber(descriptor + " Raw Turn Encoder Value",
                this.getTurnEncoderRaw());
        SmartDashboard.putNumber(descriptor + " Raw Module Angle",
                this.getRawModuleAngle());
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
        
        SmartDashboard.putNumber(descriptor + " Direction",
                direction);
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
     */
    private void turnToAngle(double target) {
        //Fix the range on target, tmake it 0-360
        if(target < 0) {
            target += 360;
        }
        
        //Make sure the modules do not turn more than 60 degrees between each cycle
        double newAngle = (this.getTurnEncoderRaw() - turnEncoderOffset) * 
                360 / RobotMap.turnGearRatio / 5;
        
        //Move it so it is within 30 degrees of each other
        while((angle - newAngle) > (360 / RobotMap.turnGearRatio / 2)) {
            newAngle += (360 / RobotMap.turnGearRatio);
        }
        
        //Find the direction
        int newDirection = 0;
        if(newAngle - angle > 0) {
            newDirection = 1;
        }
        if(angle - newAngle > 0) {
            newDirection = -1;
        }
        
        //Adding some hysteresis
        direction += newDirection;
        
        //Fixing range of direction
        if(direction > 2) {
            direction = 2;
        }
        if(direction < -2) {
            direction = -2;
        }
        
        if( (direction > 0 && newDirection == 1) ||
            (direction < 0 && newDirection == -1)) {
            angle = newAngle;
            if(angle > 360) {
                angle -= 360;
            }
        }
        
        /*
        if(!homeSwitch.get()) {
            while(angle > ((turnEncoderOffset / 5 * 360) + 60)) {
                angle -= 120;
            }
            while(angle < ((turnEncoderOffset / 5 * 360) - 60)) {
                angle += 120;
            }
        }
        if(angle > 360) {
            angle -= 360;
        }
        if(angle < 0) {
            angle += 360;
        }
        */
        
        //Both target and angle are now in the range of 0 - 360
        //Or at least they should be
        SmartDashboard.putNumber(descriptor + " Target Angle", target);
        SmartDashboard.putNumber(descriptor + " Current Angle", angle);
        
        if((angle - target) > 180) {
            angle -= 360;
        }
        if((target - angle) > 180) {
            angle += 360;
        }
        
        //Both should now be within 180 degrees of each other
        
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
        
        angleRaw = raw;
        
        return angleRaw;
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
