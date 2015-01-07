package com.techhounds.robot.commands;

import com.techhounds.robot.subsystems.DriveSubsystem;
import com.techhounds.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 *
 * @author David Murzyn
 */
public class UpdateDashboard extends CommandBase {
    
    private Timer timer;
    
    public UpdateDashboard() {
        super("UpdateDashboard");
        setInterruptible(true);
        timer = new Timer();
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        timer.reset();
        timer.start();
        SmartDashboard.putBoolean("Smart Dashboard Updating", true);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        
        if(timer.get() < .3){
            timer.reset();
            DriveSubsystem.getInstance().updateDashboard();
            ElevatorSubsystem.getInstance().updateDashboard();
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        SmartDashboard.putBoolean("Smart Dashboard Updating", false);
        System.out.println("Ended");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        SmartDashboard.putBoolean("Smart Dashboard Updating", false);
        System.out.println("Interrupted");
    }
}
