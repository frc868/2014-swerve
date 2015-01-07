/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.techhounds.robot;


import com.techhounds.robot.commands.AutonCommands;
import com.techhounds.robot.commands.TeleopCommands;
import com.techhounds.robot.subsystems.DriveSubsystem;
import com.techhounds.robot.subsystems.ElevatorSubsystem;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 * @author Tiger Huang
 */
public class RobotSwerve extends IterativeRobot {

    private AutonCommands auton;
    private TeleopCommands teleop;
   
    private Timer timer;
    
    int count = 0;
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        teleop = new TeleopCommands();
        auton = new AutonCommands();
        OI.getInstance().initialize();
        timer = new Timer();
        timer.reset();
        timer.start();
        System.out.println("*************\n" +
                           "WE ROBOT NOW!\n" +
                           "*************");
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
        teleop.cancel();
        auton.start();
        System.out.println("*************\n" +
                           "WE AUTON NOW!\n" +
                           "*************");
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        updateSmartDashboard();
    }

    public void teleopInit() {
	// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        auton.cancel();
        teleop.start();
        System.out.println("**************\n" +
                           "WE TELEOP NOW!\n" +
                           "**************");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        updateSmartDashboard();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    public void disabledPeriodic() {
        updateSmartDashboard();
    }
    
    public void updateSmartDashboard() {
     /*   if(timer.get() > .3){
            timer.reset();
            DriveSubsystem.getInstance().updateDashboard();
            SmartDashboard.putNumber("Count", count++);
        }*/
            DriveSubsystem.getInstance().updateDashboard();
            ElevatorSubsystem.getInstance().updateDashboard();
            SmartDashboard.putNumber("Count", count++);
    }
}
