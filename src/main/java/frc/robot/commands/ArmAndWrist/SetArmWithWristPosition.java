// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.ArmAndWrist;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.ArmAndWrist.ArmCommands.ClosedLoopArm.PIDArmCommand;
import frc.robot.commands.ArmAndWrist.WristCommands.ClosedLoopWrist.PIDWristCommand;
import frc.robot.subsystems.Arm.ArmSubsystem;
import frc.robot.subsystems.Wrist.WristSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SetArmWithWristPosition extends ParallelCommandGroup {
  /** Creates a new SetArmWithWristPosition. */
  public SetArmWithWristPosition(WristSubsystem m_WristSubsystem, double wristSetPoint, ArmSubsystem m_ArmSubsystem, double ArmSetPoint) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new PIDWristCommand(m_WristSubsystem, wristSetPoint), 
      new PIDArmCommand(m_ArmSubsystem, ArmSetPoint)

    );
  }
}
