// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands.AutoCommands.SwerveToAndAuto;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import edu.wpi.first.wpilibj2.command.PrintCommand;
// import frc.robot.Constants.AutoConstants;
// import frc.robot.Constants.PoseConstants;
// import frc.robot.Constants.PositionConstants;
// import frc.robot.commands.ArmAndWrist.SetArmWithWristPosition;
// import frc.robot.commands.AutoCommands.WristConsumeWithTime;
// import frc.robot.commands.AutoCommands.SwerveToMethods.SwerveToPoseWithTrajectory;
// import frc.robot.subsystems.Arm.ArmSubsystem;
// import frc.robot.subsystems.Swerve.DriveSubsystem;
// import frc.robot.subsystems.Wrist.WristIntake;
// import frc.robot.subsystems.Wrist.WristSubsystem;

// // NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// // information, see:
// // https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
// public class SwerveWithHighCone extends CommandBase {
//   private final DriveSubsystem m_robotDrive;
//   private final WristSubsystem m_WristSubsytem;
//   private final WristIntake m_WristIntake;
//   private final ArmSubsystem m_ArmSubsystem;

//   private double yDistance;
//   private double xDistance;
//   private Command RunCommand;
//   public SwerveWithHighCone(DriveSubsystem m_robotDrive, WristSubsystem m_WristSubsystem, WristIntake m_WristIntake, ArmSubsystem m_ArmSubsystem) {
//     // Use addRequirements() here to declare subsystem dependencies.
//     this.m_robotDrive = m_robotDrive;
//     this.m_ArmSubsystem = m_ArmSubsystem;
//     this.m_WristSubsytem = m_WristSubsystem;
//     this.m_WristIntake = m_WristIntake;
//     yDistance = m_robotDrive.getPose().getY();
//     xDistance = m_robotDrive.getPose().getX();
    
//     //addRequirements(m_robotDrive, m_WristSubsystem, m_WristIntake, m_ArmSubsystem);
    
//   }

//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() 
//   {
//     if(yDistance > 4.35 && yDistance < 4.95)
//     {
//       System.out.println("notYellow");
//       RunCommand = new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.firstCubePosition1);
//       // .andThen(
//       //   new SetArmWithWristPosition(m_WristSubsytem, PositionConstants.ScoreHighWristPosition, m_ArmSubsystem, PositionConstants.ScoreHighArmPosition)).andThen
//       //   (new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.firstCubePosition2)).andThen(
//       //     new WristConsumeWithTime(m_WristIntake, 2));
//     }
//     else 
//     {
//       RunCommand = new PrintCommand("not in bounds");
//     }
    
//     // if(yDistance > 3.5 && yDistance < 4.37)
//     // {
//     //  RunCommand = new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.secondCubePosition1).andThen(
//     //     new SetArmWithWristPosition(m_WristSubsytem, PositionConstants.ScoreHighWristPosition, m_ArmSubsystem, PositionConstants.ScoreHighArmPosition)).andThen
//     //     (new SwerveToPoseWithTrajectory(m_robotDrive, PoseConstants.secondCubePosition2)).andThen(
//     //       new WristConsumeWithTime(m_WristIntake, 2));    
//     // }
                    
//      RunCommand.schedule();
//     System.out.println("maybe scheduled");
//   }
// }
