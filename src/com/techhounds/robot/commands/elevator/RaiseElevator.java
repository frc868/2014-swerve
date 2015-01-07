/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techhounds.robot.commands.elevator;

import com.techhounds.robot.RobotMap;
import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.ElevatorSubsystem;

/**
 *
 * @author Tiger
 */
public class RaiseElevator extends CommandBase {
    
    public RaiseElevator() {
        requires(ElevatorSubsystem.getInstance());
        setInterruptible(true);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        ElevatorSubsystem.getInstance().moveUp(RobotMap.elevatorUpSpeed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        ElevatorSubsystem.getInstance().stopMotors();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
