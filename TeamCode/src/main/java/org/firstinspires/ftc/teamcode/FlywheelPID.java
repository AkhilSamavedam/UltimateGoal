package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@TeleOp(name="Flywheel PID Test", group="PiRhos")


public class FlywheelPID extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    static final double COUNTS_PER_MOTOR_REV = 537.6;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0 / 3;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 3.937;   // For figuring circumference - 100mm
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED_SLOW = 0.4;
    static final double DRIVE_SPEED = 0.7;

    @Override

    public void runOpMode() {
       initHardware();

        telemetry.addLine("Waiting for start");
        telemetry.update();



        /*
         * Wait for the user to press start on the Driver Station
         */

        waitForStart();
        runtime.reset();
        telemetry.addLine("Started");
        telemetry.update();

        double targetRPM = 132 ;
        double backRightPower = -0.1;
        while (opModeIsActive())
        {
            if (gamepad1.left_bumper){
                targetRPM -= 2;
            }
            if (gamepad1.right_bumper){
                targetRPM += 2;
            }
            //backRightPower = SetRPM(targetRPM, backRightPower);
            moveWPID(24,0);
        }
    }

    public double getRPM(){
        double waitTime = 250;
        ElapsedTime timer = new ElapsedTime ();
        double startFWCount = backRight.getCurrentPosition();
        while (timer.milliseconds() < waitTime)
        {
        }


        double deltaFW = backRight.getCurrentPosition() - startFWCount;

        double RPM = (deltaFW * 240)/537.6;

        return RPM;

    }



    public double SetRPM (double targetRPM, double motorPower){
        double kp = 0.0025;
        double ki = 0.000025;
        double kd = 0.000000025 * 0 ;
        double errorRPM = targetRPM + getRPM();
        double curPower = motorPower;
        double lastErr = 0 ;
        double integralErr = 0 ;
        ElapsedTime timer = new ElapsedTime();

        while (Math.abs(errorRPM) > 1) {
            double deltaError = errorRPM - lastErr;
            integralErr += errorRPM * timer.time();
            double derivative = deltaError/timer.time();

            timer.reset();

            double deltaPower = - ((errorRPM * kp) + (integralErr * ki) +(derivative *kd)) ;

            curPower += deltaPower ;

            if (curPower > 0.7) curPower = 0.7 ;
            if (curPower < -0.7) curPower = -0.7 ;

            backRight.setPower(curPower);
            double RPM = getRPM();
            errorRPM = targetRPM + RPM;
            telemetry.addData("RPM = ", RPM);
            telemetry.addData("errorRPM = ", errorRPM);
            telemetry.addData("curPower  = ", curPower);
            telemetry.addData("deltaPower  = ", deltaPower);
            telemetry.update();

            if (Math.abs(targetRPM) > RPM){
                return (curPower);
            }
        }
        return (curPower);
    }

    public void moveWPID (double targetInches, double setPower){
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode((DcMotor.RunMode.STOP_AND_RESET_ENCODER));
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        double targetPos = targetInches * COUNTS_PER_INCH;
        double kp = 0.0025;
        double ki = 0.0025;
        double kd = 0;
        double integral = 0;
        double errorFrontLeft = targetPos;
        double errorBackLeft = targetPos;
        double errorFrontRight = targetPos;
        double errorBackRight = targetPos;
        double lastMinError = 0;
        double curPower = setPower;
        ElapsedTime timer = new ElapsedTime();
        // start loop while any error is > some number
        // use the lowest change to not cause any slip with the wheels
        while (Math.abs(errorFrontLeft) >= 500 || Math.abs(errorFrontRight) >= 500 || Math.abs(errorBackLeft) >= 500 || Math.abs(errorBackRight) >= 500 ){
            double minError = Math.min(Math.abs(errorFrontLeft),Math.abs(errorFrontRight));
            minError = Math.min(minError,Math.abs(errorBackLeft));
            minError = Math.min(minError, Math.abs(errorBackRight));

            double deltaMinError = minError - lastMinError;

            integral += minError * timer.time();
            double derivative = deltaMinError/timer.time();

            timer.reset();

            double deltaPower = (minError * kp) + (integral * ki) + (derivative * kd);
            curPower += deltaPower;

            frontLeft.setPower(curPower);
            frontRight.setPower(curPower);
            backRight.setPower(curPower);
            backLeft.setPower(curPower);

            errorFrontLeft = targetPos - frontLeft.getCurrentPosition();
            errorBackLeft = targetPos - backLeft.getCurrentPosition();
            errorFrontRight = targetPos - frontRight.getCurrentPosition();
            errorBackRight = targetPos - backRight.getCurrentPosition();

            lastMinError = minError;




        }





    }
    protected void initHardware() {
        // Vuforia and Tensorflow related initialization
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        /*
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();
        }

         */

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
//        robot.init(hardwareMap);

        frontLeft = hardwareMap.get(DcMotor.class, "left_front");
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight = hardwareMap.get(DcMotor.class, "right_front");
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft = hardwareMap.get(DcMotor.class, "left_back");
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight = hardwareMap.get(DcMotor.class, "right_back");
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
/*
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"));
        pipeline = new StarterStackDeterminationPipeline();
        phoneCam.setPipeline(pipeline);
        webcam.setPipeline(pipeline);

        // We set the viewport policy to optimized view so the preview doesn't appear 90 deg
        // out when the RC activity is in portrait. We do our actual image processing assuming
        // landscape orientation, though.
        phoneCam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                phoneCam.startStreaming(320,240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }
        });
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320,240, OpenCvCameraRotation.UPRIGHT);
            }
        });

 */
        telemetry.addData("Status", "Initialization Done");
        telemetry.update();
    }

}