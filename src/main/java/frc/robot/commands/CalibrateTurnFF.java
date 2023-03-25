package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Swerve.DriveSubsystem;
/**
 * Implements a command to collect data to calibrate the swerve drive motor feed-forwards.
 *
 * <p>{@link frc.robot.Calibrations.SwerveCalibrations#Drive_FF_KS_GAIN}
 * {@link frc.robot.Calibrations.SwerveCalibrations#Drive_FF_KV_GAIN}
 * {@link frc.robot.Calibrations.SwerveCalibrations#Drive_FF_KA_GAIN}
 */
public class CalibrateTurnFF extends CommandBase {

    private enum State {
        RampPositive,
        RampNegative,
        StepPositive,
        StepNegative
    }

    private DriveSubsystem m_drivetrainSubsystem;
    /** Divide by 50 because there are 50 loops per 1 second with a 20ms control loop. */
    private static final double m_rampRateVpl = 0.25 / 50;
    private static final double m_maxRampVoltage = 3.0;
    private static final double m_stepVoltage = 3.0;
    private boolean m_isFinished;
    private State m_state;
    private double m_voltage;
    private int m_offVoltageCounter;
    private int m_onVoltageCounter;

    /**
     * Constructor for the calibrate tunning feed-forward command.
     *
     * @param drivetrainSubsystem uses the drivetrain subsystem
     */
    public CalibrateTurnFF(DriveSubsystem drivetrainSubsystem) {
        m_drivetrainSubsystem = drivetrainSubsystem;
        addRequirements(m_drivetrainSubsystem);
    }

    /**
     * Update the drivetrain state to indicate the subsystem is calibrating the modules.
     * 
     * <p>This command should be scheduled as non-interruptible.
     */
    @Override
    public void initialize() {
        m_isFinished = false;
        m_voltage = 0.0;
        m_offVoltageCounter = 150;
        m_onVoltageCounter = 100;
        m_state = State.RampPositive;
        DriverStation.reportWarning("Starting the turning motors feed-forward calibration", false);
    }

    /**
     * Run the calibration routine on each of the drivetrain swerve modules.
     */
    @Override
    public void execute() {
        switch (m_state) {
            case RampPositive:
                if (m_offVoltageCounter == 0) {    
                    m_voltage += m_rampRateVpl;
                    if (m_voltage >= m_maxRampVoltage) {
                        m_state = State.RampNegative;
                        m_voltage = 0.0;
                        m_offVoltageCounter = 150;
                    }
                } else {
                    --m_offVoltageCounter;
                }
                break;
                
            case RampNegative:
                if (m_offVoltageCounter == 0) {
                    m_voltage -= m_rampRateVpl;
                    if (m_voltage <= -m_maxRampVoltage) {
                        m_state = State.StepPositive;
                        m_voltage = 0.0;
                        m_offVoltageCounter = 150;
                    }
                } else {
                    --m_offVoltageCounter;
                }
                break;

            case StepPositive:
                if (m_offVoltageCounter == 0) {
                    m_voltage = m_stepVoltage;
                    if (m_onVoltageCounter == 0) {
                        m_state = State.StepNegative;
                        m_voltage = 0.0;
                        m_onVoltageCounter = 100;
                        m_offVoltageCounter = 150;
                    } else {
                        --m_onVoltageCounter;
                    }
                } else {
                    --m_offVoltageCounter;
                }
                break;
            
            case StepNegative:
                if (m_offVoltageCounter == 0) {
                    m_voltage = -m_stepVoltage;
                    if (m_onVoltageCounter == 0) {
                        m_voltage = 0;
                        m_isFinished = true;
                    } else {
                        --m_onVoltageCounter;
                    }
                } else {
                    --m_offVoltageCounter;
                }
                break;
            
            default:
                m_voltage = 0;
                m_isFinished = true;
                break;
        }

        m_drivetrainSubsystem.setModuleTurnVoltage(m_voltage);

    }

    /**
     * The command is complete when all the stages are complete.
     */
    @Override
    public boolean isFinished() {
        return m_isFinished;
    }

    /**
     * Update the drivetrain state when all of the modules have completed the Calibration.
     */
    @Override
    public void end(boolean interrupted) {
        DriverStation.reportWarning("Stopping the turning motors feed-forward calibration", false);
    }

}