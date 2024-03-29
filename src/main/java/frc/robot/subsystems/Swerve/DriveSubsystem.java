// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Swerve;

import java.util.function.DoubleSupplier;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
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
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class DriveSubsystem extends SubsystemBase {
  //Create Slew rate limiters
  private final SlewRateLimiter xLimiter, yLimiter, turningLimiter;
  // Create MAXSwerveModules
  private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
      DriveConstants.kFrontLeftDrivingCanId,
      DriveConstants.kFrontLeftTurningCanId,
      DriveConstants.kFrontLeftChassisAngularOffset,
      "m_frontLeft");

  private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
      DriveConstants.kFrontRightDrivingCanId,
      DriveConstants.kFrontRightTurningCanId,
      DriveConstants.kFrontRightChassisAngularOffset,
      "m_frontRight");

  private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
      DriveConstants.kRearLeftDrivingCanId,
      DriveConstants.kRearLeftTurningCanId,
      DriveConstants.kBackLeftChassisAngularOffset,
      "m_rearLeft");

  private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
      DriveConstants.kRearRightDrivingCanId,
      DriveConstants.kRearRightTurningCanId,
      DriveConstants.kBackRightChassisAngularOffset,
      "m_rearLeft");

  

  private final PIDController m_VisionLockController = new PIDController(0.014, 0, 0);
  
  private final DataLog m_log;
  

  // The gyro sensor
private final AHRS m_gyro = new AHRS(SPI.Port.kMXP, (byte) 200);
// private final AHRS m_gyro = new AHRS(I2C.Port.kOnboard, (byte) 200);

 //private final AHRS m_gyro = new 
  

  private final DoubleLogEntry m_navYawLog;
  private final DoubleLogEntry m_navPitchLog;
  private final DoubleLogEntry m_navRollLog;
  private final DoubleLogEntry m_calculatedPitch;
  private final StringLogEntry m_currentCommandLog;


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
          new Pose2d(0, 0, new Rotation2d(0)), //
          VecBuilder.fill(0.85, 0.85, Units.degreesToRadians(0.5)), // initiial was 0.05 for both on top and 0.5 for bottom, 0.05, 0.05, 0.65
          VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(60))); // 0.5, 0.5, 50 
//0.15, 0.15, 0.5          .75, 0.75, 60

          //1.5 1.5 0.65
          //0.05, 0.05, 10
      LimelightHelpers.LimelightResults llresults = LimelightHelpers.getLatestResults("limelight");
      ShuffleboardTab limeLightTab = Shuffleboard.getTab("limelight");
      Field2d m_field = new Field2d();
      boolean limelightToggledOn = true;
      


  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    this.xLimiter = new SlewRateLimiter(1.8);
    this.yLimiter = new SlewRateLimiter(1.8);

    // this.xLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAccelerationUnitsPerSecond);
    // this.yLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAccelerationUnitsPerSecond);
    this.turningLimiter = new SlewRateLimiter(DriveConstants.kTeleDriveMaxAngularSpeedRadiansPerSecond);
    m_VisionLockController.setSetpoint(0);


    double[] botposeBlue = llresults.targetingResults.botpose_wpired;
       limeLightTab.add(m_field);
       limeLightTab.addDouble("Vision Blue X", ()-> LimelightHelpers.getBotPose3d_wpiBlueX("limelight"));
       limeLightTab.addDouble("Vision Blue Y", ()-> LimelightHelpers.getBotPose3d_wpiBlueY("limelight"));
     m_gyro.setAngleAdjustment(-1);   

    zeroHeading();
   
   
  
    m_log = DataLogManager.getLog();
    

    m_navYawLog = new DoubleLogEntry(m_log, "swerve/pigeon/yaw");
    m_navPitchLog = new DoubleLogEntry(m_log, "swerve/pigeon/pitch");
    m_navRollLog = new DoubleLogEntry(m_log, "swerve/pigeon/roll");
    m_calculatedPitch = new DoubleLogEntry(m_log, "swerve/pigeon/calculated_pitch");
    m_currentCommandLog = new StringLogEntry(m_log, "/swerve/command");


     AutoBuilder.configureHolonomic(
      this::getPose,
      this::resetOdometry,
      this::getChassisSpeeds,
      this::setRobotRelativeSpeeds, 
       AutoConstants.autoBuilderPathConfig,
      this);
  
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    
    logData();
    // System.out.println(Timer.getFPGATimestamp());
    // System.out.println(LimelightHelpers.getLatency_Pipeline("limelight"));

  // if(LimelightHelpers.getBotPose3d_wpiBlue("limelight").toPose2d() != null)
  //       m_poseEstimator.addVisionMeasurement(LimelightHelpers.getBotPose3d_wpiBlue("limelight").toPose2d(), Timer.getFPGATimestamp() - LimelightHelpers.getLatency_Pipeline("limelight"));
  
