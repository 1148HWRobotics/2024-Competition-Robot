package frc.robot.Components;

import frc.robot.Devices.AnyMotor;
import frc.robot.Util.PDConstant;
import frc.robot.Util.PIDController;

public class Shooter {
    AnyMotor left;
    AnyMotor right;
    PIDController constant;
    boolean isSpinning;

    public Shooter(AnyMotor left, AnyMotor right, PDConstant constant) {
        this.left = left;
        this.right = right;

        this.constant = new PIDController(constant);
        this.isSpinning = false;
    }

    public void toggleSpinning() {
        isSpinning = !isSpinning;

        if (isSpinning) {
            left.setVoltage(12);
            right.setVoltage(12);
        } else {
            left.setVoltage(0);
            right.setVoltage(0);
        }
    }
}
