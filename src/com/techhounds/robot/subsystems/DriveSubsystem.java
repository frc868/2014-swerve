package com.techhounds.robot.subsystems;

import com.sun.squawk.util.MathUtils;
import com.techhounds.robot.OI;
import com.techhounds.robot.RobotMap;
import com.techhounds.robot.commands.driving.DriveWithGamepad;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Tiger Huang
 */
public class DriveSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    private static DriveSubsystem instance;
    
    private DriveModuleSubsystem frontLeftModule;
    private DriveModuleSubsystem frontRightModule;
    private DriveModuleSubsystem backLeftModule;
    private DriveModuleSubsystem backRightModule;
    
    private Gyro gyro;
    
    private double xAvg;
    private double yAvg;
    private double rAvg;
    
    private double aAvg;
    private double bAvg;
    private double cAvg;
    private double dAvg;
    
    private boolean isFieldCentric = false;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    private DriveSubsystem() {
        super("DriveSubsystem");
        
        frontLeftModule = new DriveModuleSubsystem(RobotMap.frontLeftModuleTurnEncoderA,
                                                   RobotMap.frontLeftModuleTurnEncoderB,
                                                   RobotMap.frontLeftModuleHomeSwitch, 
                                                   RobotMap.frontLeftModuleDriveMotor,
                                                   RobotMap.frontLeftModuleTurnMotor,
                                                   RobotMap.frontLeftModuleDriveMotorScale,
                                                   RobotMap.frontLeftModuleTurnMotorScale,
                                                   RobotMap.frontLeftModuleTurnEncoderOffset,
                                                   "FrontLeft");
        frontRightModule = new DriveModuleSubsystem(RobotMap.frontRightModuleTurnEncoderA,
                                                    RobotMap.frontRightModuleTurnEncoderB,
                                                    RobotMap.frontRightModuleHomeSwitch, 
                                                    RobotMap.frontRightModuleDriveMotor,
                                                    RobotMap.frontRightModuleTurnMotor,
                                                    RobotMap.frontRightModuleDriveMotorScale,
                                                    RobotMap.frontRightModuleTurnMotorScale,
                                                    RobotMap.frontRightModuleTurnEncoderOffset,
                                                   "FrontRight");
        backLeftModule = new DriveModuleSubsystem(RobotMap.backLeftModuleTurnEncoderA,
                                                  RobotMap.backLeftModuleTurnEncoderB,
                                                  RobotMap.backLeftModuleHomeSwitch, 
                                                  RobotMap.backLeftModuleDriveMotor,
                                                  RobotMap.backLeftModuleTurnMotor,
                                                  RobotMap.backLeftModuleDriveMotorScale,
                                                  RobotMap.backLeftModuleTurnMotorScale,
                                                  RobotMap.backLeftModuleTurnEncoderOffset,
                                                   "BackLeft");
        backRightModule = new DriveModuleSubsystem(RobotMap.backRightModuleTurnEncoderA,
                                                   RobotMap.backRightModuleTurnEncoderB,
                                                   RobotMap.backRightModuleHomeSwitch, 
                                                   RobotMap.backRightModuleDriveMotor,
                                                   RobotMap.backRightModuleTurnMotor,
                                                   RobotMap.backRightModuleDriveMotorScale,
                                                   RobotMap.backRightModuleTurnMotorScale,
                                                   RobotMap.backRightModuleTurnEncoderOffset,
                                                   "BackRight");
        
        gyro = new Gyro(RobotMap.gyroPort);
    }
    
    public static DriveSubsystem getInstance() {
        if(instance == null) {
            instance = new DriveSubsystem();
            instance.setDefaultCommand(new DriveWithGamepad());
        }
        return instance;
    }
    
    public void driveWithGamepad() {
        double x = OI.getInstance().getDriverGamePad().getLeftStickX();
        double y = OI.getInstance().getDriverGamePad().getLeftStickY();
        double r = OI.getInstance().getDriverGamePad().getRightStickX();
        
        //xAvg = xAvg * .75 + x * .25;
        //yAvg = yAvg * .75 + y * .25;
        //rAvg = rAvg * .75 + r * .25;
        
        if(isFieldCentric) {
            double angle = gyro.getAngle() * Math.PI / 180;
            xAvg = x * Math.cos(angle) - y * Math.sin(angle);
            yAvg = x * Math.sin(angle) + y * Math.cos(angle);
            rAvg = r;
        }
        else {
            xAvg = x;
            yAvg = y;
            rAvg = r;
        }
        
        SmartDashboard.putNumber("Gamepad Left Stick X", xAvg);
        SmartDashboard.putNumber("Gamepad Left Stick Y", yAvg);
        SmartDashboard.putNumber("Gamepad Right Stick X", rAvg);
        
        // Time for swerve logic :)
        // Using ether's reverse kinematics for swerve drive
        // Find it here http://www.chiefdelphi.com/media/papers/2426
        // I suggest you read it, its a fascinating derivation
        double a = xAvg - rAvg * RobotMap.ROBOT_LENGTH/2;
        double b = xAvg + rAvg * RobotMap.ROBOT_LENGTH/2;
        double c = yAvg - rAvg * RobotMap.ROBOT_WIDTH/2;
        double d = yAvg + rAvg * RobotMap.ROBOT_WIDTH/2;
        
        //aAvg = aAvg * .75 + a * .25;
        //bAvg = bAvg * .75 + b * .25;
        //cAvg = cAvg * .75 + c * .25;
        //dAvg = dAvg * .75 + d * .25;
        aAvg = a;
        bAvg = b;
        cAvg = c;
        dAvg = d;
        
        frontLeftModule.setDriveParams(bAvg, dAvg);
        frontRightModule.setDriveParams(bAvg, cAvg);
        backLeftModule.setDriveParams(aAvg, dAvg);
        backRightModule.setDriveParams(aAvg, cAvg);
        
        //Reduces everything by the same scale if anything exceeds 1
        double scale = Math.max(1.0,Math.max(
                        Math.max(frontLeftModule.getMagnitude(),
                                 frontRightModule.getMagnitude()),
                        Math.max(backLeftModule.getMagnitude(),
                                 backRightModule.getMagnitude())));
        
        frontLeftModule.drive(scale);
        frontRightModule.drive(scale);
        backLeftModule.drive(scale);
        backRightModule.drive(scale);
    }
    
    public boolean doneHomeModules() {
        return frontLeftModule.doneHomeModule() &&
               frontRightModule.doneHomeModule() &&
               backLeftModule.doneHomeModule() &&
               backRightModule.doneHomeModule();
    }
    
    public void homeModulesInit(){
        frontLeftModule.homeModuleInit();
        frontRightModule.homeModuleInit();
        backLeftModule.homeModuleInit();
        backRightModule.homeModuleInit();
        //System.out.println("Starting Home Modules");
    }
    
    public void homeModules(){
        frontLeftModule.homeModule();
        frontRightModule.homeModule();
        backLeftModule.homeModule();
        backRightModule.homeModule();
        //System.out.println("Homing Modules");
    }
    
    public void zeroGyro() {
        gyro.reset();
    }
    
    public void setFieldCentric() {
        isFieldCentric = true;
    }
    
    public void setRobotCentric() {
        isFieldCentric = false;
    }
    
    public void stopMotors(){
        frontLeftModule.stopMotors();
        frontRightModule.stopMotors();
        backLeftModule.stopMotors();
        backRightModule.stopMotors();
    }
    
    public void updateDashboard() {
        frontLeftModule.updateDashboard();
        frontRightModule.updateDashboard();
        backLeftModule.updateDashboard();
        backRightModule.updateDashboard();
        SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
        SmartDashboard.putBoolean("Field Centric", isFieldCentric);
    }
    
    public DriveModuleSubsystem getFrontLeftModule() {
        return frontLeftModule;
    }
    public DriveModuleSubsystem getFrontRightModule() {
        return frontRightModule;
    }
    public DriveModuleSubsystem getBackLeftModule() {
        return backLeftModule;
    }
    public DriveModuleSubsystem getBackRightModule() {
        return backRightModule;
    }
    
    public void saveOffsets() {
        frontLeftModule.saveOffset();
        frontRightModule.saveOffset();
        backLeftModule.saveOffset();
        backRightModule.saveOffset();
    }
}
