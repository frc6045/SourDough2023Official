// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands.AutoCommands.SwerveToNearest;

// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.DriverStation.Alliance;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import frc.robot.Constants.PoseConstants;
// import frc.robot.commands.AutoCommands.SwerveToMethods.SwerveToPoseWithTrajectory;
// import frc.robot.subsystems.Swerve.DriveSubsystem;

// // NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// // information, see:
// // https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
// public class SwerveToNearestPole extends InstantCommand {
//   private final DriveSubsystem m_robotDrive;
//   private double yDistance;
//   private double xDistance;
//   public SwerveToNearestPole(DriveSubsystem m_robotDrive) {
//     // Use addRequirements() here to declare subsystem dependencies.
//     this.m_robotDrive = m_robotDrive;

//     addRequirements(m_robotDrive);
//   }

//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() 
//   {
//     yDistance = m_robotDrive.getPose().getY();
//     xDistance = m_robotDrive.getPose().getX();
//     System.out.println("SwerveToNearestPole");
//     System.out.println("xDistance: " + xDistance);
//     System.out.println("yDistance: " + yDistance);

//     // if(DriverStation.getAlliance() == Alliance.Blue)
//     // {
//     //     if(yDistance > 4.325 && yDistance < 4.95)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.firstConeHighPosition1).schedule();
//     //       System.out.println("one");
//     //     }
//     //     else if(yDistance > 3.545 && yDistance < 4.325)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.secondConeHighPosition1).schedule();
//     //       System.out.println("two");
//     //     }
//     //     else if(yDistance > 2.7 && yDistance < 3.545)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.thirdConeHighPosition1).schedule();
//     //       System.out.println("two");
//     //     }
//     //     else if(yDistance > 1.865 && yDistance < 2.7)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.fourthConeHighPosition1).schedule();
//     //       System.out.println("two");
//     //     }
//     //     else if(yDistance > 1.06 && yDistance < 1.865)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.fifthConeHighPosition1).schedule();
//     //       System.out.println("two");
//     //     }
//     //     else if(yDistance < 1.06)
//     //     {
//     //       new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.sixthConeHighPosition1).schedule();
//     //       System.out.println("two");
//     //     }
//     //     else
//     //     {}
//     //  }

//     // else if(DriverStation.getAlliance() == Alliance.Red)
//     // {
//     //   if(yDistance > 4.325 && yDistance < 4.95)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.firstConeHighPosition1).schedule();
//     //     System.out.println("one");
//     //   }
//     //   else if(yDistance > 3.545 && yDistance < 4.325)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.secondConeHighPosition1).schedule();
//     //     System.out.println("two");
//     //   }
//     //   else if(yDistance > 2.7 && yDistance < 3.545)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.thirdConeHighPosition1).schedule();
//     //     System.out.println("two");
//     //   }
//     //   else if(yDistance > 1.865 && yDistance < 2.7)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.fourthConeHighPosition1).schedule();
//     //     System.out.println("two");
//     //   }
//     //   else if(yDistance > 1.06 && yDistance < 1.865)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.fifthConeHighPosition1).schedule();
//     //     System.out.println("two");
//     //   }
//     //   else if(yDistance < 1.06)
//     //   {
//     //     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.sixthConeHighPosition1).schedule();
//     //     System.out.println("two");
//     //   }
    
//     // }
    
//     //Red positions



//     new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.secondConeHighPosition1).schedule();



//     // if(yDistance > 3 && yDistance < 4.01)
//     // {
//     //   new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.thirdConeHighPosition1).schedule();
//     //   System.out.println("three");
//     // }
//     // if(yDistance > 4 && yDistance < 5)
//     // {
//     //   new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.fourthConeHighPosition1).schedule();
//     // }



    
//   }
// }
