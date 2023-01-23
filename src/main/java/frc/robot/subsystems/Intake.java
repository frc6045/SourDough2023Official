package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxLimitSwitch;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

/*spark max motor controler  
dont to create motor objects
encoders
*/
public class Intake extends SubsystemBase{
 
  public final CANSparkMax leftIntakeMotor;
  public final CANSparkMax rightIntakeMotor;
  public final MotorControllerGroup intakeMotorGroup;
s
 RelativeEncoder leftIntakeEncoder;
 RelativeEncoder rightIntakeEncoder;
private int position;

public Intake()
{
    leftIntakeMotor = new CANSparkMax(ArmConstants.leftIntakeMotorCANID, MotorType.kBrushless);
    rightIntakeMotor = new CANSparkMax(ArmConstants.rightIntakeMotorCANID, MotorType.kBrushless);
   leftIntakeMotor.restoreFactoryDefaults();
   rightIntakeMotor.restoreFactoryDefaults();
  
leftIntakeMotor.setInverted(true);
   rightIntakeMotor.setInverted(true);

intakeMotorGroup = new MotorControllerGroup(leftIntakeMotor, rightIntakeMotor);


 leftIntakeEncoder = leftIntakeMotor.getEncoder();
     rightIntakeEncoder = rightIntakeMotor.getEncoder();


leftIntakeEncoder.setPosition(0);
rightIntakeEncoder.setInverted(false);
}


    @Override 
    public void periodic() {




    }

}
    



