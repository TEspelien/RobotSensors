package org.firstinspires.ftc.teamcode.Mechanisms;

import android.graphics.Color;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelImpl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.Hardware.MecanumHardware;
import org.firstinspires.ftc.teamcode.Utilities.Audio.SoundEffectManager;
import org.firstinspires.ftc.teamcode.Utilities.Control.LEDRiver;
import org.firstinspires.ftc.teamcode.Utilities.Mocking.DcMotorExMock;
import org.firstinspires.ftc.teamcode.Utilities.Mocking.DigitalChannelMock;
import org.firstinspires.ftc.teamcode.Utilities.Mocking.IntakeMock;
import org.firstinspires.ftc.teamcode.Utilities.Mocking.ServoMock;

public class SparkyTheRobot extends MecanumHardware {
    public LynxModule rightHub;
    public DcMotorEx leftFlipper;
    public DcMotorEx rightFlipper;
    public DcMotorEx linearSlide;
    public DcMotorEx winch;

    public ServoImplEx leftIntakeFlipper;
    public ServoImplEx rightIntakeFlipper;
    public CRServoImplEx leftIntakeRoller;
    public CRServoImplEx rightIntakeRoller;
    public Intake intake;

    public Servo markerDeployer;
    public Servo parkingMarker;
    private Servo cameraFlipper;
    public Servo blockTrapper;
    public LEDRiver ledRiver;

    // Sensors
    public DigitalChannel hangSwitch;
    public DigitalChannel slideSwitch; // ON when fully extended

    public SoundEffectManager soundEffects;

    public SparkyTheRobot(LinearOpMode oM) {
        super(oM);
        try {
            ledRiver = hwMap.get(LEDRiver.IMPL, "leds");
            ledRiver.setMode(LEDRiver.Mode.SOLID);
            ledRiver.setLEDMode(LEDRiver.LEDMode.RGB);
            ledRiver.setColorDepth(LEDRiver.ColorDepth.BIT_24);
            ledRiver.apply();

            rightHub = hwMap.get(LynxModule.class, "rightHub");

            leftFlipper = hwMap.get(DcMotorEx.class, "flipperLeft");
            rightFlipper = hwMap.get(DcMotorEx.class, "flipperRight");
            rightFlipper.setDirection(DcMotorEx.Direction.REVERSE);
            linearSlide = hwMap.get(DcMotorEx.class, "extender");
            winch = hwMap.get(DcMotorEx.class, "winch");

            leftIntakeFlipper = hwMap.get(ServoImplEx.class, "leftIntakeFlipper");
            rightIntakeFlipper = hwMap.get(ServoImplEx.class, "rightIntakeFlipper");
            leftIntakeRoller = hwMap.get(CRServoImplEx.class, "leftIntakeRoller");
            rightIntakeRoller = hwMap.get(CRServoImplEx.class, "rightIntakeRoller");
            intake = new Intake(leftIntakeFlipper, rightIntakeFlipper, leftIntakeRoller, rightIntakeRoller);

            markerDeployer = hwMap.get(ServoImplEx.class, "markerDeployer");
            cameraFlipper = hwMap.get(ServoImplEx.class, "cameraFlipper");
            parkingMarker = hwMap.get(ServoImplEx.class, "parkingMarker");
            blockTrapper = hwMap.get(ServoImplEx.class, "blockTrapper");

            hangSwitch = hwMap.get(DigitalChannelImpl.class, "hangSwitch");
            hangSwitch.setMode(DigitalChannel.Mode.INPUT);
            slideSwitch = hwMap.get(DigitalChannelImpl.class, "slideSwitch");
            slideSwitch.setMode(DigitalChannel.Mode.INPUT);

            leftFlipper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightFlipper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            winch.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            onRawChassis = false;

        } catch (IllegalArgumentException e) {
            leftFlipper = new DcMotorExMock();
            rightFlipper = new DcMotorExMock();
            linearSlide = new DcMotorExMock();
            winch = new DcMotorExMock();

            markerDeployer = new ServoMock();
            parkingMarker = new ServoMock();
            cameraFlipper = new ServoMock();
            blockTrapper = new ServoMock();
            // We don't need to mock LEDs

            intake = new IntakeMock(null, null, null, null);
            hangSwitch = new DigitalChannelMock();
            slideSwitch = new DigitalChannelMock();
            onRawChassis = true;
        }

        soundEffects = new SoundEffectManager(hwMap.appContext, SoundEffectManager.PACMAN_AUDIO);
    }

    public void calibrate(boolean calibrateGyros) {
        /*ledRiver.setMode(LEDRiver.Mode.PATTERN)
                .setPattern(LEDRiver.Pattern.BREATHING.builder())
                .setColor(Color.RED).apply();*/
        DcMotor[] motors =
                new DcMotor[] {leftFlipper, rightFlipper, winch, linearSlide,
                frontLeft, frontRight, backLeft, backRight};

        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        // Now calibrate gyros
        // Wait time here will be plenty for our motor encoders to initialize
        if (calibrateGyros) {
            BNO055IMU[] imus = new BNO055IMU[]{/*armIMU,*/ primaryIMU};
            for (BNO055IMU imu : imus) {
                imu.initialize(metricParameters);
            }
        } else {
            sleep(50);
        }
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        //ledRiver.setColor(Color.BLUE).apply();
    }
}
