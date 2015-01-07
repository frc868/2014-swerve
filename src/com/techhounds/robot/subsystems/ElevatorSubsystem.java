/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techhounds.robot.subsystems;

import com.techhounds.robot.RobotMap;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Tiger
 */
public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    private static ElevatorSubsystem instance;
    
    private Victor elevatorMotor;
    private DigitalInput bottomSwitch;
    private DigitalInput topSwitch;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    private ElevatorSubsystem() {
        super("Elevator Subsystem");
        
        this.elevatorMotor = new Victor(RobotMap.elevatorMotor);
        this.bottomSwitch = new DigitalInput(RobotMap.elevatorBottomSwitch);
        this.topSwitch = new DigitalInput(RobotMap.elevatorTopSwitch);
    }
    
    public static ElevatorSubsystem getInstance() {
        if(instance == null) {
            instance = new ElevatorSubsystem();
            //instance.setDefaultCommand(new DriveWithGamepad());
        }
        return instance;
    }
    
    public void move(double speed) {
        if((speed > 0 && topSwitch.get()) || (speed < 0 && bottomSwitch.get())) {
            elevatorMotor.set(-speed);
        }
        else {
            elevatorMotor.set(0.0);
        }
    }
    
    public void moveUp(double speed) {
        move(speed);
    }
    
    public void moveDown(double speed) {
        move(-speed);
    }
    
    public void stopMotors() {
        move(0.0);
    }
    
    public void updateDashboard() {
        SmartDashboard.putBoolean("Elevator Top Switch",
                !topSwitch.get());
        SmartDashboard.putBoolean("Elevator Bottom Switch",
                !bottomSwitch.get());
    }
}
