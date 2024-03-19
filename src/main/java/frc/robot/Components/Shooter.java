package frc.robot.Components;

import edu.wpi.first.wpilibj.PS4Controller;
import frc.robot.Core.ScheduledComponent;
import frc.robot.Devices.AnyMotor;
import frc.robot.Util.DeSpam;
import frc.robot.Util.PDConstant;
import frc.robot.Util.PIDController;

public class Shooter extends ScheduledComponent {
    PS4Controller con;
    AnyMotor left;
    AnyMotor right;

   public double distance = 0;
    boolean isSpinning = false;

    DeSpam dSpam = new DeSpam(0.5);

    public Shooter(AnyMotor left, AnyMotor right, PDConstant constant, PS4Controller con) {
        this.con = con;
        this.left = left;
        this.right = right;

        left.setVelocityPD(new PIDController(constant));
        right.setVelocityPD(new PIDController(constant));
    }

    public void spin() {
        isSpinning = true;
    }

    public void stop() {
        isSpinning = false;
    }

    public void toggleSpinning() {
        isSpinning = !isSpinning;
    }

    public void setDistance(double dist) {
        distance = dist;
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public double vel;

    public void tick(double dTime) {
        if (!isSpinning) {
            left.setVoltage(0);
            right.setVoltage(0);
            return;
        }

        left.setVoltage(12);
        right.setVoltage(12);

        // var angle = Math.toRadians(40);
        // var velocity = 3 * distance / Math.cos(angle) * Math.sqrt(9.81 / (distance * Math.tan(angle) - 12));
        // checks if velocity is NaN

        // var vel = (-con.getRightY() + 1) * 150;

        // this.vel = vel;

        // dSpam.exec(() -> {
        //     System.out.println("vel: " + vel);
        // });

        // left.setVelocity(vel);
        // right.setVelocity(vel);
    }

    @Override
    protected void cleanUp() {

    }
}
