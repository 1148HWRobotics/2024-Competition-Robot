package frc.robot;

import frc.robot.Core.RobotPolicy;
import frc.robot.Devices.Motor.Falcon;

public class TestRobot {
        static RobotPolicy init() {
                Falcon turnFalcon1 = new Falcon(1, "drive", false);
                Falcon turnFalcon2 = new Falcon(2, "drive", false);
                Falcon turnFalcon3 = new Falcon(3, "drive", false);
                Falcon turnFalcon4 = new Falcon(4, "drive", false);

                return new RobotPolicy() {
                        @Override
                        public void teleop() {
                                turnFalcon1.setVoltage(1);
                                turnFalcon2.setVoltage(1);
                                turnFalcon3.setVoltage(1);
                                turnFalcon4.setVoltage(1);
                        }

                        @Override
                        public void autonomous() {

                        }

                        @Override
                        public void disabled() {

                        }

                        @Override
                        public void test() {

                        }
                };
        }
}
