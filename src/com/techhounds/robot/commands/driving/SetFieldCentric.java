package com.techhounds.robot.commands.driving;

import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveSubsystem;

/**
 *
 * @author Tiger Huang
 */
public class SetFieldCentric extends CommandBase {
    
    private DriveSubsystem drive;
    
    public SetFieldCentric() {
        super("SetFieldCentric");
        drive = DriveSubsystem.getInstance();
        requires(drive);
        setInterruptible(true);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        drive.setFieldCentric();
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
