package com.techhounds.robot.commands.driving;

import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveSubsystem;

/**
 *
 * @author Tiger Huang
 */
public class SetRobotCentric extends CommandBase {
    
    private DriveSubsystem drive;
    
    public SetRobotCentric() {
        super("SetRobotCentric");
        drive = DriveSubsystem.getInstance();
        requires(drive);
        setInterruptible(true);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        drive.setRobotCentric();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
