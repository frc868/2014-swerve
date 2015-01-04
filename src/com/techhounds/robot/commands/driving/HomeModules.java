package com.techhounds.robot.commands.driving;

import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Tiger Huang
 */
public class HomeModules extends CommandBase {
    
    private DriveSubsystem drive;
    
    public HomeModules() {
        super("HomeModules");
        drive = DriveSubsystem.getInstance();
        requires(drive);
        setInterruptible(true);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        SmartDashboard.putBoolean("Homing", true);
        drive.homeModulesInit();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        drive.homeModules();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return drive.doneHomeModules();
    }

    // Called once after isFinished returns true
    protected void end() {
        SmartDashboard.putBoolean("Homing", false);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
