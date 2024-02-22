package frc.robot;

// import com.pathplanner.lib.auto.AutoBuilder;
// import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
// import com.pathplanner.lib.util.PIDConstants;
// import com.pathplanner.lib.util.ReplanningConfig;

import frc.robot.Auto.Positioning.FieldPositioning;
import frc.robot.Auto.Positioning.Position;
import frc.robot.Auto.Positioning.ResettablePosition;
import frc.robot.Components.Elevator;
import frc.robot.Components.Shooter;
import frc.robot.Core.RobotPolicy;
import frc.robot.Core.Scheduler;
import frc.robot.Devices.AbsoluteEncoder;
import frc.robot.Devices.BetterPS4;
import frc.robot.Devices.BinarySensor;
import frc.robot.Devices.Imu;
import frc.robot.Devices.LimeLight;
import frc.robot.Devices.Motor.Falcon;
import frc.robot.Drive.PositionedDrive;
import frc.robot.Drive.SwerveModule;
import frc.robot.Drive.SwerveModulePD;
import frc.robot.Util.AngleMath;
import frc.robot.Util.Container;
import frc.robot.Util.DeSpam;
import frc.robot.Util.PDConstant;
import frc.robot.Util.PIDController;
import frc.robot.Util.PWIDConstant;
import frc.robot.Util.PWIDController;
import frc.robot.Util.ScaleInput;
import frc.robot.Util.Vector2;

public class RobotContainer {

