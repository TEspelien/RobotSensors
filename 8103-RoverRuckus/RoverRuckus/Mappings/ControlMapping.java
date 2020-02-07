package org.firstinspires.ftc.teamcode.RoverRuckus.Mappings;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Utilities.Control.StickyGamepad;

public abstract class ControlMapping {
    StickyGamepad stickyGamepad1;
    StickyGamepad stickyGamepad2;
    Gamepad gamepad1;
    Gamepad gamepad2;

    public ControlMapping(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        stickyGamepad1 = new StickyGamepad(gamepad1);
        stickyGamepad2 = new StickyGamepad(gamepad2);
    }

    // All 0 to 1
    public abstract double driveStickX();
    public abstract double driveStickY();
    public abstract double turnSpeed();

    public abstract double translateSpeedScale();
    public abstract double turnSpeedScale();
    public abstract double armSpeed();

    public abstract boolean lockTo45();
    public abstract boolean lockTo225();
    public abstract boolean resetHeading();


    public abstract boolean flipOut();
    public abstract boolean flipBack();
    public abstract boolean flipToMin();
    public abstract double getExtendSpeed();
    public abstract double getSlewSpeed();
    public abstract double getGP2TurnSpeed();
    public abstract int getHangDir();
    public abstract boolean disableGP2Controls();
    public abstract boolean retakeControls();
    public abstract boolean openLatch();
    public abstract boolean quickReverse();

    public abstract boolean collectWithArm();
    public abstract boolean depositWithArm();

    public abstract boolean shakeCamera();
    public abstract double getSpinSpeed();

    public abstract boolean override();
    public abstract void setIntakeDir(int d);

    // Metric must be 0-1
    static double scaleControl(double metric, double min, double max) {
        return min + metric * (max - min);
    }

    static int boolsToDir(boolean forwards, boolean backwards) {
        return (forwards ? 1 : 0) + (backwards ? -1 : 0);
    }

    static double removeLowVals(double val, double threshold) {
        return (Math.abs(val) >= threshold) ? val : 0;
    }

    static double clamp(double val) {
        return Math.max(-1, Math.min(1, val));
    }

    public void update() {
        stickyGamepad1.update();
        stickyGamepad2.update();
    }
}
