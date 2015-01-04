package com.techhounds.robot.commands.driving;

import com.techhounds.robot.OI;
import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveModuleSubsystem;
import com.techhounds.robot.subsystems.DriveSubsystem;

/**
 *
 * @author Tiger Huang
 */
public class DriveWheelManual extends CommandBase {
    private DriveModuleSubsystem module;
    
    public DriveWheelManual(DriveModuleSubsystem module) {
        requires(DriveSubsystem.getInstance());
        this.module = module;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double power = OI.getInstance().getUserDrivePower();
        module.move(power);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        module.move(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