    static RobotPolicy init() {
        PositionedDrive drive;
        LimeLight limeLight;
        var imu = new Imu(18);
        var turnPD = new PIDController(new PDConstant(0.1, 0.8));
        var shooter = new Shooter(
                new Falcon(12, false),
                new Falcon(10, true),
                new PDConstant(0.1, 0));
        var intakeSensor = new BinarySensor(2); // aka beamBreak
        var elevator = new Elevator(
                new Falcon(9, false),
                new Falcon(13, true),
                new PWIDController(new PWIDConstant(0.03, 0, 0.005, 0.1)));
        var elevatorDownSensor = new BinarySensor(0);
        // return 0 if its down
        var intake = new Falcon(14, false);
        var carriage = new Falcon(11, true);
        carriage.setVelocityPD(new PIDController(new PDConstant(0.1, 0.0)));

        {
            var placeholderTurnPID = new PDConstant(0, 0);

            var moduleGoPID = new PWIDController(
                    new PWIDConstant(1, 0, 0, 0));

            var leftBackEncoder = new AbsoluteEncoder(22, "drive", -44.64843, false).setOffset(-90);
            var leftBackTurn = new Falcon(2, "drive", true);
            var leftBackGo = new Falcon(1, "drive", false);
            var leftBackRaw = new SwerveModule(leftBackTurn, leftBackGo, moduleGoPID);
            var leftBack = new SwerveModulePD(leftBackRaw, placeholderTurnPID, leftBackEncoder);

            var rightBackEncoder = new AbsoluteEncoder(21, "drive", 9.93164, false).setOffset(-90);
            var rightBackTurn = new Falcon(8, "drive", true);
            var rightBackGo = new Falcon(7, "drive", false);
            var rightBackRaw = new SwerveModule(rightBackTurn, rightBackGo, moduleGoPID);
            var rightBack = new SwerveModulePD(rightBackRaw, placeholderTurnPID, rightBackEncoder);

            var leftFrontEncoder = new AbsoluteEncoder(23, "drive", 45.96679, false).setOffset(-90);
            var leftFrontTurn = new Falcon(4, "drive", true);
            var leftFrontGo = new Falcon(3, "drive", false);
            var leftFrontRaw = new SwerveModule(leftFrontTurn, leftFrontGo, moduleGoPID);
            var leftFront = new SwerveModulePD(leftFrontRaw, placeholderTurnPID, leftFrontEncoder);

            var rightFrontEncoder = new AbsoluteEncoder(24, "drive", -99.66796, false).setOffset(-90);
            var rightFrontTurn = new Falcon(6, "drive", true);
            var rightFrontGo = new Falcon(5, "drive", false);
            var rightFrontRaw = new SwerveModule(rightFrontTurn, rightFrontGo, moduleGoPID);
            var rightFront = new SwerveModulePD(rightFrontRaw, placeholderTurnPID, rightFrontEncoder);

            limeLight = new LimeLight();

            drive = new PositionedDrive(leftFront, rightFront, leftBack, rightBack, 23.0, 23.0);
        }

        limeLight.setCamMode(true);
        var fieldPositioning = new FieldPositioning(drive, imu, limeLight, new Position(0, new Vector2(0, 0)));

        var resettablePos = new ResettablePosition(fieldPositioning);
        // AutoBuilder.configureHolonomic(
        // () -> resettablePos.getPosition(), // Robot pose supplier
        // (pos) -> resettablePos.reset(pos), // Method to reset odometry (will be
        // called if your auto has a
        // // starting
        // // pose)
        // () -> fieldPositioning.getRobotRelativeSpeeds(), // ChassisSpeeds supplier.
        // MUST BE ROBOT RELATIVE
        // (speeds) -> drive.fromChassisSpeeds(speeds), // Method that will drive the
        // robot given ROBOT RELATIVE
        // // ChassisSpeeds
        // new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should
        // likely live in your
        // // Constants class
        // new PIDConstants(5.0, 0.0, 0.0), // Translation PID constants
        // new PIDConstants(5.0, 0.0, 0.0), // Rotation PID constants
        // 4.5, // Max module speed, in m/s
        // 0.4, // Drive base radius in meters. Distance from robot center to furthest
        // module.
        // new ReplanningConfig() // Default path replanning config. See the API for the
        // options here
        // ),
        // () -> {
        // // Boolean supplier that controls when the path will be mirrored for the red
        // // alliance
        // // This will flip the path being followed to the red side of the field.
        // // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

        // Alliance alliance = DriverStation.getAlliance();
        // if (alliance != Alliance.Invalid) {
        // return alliance == DriverStation.Alliance.Red;
        // }
        // return false;
        // },
        // new Subsystem() {

        // } // Reference to this subsystem to set requirements
        // );

        var con = new BetterPS4(0);

        return new RobotPolicy() {

            public void teleop() {
                drive.setAlignmentThreshold(0.5);

                var constants = new PDConstant(-0.2, -0.0).withMagnitude(0.5);
                drive.setConstants(constants);

                final Container<Boolean> isShooting = new Container<>(false);
                final Container<Boolean> inCarrage = new Container<>(false);
                final Container<Boolean> inIntake = new Container<>(false);

                Scheduler.registerTick(() -> {
                    if (!intakeSensor.get()) {
                        inIntake.val = true;

                    }

                    if (intakeSensor.get() && inIntake.val) {
                        inIntake.val = false;
                        inCarrage.val = true;
                    }

                    System.out.println(intakeSensor.get());
                    if (isShooting.val) {
                        // do nothing
                    } else if (con.getL2Button() && !elevatorDownSensor.get() && !inCarrage.val) {
                        carriage.setVoltage(8);
                        intake.setVoltage(8);
                    } else if (con.getL1Button()) {
                        carriage.setVoltage(-8);
                        intake.setVoltage(-8);
                    } else {
                        carriage.setVelocity(0);
                        intake.setVoltage(0);
                    }

                    if (con.getR2Button()) {
                        inCarrage.val = false;
                        carriage.setVoltage(8);
                    }

                    if (con.getR2ButtonPressed()) {
                        shooter.toggleSpinning();
                    }

                    // elevator logic
                    if (elevatorDownSensor.get() && !con.getR1Button()) {

                    } else

                    if (con.getR1Button()) {

                    } else if (con.getR1ButtonReleased()) {
                        isShooting.val = true;
                        carriage.setVoltage(7);
                        Scheduler.setTimeout(() -> {
                            carriage.setVoltage(0);
                            isShooting.val = false;
                            inCarrage.val = false;
                        }, 1);
                    }

                    // Arrow buttons control
                    if (con.povChanged()) {
                        switch (con.getPOV()) {
                            case 0: // Button Up
                                shooter.toggleSpinning();
                                break;
                            case 90: // Button Right
                                // Test Mode
                                break;
                            case 180: // Button Down
                                // Toggle auto fw
                                break;
                            case 270: // Button Left
                                limeLight.setCamMode(!limeLight.getCamMode());
                                break;
                        }
                    }

                    if (con.getCrossButtonPressed())
                        if (elevator.getTarget() == 0)
                            elevator.moveUp();
                        else
                            elevator.moveDown();

                    final var displacementFromTar = new Vector2(327.87, 34.25).minus(fieldPositioning.getPosition());
                    // 2 82.1238544369317, 32.670822887202704
                    var correction = -turnPD.solve(AngleMath.getDelta(displacementFromTar.getTurnAngleDeg() + 90,
                            fieldPositioning.getTurnAngle()));

                    if ((con.getLeftStick().getMagnitude() + Math.abs(con.getRightX()) > 0.1) || con.getR1Button())
                        drive.power(ScaleInput.curve(con.getLeftStick().getMagnitude(), 1.5) * 12.0, // voltage
                                (con.getLeftStick().getAngleDeg()) - fieldPositioning.getTurnAngle(), // go angle
                                !con.getR1Button() ? con.getRightX() * -12.0
                                        : correction, // change back to correction
                                // turn voltage
                                // 0,
                                false);
                    else
                        drive.power(0, 0, 0);
                });
            }

            public void autonomous() {
                limeLight.setCamMode(true);
            }

            public void test() {
                
            }

            @Override
            public void disabled() {

            }

        };
    }

    @Override
    public String toString() {
        return "RobotContainer [Michael Barr Was Here]";
    }
}
