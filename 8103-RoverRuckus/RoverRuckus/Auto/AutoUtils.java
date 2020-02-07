package org.firstinspires.ftc.teamcode.RoverRuckus.Auto;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.DriveSystems.Mecanum.RoadRunner.SampleMecanumDriveREV;
import org.firstinspires.ftc.teamcode.Mechanisms.SparkyTheRobot;
import org.firstinspires.ftc.teamcode.RoverRuckus.Deployers.Auto.StartingPosition;
import org.firstinspires.ftc.teamcode.Vision.VuforiaCVUtil;
import org.opencv.core.Rect;

@Config
public abstract class AutoUtils extends VuforiaCVUtil {
    public StartingPosition startingPosition;
    public SparkyTheRobot robot;

    public static double MARKER_DEPLOYER_DEPLOY = 0;
    public static double MARKER_DEPLOYER_RETRACTED = 0.85;
    public static int MS_TO_WAIT_AFTER_TURN = 100;

    public static double PARKING_MARKER_EXTENDED = 1;
    public static double PARKING_MARKER_RETRACTED = 0;

    public static double HANG_HOLD_POWER = -0.25;
    public static String SETUP_ERROR_MSG = "WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG " +
            "WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG WRONG";

    public static double DEPO_START_HEADING = Math.PI * 1.75;
    public static double CRATER_START_HEADING = Math.PI * 0.75;

    double TURN_MAX_SPEED = 0.5;
    double ACCEPTABLE_HEADING_VARIATION = Math.PI / 180;
    double SETUP_ERROR_DETECTION_TOLERANCE = Math.PI / 12;

    public void setWinchHoldPosition() {
        robot.winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.winch.setPower(1);
        robot.winch.setTargetPosition(0);
    }

    public void unhookFromLander(SampleMecanumDriveREV drive, SparkyTheRobot robot, double finalHeading) {

        // Lower robot in two phases
        // In the first phase, we will lower the robot "manually"
        // In the second phase, we will just freefall to be faster

        //robot.leds.setPattern(RevBlinkinLedDriver.BlinkinPattern.LIGHT_CHASE_RED);
        robot.updateReadings();
        robot.winch.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.winch.setPower(1);
        while (opModeIsActive()) {
            robot.updateReadings();
            if (robot.primaryIMU.getGravity().zAccel >= 9.6) {
                break;
            }
        }
        robot.sleep(250);
        robot.winch.setMotorEnable();
        robot.winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.winch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.winch.setPower(0);

        robot.updateReadings();
        turnToPos(finalHeading, 1);
    }

    public void refoldMechanisms() {
        robot.winch.setMotorDisable();
        robot.intake.goToMin();
    }

    public static int getMiddlePosition(Rect boundingBox) {
        return boundingBox.x + (boundingBox.width / 2);
    }
    public void followPath(SampleMecanumDriveREV drive, Trajectory trajectory) {
        //robot.leds.setPattern(RevBlinkinLedDriver.BlinkinPattern.BREATH_RED);
        drive.followTrajectory(trajectory);
        while (!isStopRequested() && drive.isFollowingTrajectory()) {
            drive.update();
        }
    }

    public void followPathWatchingWinch(SampleMecanumDriveREV drive, Trajectory trajectory) {
        drive.followTrajectory(trajectory);
        while (!isStopRequested() && drive.isFollowingTrajectory()) {
            drive.update();
            if (!robot.hangSwitch.getState()) {
                robot.winch.setPower(0);
            }
        }
    }

    public void turnToPos(double pos) {
        turnToPos(pos, 0);
    }

    public void turnToPos(double pos, int forcedDir) {
        double difference = Double.MAX_VALUE;

        while (Math.abs(difference) > ACCEPTABLE_HEADING_VARIATION && opModeIsActive()) {
            double heading = robot.getHeading();
            difference = robot.getSignedAngleDifference(pos, heading);

            double turnSpeed = Math.max(-TURN_MAX_SPEED, Math.min(TURN_MAX_SPEED, difference));
            turnSpeed = Math.copySign(Math.max(0.05, Math.abs(turnSpeed)), turnSpeed);

            // Optionally force turn direction
            // But allow for minor changes in direction the other way
            if (forcedDir != 0 && Math.abs(difference) >= Math.PI / 2) {
                turnSpeed = Math.copySign(turnSpeed, forcedDir);
            }

            double[] unscaledMotorPowers = new double[4];

            for (int i = 0; i < unscaledMotorPowers.length; i++) {
                if (i % 2 == 0) {
                    unscaledMotorPowers[i] = -turnSpeed;
                } else {
                    unscaledMotorPowers[i] = turnSpeed;
                }
            }
            telemetry.addData("Difference", difference);
            telemetry.addData("Heading", heading);
            telemetry.addData("Turn speed", turnSpeed);
            telemetry.update();

            robot.setMotorSpeeds(unscaledMotorPowers);
        }
        stopMoving();
        Log.i("AutoUtils", "Finished turn at " + robot.getHeading());
        robot.sleep(MS_TO_WAIT_AFTER_TURN);
        if (Math.abs(robot.getSignedAngleDifference(pos, robot.getHeading())) > ACCEPTABLE_HEADING_VARIATION) {
            turnToPos(pos);
        }
    }

    public void stopMoving() {
        for (DcMotor m : robot.motorArr) {
            m.setPower(0);
        }
    }

    public GoldPosition waitAndWatchMinerals() {
        // This sometimes might count the same frame twice
        // but we're OK with that - we'll just run this for
        // a set time, not a set frame count
        robot.winch.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.winch.setPower(HANG_HOLD_POWER);
        GoldPosition result = GoldPosition.CENTER;

        // Positions:

        // Left: 517
        // Center: 297
        // Right: 80

        double targetDir = startingPosition == StartingPosition.CRATER ?
                CRATER_START_HEADING : DEPO_START_HEADING;

        while (!isStarted() && !isStopRequested()) {
            int middleLine = getMiddlePosition(detector.getFoundRect());

            if (middleLine < 200) {
                result = GoldPosition.RIGHT;
            } else if (middleLine < 400) {
                result = GoldPosition.CENTER;
            } else {
                result = GoldPosition.LEFT;
            }

            double heading = robot.getHeading();
            double diff = robot.getSignedAngleDifference(robot.getHeading(), targetDir);
            String message = (Math.abs(diff) < SETUP_ERROR_DETECTION_TOLERANCE) ?
                    "Correct" : SETUP_ERROR_MSG;

            telemetry.addData("Setup", message);
            telemetry.addData("Heading", heading);
            telemetry.addData("Vision line", middleLine);
            telemetry.addData("Vision", result.toString());
            telemetry.update();
        }
        return result;
    }
}