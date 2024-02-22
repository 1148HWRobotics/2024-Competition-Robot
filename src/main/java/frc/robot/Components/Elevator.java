package frc.robot.Components;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.robot.Core.ScheduledComponent;
import frc.robot.Devices.Motor.Falcon;
import frc.robot.Util.MathPlus;
import frc.robot.Util.MotionController;
import frc.robot.Util.PDConstant;
import frc.robot.Util.PIDController;

public class Elevator extends ScheduledComponent {
    Falcon left;
    Falcon right;
    PIDController controller;

    public Elevator(Falcon left, Falcon right, MotionController constant) {
        this.left = left;
        this.right = right;
        left.setVelocityPD(constant);
    }

    private Double target = 0.0;

    public double getHeight() {
        return left.getDegrees();
    }

    public double getTarget() {
        return target;
    }

    public void moveUp() {
        target = 360.0 * 15;
    }

    public void moveDown() {
        target = 0.0;
    }

    @Override
    protected void tick(double dTime) {
        if (target != null) {
            left.setVelocity(MathPlus.clampAbsVal(target - getHeight(), 180));
        }   
    }

    @Override
    protected void cleanUp() {
    }
}
