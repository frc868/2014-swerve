
package com.techhounds.robot;

import com.techhounds.robot.commands.driving.DriveModuleManual;
import com.techhounds.robot.commands.driving.DriveWheelManual;
import com.techhounds.robot.commands.driving.SaveModuleOffsets;
import com.techhounds.robot.commands.driving.SpinWheelManual;
import com.techhounds.robot.commands.driving.HomeModules;
import com.techhounds.robot.commands.elevator.LowerElevator;
import com.techhounds.robot.commands.elevator.RaiseElevator;
import com.techhounds.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 * Most of the stuff here is self explanatory
 * @author Tiger Huang
 */
public class OI {
    private static OI instance;
    
    private boolean initialized;
    
    private Gamepad driverGamepad;
    
    private OI() {
        driverGamepad = new Gamepad(RobotMap.DRIVER_GAMEPAD);
        initialized = false;
        DriveSubsystem drive = DriveSubsystem.getInstance();
        SmartDashboard.putData("Tweak Left Front", new SpinWheelManual(drive.getFrontLeftModule()));
        SmartDashboard.putData("Tweak Right Front", new SpinWheelManual(drive.getFrontRightModule()));
        SmartDashboard.putData("Tweak Left Back", new SpinWheelManual(drive.getBackLeftModule()));
        SmartDashboard.putData("Tweak Right Back", new SpinWheelManual(drive.getBackRightModule()));
        SmartDashboard.putData("Drive Left Front", new DriveWheelManual(drive.getFrontLeftModule()));
        SmartDashboard.putData("Drive Right Front", new DriveWheelManual(drive.getFrontRightModule()));
        SmartDashboard.putData("Drive Left Back", new DriveWheelManual(drive.getBackLeftModule()));
        SmartDashboard.putData("Drive Right Back", new DriveWheelManual(drive.getBackRightModule()));
        SmartDashboard.putData("Drive 2d Left Front", new DriveModuleManual(drive.getFrontLeftModule()));
        SmartDashboard.putData("Drive 2d Right Front", new DriveModuleManual(drive.getFrontRightModule()));
        SmartDashboard.putData("Drive 2d Left Back", new DriveModuleManual(drive.getBackLeftModule()));
        SmartDashboard.putData("Drive 2d Right Back", new DriveModuleManual(drive.getBackRightModule()));
        SmartDashboard.putData("Home Modules", new HomeModules());
        SmartDashboard.putData("Save Offsets", new SaveModuleOffsets());
        SmartDashboard.putData("Elevator Up", new RaiseElevator());
        SmartDashboard.putData("Elevator Down", new LowerElevator());
        
        Button homeModules = new JoystickButton(driverGamepad, RobotMap.HOME_MODULES);
        homeModules.whenPressed(new HomeModules());
        
        Button elevatorUp = new JoystickButton(driverGamepad, RobotMap.ELEVATOR_UP);
        elevatorUp.whileActive(new RaiseElevator());
        Button elevatorDown = new JoystickButton(driverGamepad, RobotMap.ELEVATOR_DOWN);
        elevatorDown.whileActive(new LowerElevator());
    }
    
    public static OI getInstance() {
        if(instance == null) {
            instance = new OI();
        }
        return instance;
    }
    
    public void initialize() {
        if(initialized) {
            return;
        }
        
        //No buttons to initialize
        
        initialized = true;
    }
    
    public Gamepad getDriverGamePad() {
        return driverGamepad;
    }
    
    public double getUserSpinPower() {
        return driverGamepad.getLeftStickX();
    }
    public double getUserDrivePower() {
        return driverGamepad.getLeftStickY();
    }

    public double getUserX() {
        return driverGamepad.getLeftStickX();
    }
    public double getUserY() {
        return driverGamepad.getLeftStickY();
    }
}

