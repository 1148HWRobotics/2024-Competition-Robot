package frc.robot;

import frc.robot.Devices.AbsoluteEncoder;
import frc.robot.Devices.Motor.Falcon;
import frc.robot.Drive.SwerveModule;
import frc.robot.Drive.SwerveModulePD;
import frc.robot.Util.PDConstant;
import frc.robot.Util.PIDConstant;
import frc.robot.Util.PIDController;
import frc.robot.Util.PWIDConstant;
import frc.robot.Util.PWIDController;

public class TestRobot extends Robot {
    @Override
    public void testPeriodic(){
            var moduleGoPID = new PWIDController(
                    new PWIDConstant(1, 0, 0, 0));
            var placeholderTurnPID = new PDConstant(0.1, 0);
            var controller = new PIDController(placeholderTurnPID);
            var leftFrontEncoder = new AbsoluteEncoder(23, "drive", 45.96679, false).setOffset(0);
            var leftFrontTurn = new Falcon(4, "drive", true);
            var leftFrontGo = new Falcon(3, "drive", false);
            var leftFrontRaw = new SwerveModule(leftFrontTurn, leftFrontGo, moduleGoPID);
            var leftFront = new SwerveModulePD(leftFrontRaw, placeholderTurnPID, leftFrontEncoder);

            System.out.println(leftFrontEncoder.absVal() + " ," + controller.solve(leftFront.error));
    }
}
