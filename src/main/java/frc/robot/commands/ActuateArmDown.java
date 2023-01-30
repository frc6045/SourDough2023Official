// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArmCostants;
import frc.robot.subsystems.Arm;

public class ActuateArmDown extends CommandBase {
  private final Arm m_Arm;
  /** Creates a new ActuateArm. */
  public ActuateArmDown(Arm m_Arm) 
  {
    this.m_Arm = m_Arm;
    addRequirements(m_Arm);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    m_Arm.setSpeed(-0.1);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    m_Arm.stopMotor();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(m_Arm.getOutputCurrent() > ArmCostants.maxAmperageOnArm)
    {
    return true;
    }

    return false;
    
  }
}
