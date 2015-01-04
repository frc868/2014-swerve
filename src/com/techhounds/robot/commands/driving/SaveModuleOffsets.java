package com.techhounds.robot.commands.driving;

import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj.Preferences;

/**
 *
 * @author tiger
 */
public class SaveModuleOffsets extends CommandBase {
    
    public SaveModuleOffsets() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        DriveSubsystem.getInstance().saveOffsets();
        Preferences.getInstance().save();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
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
    }
}
