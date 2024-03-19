package frc.robot.Components;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.Core.ScheduledComponent;
import frc.robot.Devices.Motor.Falcon;
import frc.robot.Util.MathPlus;
import frc.robot.Util.MotionController;
import frc.robot.Devices.BinarySensor;

public class Elevator extends ScheduledComponent {
    Falcon left;
    Falcon right;
    BinarySensor zero;
    PIDController controller;

    public Elevator(Falcon left, Falcon right, MotionController constant, BinarySensor zero) {
        this.left = left;
        this.right = right;
        this.zero = zero;
        this.controller = new PIDController(0.01, 0, 0, 0.02);

        // if (constant instanceof PIDController)
        // ((PIDController) constant).setDeadZone(0.5);

        left.setVelocityPD(constant.clone());
        right.setVelocityPD(constant.clone());
    }

    public boolean isDown() {
        return target == 0;
    }

    private Double target = 0.0;

    public double getHeight() {
        return left.getDegrees();
    }

    public double getTarget() {
        return target;
    }

    public void moveUp() {
        target = 360.0 * 23.5;
    }

    public void moveDown() {
        target = 0.0;
    }

    @Override
    protected void tick(double dTime) {
        if (target != null) {
            var goingDown = target == 0;
            var height = getHeight() + 0.1;

            var slowFac = 20;
            var max = 48 * slowFac;
            if (!goingDown)
                max *= 1.6;
            var vel = MathPlus.clampAbsVal(target - height, max) / slowFac;

            if (!zero.get() && goingDown) {
                left.setVoltage(0);
                right.setVoltage(0);
            } else {
                left.setVelocity(vel);
                right.setVelocity(vel);
            }

            controller.setSetpoint(target);
            // if (!zero.enabled()){
            // height = 0.0;
            // }
            // if(zero.get() == false){
            // height = 0.0;
            // }
            // left.setVoltage(controller.calculate(getHeight()));
            // right.setVoltage(controller.calculate(getHeight()));
        }
    }

    @Override
    protected void cleanUp() {
    }
}
