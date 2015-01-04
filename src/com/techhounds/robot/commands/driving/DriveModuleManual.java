package com.techhounds.robot.commands.driving;

import com.techhounds.robot.OI;
import com.techhounds.robot.commands.CommandBase;
import com.techhounds.robot.subsystems.DriveModuleSubsystem;
import com.techhounds.robot.subsystems.DriveSubsystem;

/**
 *
 * @author tiger
 */
public class DriveModuleManual extends CommandBase {
    private DriveModuleSubsystem module;
    
    public DriveModuleManual(DriveModuleSubsystem module) {
        requires(DriveSubsystem.getInstance());
        this.module = module;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double x = OI.getInstance().getUserX();
        double y = OI.getInstance().getUserY();
        module.setDriveParams(x, y);
        module.drive(1);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        module.setDriveParams(0, 0);
        module.drive(1);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
