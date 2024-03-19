package frc.robot.Devices;

import edu.wpi.first.wpilibj.PS4Controller;
import frc.robot.Core.ScheduledComponent;
import frc.robot.Core.Scheduler;
import frc.robot.Util.Container;
import frc.robot.Util.Vector2;

public class BetterPS4 extends PS4Controller {
    ScheduledComponent scheduledState;
    Container<Boolean> povChanged = new Container<Boolean>(false);

    public BetterPS4(int port) {
        super(port);

        var con = this;

        Container<Integer> lastPOV = new Container<Integer>(-1);
        Scheduler.registerTick(() -> {
            povChanged.val = false;
            if (lastPOV.val != con.getPOV()) {
                povChanged.val = true;
                lastPOV.val = con.getPOV();
            }
        });
    }

    public boolean povChanged() {
        return povChanged.val;
    }

    public Vector2 getLeftStick() {
        return new Vector2(getLeftX(), -getLeftY());
    }

    public Vector2 getRightStick() {
        return new Vector2(getRightX(), getRightY());
    }
}
