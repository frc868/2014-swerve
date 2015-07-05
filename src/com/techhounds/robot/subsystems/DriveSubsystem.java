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
    
    private double xAvg = 0;
    private double yAvg = 0;
    private double rAvg = 0;
    
    private double decayFactor = 1;
    
    private boolean isFieldCentric = false;
    private boolean strafeEnabled = true;
    
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
        
        SmartDashboard.putNumber("Decay Factor", decayFactor);
        SmartDashboard.putBoolean("Drive Reversible", false);
        SmartDashboard.putBoolean("Strafe Enabled", strafeEnabled);
        
        SmartDashboard.putNumber("Kp", RobotMap.Kp);
        SmartDashboard.putNumber("Ki", RobotMap.Ki);
        SmartDashboard.putNumber("Kd", RobotMap.Kd);
    }
    
    public static DriveSubsystem getInstance() {
        if(instance == null) {
            instance = new DriveSubsystem();
            instance.setDefaultCommand(new DriveWithGamepad());
        }
        return instance;
    }
    
    public void driveWithGamepad() {
        double xin = OI.getInstance().getDriverGamePad().getLeftStickX();
        double yin = OI.getInstance().getDriverGamePad().getLeftStickY();
        double rin = OI.getInstance().getDriverGamePad().getRightStickX();
        
        xAvg = xAvg * (1 - decayFactor) + xin * decayFactor;
        yAvg = yAvg * (1 - decayFactor) + yin * decayFactor;
        rAvg = rAvg * (1 - decayFactor) + rin * decayFactor;
        
        double x = 0;
        double y = 0;
        double r = 0;
        
        if(isFieldCentric) {
            double angle = gyro.getAngle() * Math.PI / 180;
            x = xAvg * Math.cos(angle) - yAvg * Math.sin(angle);
            y = xAvg * Math.sin(angle) + yAvg * Math.cos(angle);
            r = rAvg;
        }
        else {
            x = xAvg;
            y = yAvg;
            r = rAvg;
        }
        
        //x = x * x * x;
        //y = y * y * y;
        //r = r * r * r;
        
        if(!strafeEnabled) {
            x = 0;
        }
        
        SmartDashboard.putNumber("Gamepad Left Stick X", x);
        SmartDashboard.putNumber("Gamepad Left Stick Y", y);
        SmartDashboard.putNumber("Gamepad Right Stick X", r);
        
        // Time for swerve logic :)
        // Using ether's reverse kinematics for swerve drive
        // Find it here http://www.chiefdelphi.com/media/papers/2426
        // I suggest you read it, its a fascinating derivation
        double a = x - r * RobotMap.ROBOT_LENGTH/2;
        double b = x + r * RobotMap.ROBOT_LENGTH/2;
        double c = y - r * RobotMap.ROBOT_WIDTH/2;
        double d = y + r * RobotMap.ROBOT_WIDTH/2;
        
        frontLeftModule.setDriveParams(b, d);
        frontRightModule.setDriveParams(b, c);
        backLeftModule.setDriveParams(a, d);
        backRightModule.setDriveParams(a, c);
        
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
        decayFactor = SmartDashboard.getNumber("Decay Factor");
        boolean reversible = SmartDashboard.getBoolean("Drive Reversible");
        frontLeftModule.setDriveReversible(reversible);
        frontRightModule.setDriveReversible(reversible);
        backLeftModule.setDriveReversible(reversible);
        backRightModule.setDriveReversible(reversible);
        double kp = SmartDashboard.getNumber("Kp");
        double ki = SmartDashboard.getNumber("Ki");
        double kd = SmartDashboard.getNumber("Kd");
        frontLeftModule.setPidConstants(kp, ki, kd);
        frontRightModule.setPidConstants(kp, ki, kd);
        backLeftModule.setPidConstants(kp, ki, kd);
        backRightModule.setPidConstants(kp, ki, kd);
        strafeEnabled = SmartDashboard.getBoolean("Strafe Enabled");
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
