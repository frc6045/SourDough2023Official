// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Swerve;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveSubsystem extends SubsystemBase {
  //Create Slew rate limiters
  private final SlewRateLimiter xLimiter, yLimiter, turningLimiter;
  // Create MAXSwerveModules
  private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
      DriveConstants.kFrontLeftDrivingCanId,
      DriveConstants.kFrontLeftTurningCanId,
      DriveConstants.kFrontLeftChassisAngularOffset);

  private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
      DriveConstants.kFrontRightDrivingCanId,
      DriveConstants.kFrontRightTurningCanId,
      DriveConstants.kFrontRightChassisAngularOffset);

  private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
      DriveConstants.kRearLeftDrivingCanId,
      DriveConstants.kRearLeftTurningCanId,
      DriveConstants.kBackLeftChassisAngularOffset);

  private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
      DriveConstants.kRearRightDrivingCanId,
      DriveConstants.kRearRightTurningCanId,
      DriveConstants.kBackRightChassisAngularOffset);

  // The gyro sensor
  private final AHRS m_gyro = new AHRS(SPI.Port.kMXP, (byte) 200);


  // Odometry class for tracking robot pose

  //gyro.getHeadingDegrees();






      private final SwerveDrivePoseEstimator m_poseEstimator =
      new SwerveDrivePoseEstimator(
          DriveConstants.kDriveKinematics,
          Rotation2d.fromDegrees(getHeadingDegrees()),
          new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
          },
          new Pose2d(13.5, 1.45, new Rotation2d(0)), //14.6, 1, new Rotation2d(0)
          VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(0)), // initiial was 0.05 for both on top and 0.5 for bottom
          VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(720)));

      LimelightHelpers.LimelightResults llresults = LimelightHelpers.getLatestResults("limelight");
      ShuffleboardTab limeLightTab = Shuffleboard.getTab("limelight");
      Field2d m_field = new Field2d();


  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    this.xLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAccelerationUnitsPerSecond);
    this.yLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAccelerationUnitsPerSecond);
    this.turningLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAngularSpeedRadiansPerSecond);

    double[] botposeBlue = llresults.targetingResults.botpose_wpired;
    limeLightTab.add("limeLightBluePose", botposeBlue[0]);
    limeLightTab.add(m_field);
    m_gyro.setAngleAdjustment(-1);   

    zeroHeading();
  
    


  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    boolean colorEqualsBlue = true;

    // double xDistance = m_poseEstimator.getEstimatedPosition().getX() - LimelightHelpers.getBotPose3d_wpiBlue("limelight").getX();
    // double yDistance = m_poseEstimator.getEstimatedPosition().getY() - LimelightHelpers.getBotPose3d_wpiBlue("limelight").getY();
    // double rotDistance = m_poseEstimator.getEstimatedPosition().getRotation().getDegrees() - LimelightHelpers.getBotPose3d_wpiBlue("limelight").getRotation().getHeadingDegrees() / Math.PI / 2 * 360;
    if(LimelightHelpers.getBotPose3d_wpiBlue("limelight").getX() > 13.6)
    {
      if(LimelightHelpers.getBotPose2d("limelight").getX() != 0 && colorEqualsBlue == true)
      {
        m_poseEstimator.addVisionMeasurement(LimelightHelpers.getBotPose3d_wpiBlue("limelight").toPose2d(), Timer.getFPGATimestamp() - LimelightHelpers.getLatency_Pipeline("limelight"));
      }
      if(LimelightHelpers.getBotPose2d("limelight").getX() != 0 && colorEqualsBlue == false)
      {
        m_poseEstimator.addVisionMeasurement(LimelightHelpers.getBotPose3d_wpiRed("limelight").toPose2d(), Timer.getFPGATimestamp() - LimelightHelpers.getLatency_Pipeline("limelight"));
      }
    }
    

    updateOdometry();
    //eventually figure out what each result value is

    m_field.setRobotPose(getPose());

   



    

  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return m_poseEstimator.getEstimatedPosition();
  }



  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    m_poseEstimator.resetPosition(
        Rotation2d.fromDegrees(getHeadingDegrees()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        },
        pose);
  }

  public void updateOdometry() 
  {
    m_poseEstimator.update(
        Rotation2d.fromDegrees(getHeadingDegrees()),
        new SwerveModulePosition[] {
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_rearLeft.getPosition(),
          m_rearRight.getPosition()
        });
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed        Speed of the robot in the x direction (forward).
   * @param ySpeed        Speed of the robot in the y direction (sideways).
   * @param rot           Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the
   *                      field.
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) 
  {
    // Adjust input based on max speed
    // xSpeed *= 0.5;
    // ySpeed *= 0.5;
    rot *= 0.3;


    xSpeed = xLimiter.calculate(xSpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    ySpeed = yLimiter.calculate(ySpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    rot = turningLimiter.calculate(rot) * DriveConstants.kMaxAngularSpeed;

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(getHeadingDegrees()))
            : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
    
  }

  /**
   * Sets the wheels into an X formation to prevent movement.
   */
  public void setX() {
    m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
  }



  /**
   * Sets the swerve ModuleStates.
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_rearLeft.setDesiredState(desiredStates[2]);
    m_rearRight.setDesiredState(desiredStates[3]);
  }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    m_gyro.zeroYaw();
    m_gyro.setAngleAdjustment(-1);
    
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeadingDegrees() {
   // return Rotation2d.fromDegrees(m_gyro.getHeadingDegrees()).getDegrees();
      // FIXME Uncomment if you are using a NavX
    // FIXME Remove if you are using a Pigeon
    //return Rotation2d.fromDegrees(m_pigeon.getFusedHeading());

    // FIXME Uncomment if you are using a NavX
    if (m_gyro.isMagnetometerCalibrated()) {
      //      // We will only get valid fused headings if the magnetometer is calibrated
          return m_gyro.getFusedHeading() * -1;
         }
      //
      //    // We have to invert the angle of the NavX so that rotating the robot counter-clockwise makes the angle increase.
         return (m_gyro.getAngle() *-1);
        }



  public Rotation2d getHeadingRotation2d()
  {
    // FIXME Remove if you are using a Pigeon
    //return Rotation2d.fromDegrees(m_pigeon.getFusedHeading());

    // FIXME Uncomment if you are using a NavX
    if (m_gyro.isMagnetometerCalibrated()) {
      //      // We will only get valid fused headings if the magnetometer is calibrated
          return Rotation2d.fromDegrees(m_gyro.getFusedHeading() * -1);
         }
      //
      //    // We have to invert the angle of the NavX so that rotating the robot counter-clockwise makes the angle increase.
         return Rotation2d.fromDegrees(m_gyro.getAngle() * -1);
  }
  



  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  public MAXSwerveModule[] getMaxSwerveModules()
  {
    MAXSwerveModule[] maxArray = {
      m_frontLeft, 
      m_frontRight, 
      m_rearLeft, 
      m_rearRight};
    return maxArray;
  }

  public double getAverageDistanceMeters()
  {
  return m_frontLeft.getEncoderCounts();
  
  }



  
}
