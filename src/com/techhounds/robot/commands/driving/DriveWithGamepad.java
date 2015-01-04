package com.techhounds.robot.commands.driving;

import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveSubsystem;

/**
 *
 * @author Tiger Huang
 */
public class DriveWithGamepad extends CommandBase {
    
    private DriveSubsystem drive;
    
    public DriveWithGamepad() {
        super("DriveWithGamepad");
        drive = DriveSubsystem.getInstance();
        requires(drive);
        setInterruptible(true);
    }
    
    protected void initialize() {
    }

    protected void execute() {
        drive.driveWithGamepad();
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
        drive.stopMotors();
    }

    protected void interrupted() {
        end();
    }
}