//System.out.println(LimelightHelpers.getBotPose3d_wpiBlue("limelight").toPose2d().getX());


    //eventually figure out what each result value is

   //TODO: addMyVisionMeasurment();
    updateOdometry();
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

  public double getPoseHeading()
  {
    return m_poseEstimator.getEstimatedPosition().getRotation().getDegrees();
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
    double m_HeadingDegrees = getPose().getRotation().getDegrees();

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(m_HeadingDegrees)) //TODO: changed getHeadingDegrees()
            : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
    
  }


  //Turbo Spin
  public void Kachow(double xSpeed, double ySpeed, double rot, boolean fieldRelative) 
  {
    // Adjust input based on max speed
    // xSpeed *= 0.5;
    // ySpeed *= 0.5;
    rot *= 0.8;


    xSpeed = xLimiter.calculate(xSpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    ySpeed = yLimiter.calculate(ySpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    rot = turningLimiter.calculate(rot) * DriveConstants.kMaxAngularSpeed;
    double m_HeadingDegrees = getPose().getRotation().getDegrees();

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(m_HeadingDegrees)) //TODO: changed getHeadingDegrees()
            : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
    
  }




  public void DriveWithVisionLockOn(double xSpeed, double ySpeed, double armPosition, boolean fieldRelative)
  {

    double rot;
    if(armPosition > 0.15 && LimelightHelpers.getCurrentPipelineIndex("limelight-bottom") == 1)
    {
      LimelightHelpers.setPipelineIndex("limelight-bottom", 0);
    }
    else if(armPosition < 0.15 && LimelightHelpers.getCurrentPipelineIndex("limelight-bottom") == 0 )
    {
      LimelightHelpers.setPipelineIndex("limelight-bottom", 1);
    }
          
      rot = m_VisionLockController.calculate(LimelightHelpers.getTX("limelight-bottom"));

 


    xSpeed = xLimiter.calculate(xSpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    ySpeed = yLimiter.calculate(ySpeed) * DriveConstants.kMaxSpeedMetersPerSecond;
    rot = turningLimiter.calculate(rot) * DriveConstants.kMaxAngularSpeed;
    double m_HeadingDegrees = DriverStation.getAlliance() == Alliance.Red ? getPose().getRotation().getDegrees() : getPose().getRotation().getDegrees();

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(m_HeadingDegrees)) //TODO: changed getHeadingDegrees()
            : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
    //System.out.println("debugging in your mom -()-()-");
    
  }

  public void setModuleDriveVoltage(double voltage) {
    m_frontLeft.setDriveVoltage(voltage);
    m_frontRight.setDriveVoltage(voltage);
    m_rearLeft.setDriveVoltage(voltage);
    m_rearRight.setDriveVoltage(voltage);
    
}

public void setModuleTurnVoltage(double voltage) {
  m_frontLeft.setTurnVoltage(voltage);
  m_frontRight.setTurnVoltage(voltage);
  m_rearLeft.setTurnVoltage(voltage);
  m_rearRight.setTurnVoltage(voltage);
  
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
    //m_gyro.setAngleAdjustment(-1);
    
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
          // return m_gyro.getFusedHeading() * -1;
          return (m_gyro.getAngle() *-1);
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
          // return Rotation2d.fromDegrees(m_gyro.getFusedHeading() * -1);
          return Rotation2d.fromDegrees(m_gyro.getAngle() * -1);
         }
      //
      //    // We have to invert the angle of the NavX so that rotating the robot counter-clockwise makes the angle increase.
         return Rotation2d.fromDegrees(m_gyro.getAngle() * -1);
  }

  public double getRoll()
  {
    return m_gyro.getRoll();
  }

  public double getEstimatedX()
  {
    return m_poseEstimator.getEstimatedPosition().getX();
  }

  public double getEstimatedY()
  {
    return m_poseEstimator.getEstimatedPosition().getY();
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

  public SwerveModuleState[] getModuleStates()
  {
    SwerveModuleState[] moduleStates = new SwerveModuleState[4];
    MAXSwerveModule[] modules = getMaxSwerveModules();
    for(int i = 0; i < modules.length; i++)
    {
       moduleStates[i] = modules[i].getState();
    }
    return moduleStates;
  }

  public ChassisSpeeds getChassisSpeeds()
  {
    return Constants.DriveConstants.kDriveKinematics.toChassisSpeeds(getModuleStates());
  }

  public void setRobotRelativeSpeeds(ChassisSpeeds chassisSpeeds)
  {
    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(chassisSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(
      swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
  m_frontLeft.setDesiredState(swerveModuleStates[0]);
  m_frontRight.setDesiredState(swerveModuleStates[1]);
  m_rearLeft.setDesiredState(swerveModuleStates[2]);
  m_rearRight.setDesiredState(swerveModuleStates[3]);
  }

 









  public double getAverageDistanceMeters()
  {
  return m_frontLeft.getEncoderCounts();
  }

  public double getFrontLeftRot()
  {
    return m_frontLeft.getEncoderCounts();
  }

  public double getFrontRightRot()
  {
    return m_frontRight.getEncoderCounts();
  }

  public double getBackLeftRot()
  {
    return m_rearLeft.getEncoderCounts();
  }

  public double getBackRightRot()
  {
    return m_rearRight.getEncoderCounts();
  }

  public void addMyVisionMeasurment()
  {

        Alliance m_Alliance = DriverStation.getAlliance();
        double acceptableScoreMergeDistance = 3.2; //2.8
        double acceptablePickUpMergeDistance = 13.4;

      
    try{
        if(m_Alliance == Alliance.Blue)
        {
          if(LimelightHelpers.getBotPose3d_wpiBlue("limelight").getX() < acceptableScoreMergeDistance && LimelightHelpers.getBotPose3d_wpiBlue("limelight").getX() != 0 || LimelightHelpers.getBotPose3d_wpiBlue("limelight").getX() > acceptablePickUpMergeDistance)
          {
            m_poseEstimator.addVisionMeasurement(LimelightHelpers.getBotPose3d_wpiBlue("limelight").toPose2d(), Timer.getFPGATimestamp() - LimelightHelpers.getLatency_Pipeline("limelight")/1000);
            //System.out.println(LimelightHelpers.getBotPose3d_wpiRed("limelight").getX());
    
          }
        }
        else if(m_Alliance == Alliance.Red)
        {
          System.out.println(LimelightHelpers.getBotPose3d_wpiRed("limelight").getX());
          if(LimelightHelpers.getBotPose3d_wpiRed("limelight").getX() < acceptableScoreMergeDistance && LimelightHelpers.getBotPose3d_wpiRed("limelight").getX() != 0 || LimelightHelpers.getBotPose3d_wpiRed("limelight").getX() > acceptablePickUpMergeDistance)
          {
            m_poseEstimator.addVisionMeasurement(LimelightHelpers.getBotPose3d_wpiRed("limelight").toPose2d(), Timer.getFPGATimestamp() - LimelightHelpers.getLatency_Pipeline("limelight")/1000);
            System.out.println("more yeah");
          }
        }
        else 
        System.out.println("Alliance not found, no limelight data");
    } catch(Exception e)
    {
      System.out.println("yeah that an error with vision: " + e);
    }

   }

   public void toggleLimelight()
   {
    if(limelightToggledOn)
    {
    LimelightHelpers.setLEDMode_ForceOn("limelight");
    LimelightHelpers.setLEDMode_ForceOn("limelight-bottom");
    limelightToggledOn = false;
    }
    else if(!limelightToggledOn)
    {
      LimelightHelpers.setLEDMode_ForceOff("limelight");
      LimelightHelpers.setLEDMode_ForceOff("limelight-bottom");
      limelightToggledOn = true;
    }

   }

   private void logData() {
    long timeStamp = (long) (Timer.getFPGATimestamp() * 1e6);
    m_navYawLog.append(m_gyro.getYaw(), timeStamp);
    m_navPitchLog.append(m_gyro.getPitch(), timeStamp);
    m_navRollLog.append(m_gyro.getRoll(), timeStamp);
    m_calculatedPitch.append(getHeadingDegrees(), timeStamp);

    Command currentCommand = getCurrentCommand();
    m_currentCommandLog.append(currentCommand != null ? currentCommand.getName() : "None", timeStamp);
}







  
}
