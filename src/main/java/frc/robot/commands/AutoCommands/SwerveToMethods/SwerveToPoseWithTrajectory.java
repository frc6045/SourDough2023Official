// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands.AutoCommands.SwerveToMethods;



// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.controller.ProfiledPIDController;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.trajectory.Trajectory;
// import edu.wpi.first.math.trajectory.TrajectoryUtil;
// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.Filesystem;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import edu.wpi.first.wpilibj2.command.PrintCommand;
// import edu.wpi.first.wpilibj2.command.ProxyCommand;
// import edu.wpi.first.wpilibj2.command.RunCommand;
// import edu.wpi.first.wpilibj2.command.ScheduleCommand;
// import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
// import frc.robot.Constants.AutoConstants;
// import frc.robot.Constants.DriveConstants;
// import frc.robot.subsystems.Swerve.DriveSubsystem;

// import java.io.IOException;
// import java.nio.file.Path;

// import com.pathplanner.lib.PathConstraints;
// import com.pathplanner.lib.PathPlanner;
// import com.pathplanner.lib.PathPlannerTrajectory;
// import com.pathplanner.lib.PathPoint;
// import com.pathplanner.lib.auto.PIDConstants;
// import com.pathplanner.lib.auto.SwerveAutoBuilder;

// public class SwerveToPoseWithTrajectory extends CommandBase {

//   private final DriveSubsystem drive;
//   private final Pose2d targetPose;
//   private boolean isFinished = false;

//   public SwerveToPoseWithTrajectory(DriveSubsystem drive, Pose2d targetPose) {
//     this.drive = drive;
//     this.targetPose = targetPose;
    
//   }

//   @Override
//   public void initialize() {



//       PathPlannerTrajectory trajectory = PathPlanner.generatePath(
//         new PathConstraints(2, 1.5), 
//         new PathPoint(drive.getPose().getTranslation(), drive.getPose().getRotation(), drive.getPose().getRotation()),
//         new PathPoint(targetPose.getTranslation(), targetPose.getRotation(), targetPose.getRotation()));
   

//         SwerveAutoBuilder autoBuilder = new SwerveAutoBuilder(
//           drive::getPose, 
//           drive::resetOdometry,
//           DriveConstants.kDriveKinematics,
//           new PIDConstants(0.3, 0.0 ,0), //original p = 5, 1st attempt: p = 5, d = 0.5, 2nd attempt: p= 5, d = 0.5, 3rd attempt: p = 5, d = 3 this caused the wheels to shutter
//           new PIDConstants(0.6, 0.0, 0),
//           drive::setModuleStates,
//           AutoConstants.eventMap,
//           true,
//           drive);



//           //print statement to help diagnoase the issue, then ensures that the follow trajectory command finishes
//           autoBuilder.followPath(trajectory).andThen(new PrintCommand("Screw You")).andThen(new InstantCommand(()-> finish())).schedule();

          
//     // Reset odometry to the starting pose of the trajectory.
//     // drive.resetOdometry(trajectory.getInitialPose());
//   }

//   @Override
//   public void execute() 
//   {
//     System.out.println("Command Still running");
//   }

//   @Override
//   public void end(boolean interrupted) {
//     drive.drive(0, 0, 0, true);
//     System.out.println("it finished");
//   }

//   @Override
//   public boolean isFinished() {
//     System.out.println("trying to finishing");
//     return isFinished;


    
//   }

 
//   public void finish()
//   {
//     System.out.println("finishing");
//      isFinished = true;
      
//   }



// }
