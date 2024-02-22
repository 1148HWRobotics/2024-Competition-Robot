package frc.robot.Devices;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Core.ScheduledComponent;

public class BinarySensor extends ScheduledComponent {
    DigitalInput input;
    Boolean lastState = false;
    boolean justChanged = false;
    final boolean reversed;

    public BinarySensor(int input, boolean reversed) {
        this.input = new DigitalInput(input);
        this.reversed = reversed;
        if (reversed)
            lastState = true;
    }

    public BinarySensor(int input) {
        this(input, false);
    }

    public boolean get() {
        if (reversed)
            return !input.get();
        else
            return input.get();
    }

    public boolean justChanged() {
        return justChanged;
    }

    public boolean disabled() {
        return justChanged() && !get();
    }

    public boolean enabled() {
        return justChanged() && get();
    }

    @Override
    protected void tick(double dTime) {
        final boolean s = get();

        justChanged = lastState != s;
        lastState = s;
    }

    @Override
    protected void cleanUp() {

    }
}
