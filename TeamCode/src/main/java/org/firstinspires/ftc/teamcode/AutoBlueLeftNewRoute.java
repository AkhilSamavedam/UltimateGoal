package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Auto Blue Left New Route", group="PiRhos")
public class AutoBlueLeftNewRoute extends UltimateGoalAutonomousBaseOpenCV{

    @Override
    public void runOpMode() {

        // Initialize hardware
        initHardware(true);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        double shooterPwr = -0.49 ;

        // Find number of rings + print for drivers
        OpenCVTestPipeline.RingPosition objectFound = OpenCVRecognizeStack(1000 );
        // sleep(30000);
        telemetry.addData("Object Found: ", objectFound);
        telemetry.update();

        // Set flywheel power at beginning to speed up set time
        flywheelShooter.setPower(shooterPwr);

        // Move fwd and left to shoot 3 shots
        moveWPID(0,-54);
        moveWPID(-6,0);

        shooterPwr = SetRPM(128, shooterPwr);
        telemetry.addData("Trigger", 1);
        telemetry.update();
        sleep(2500);

        shooterPwr = SetRPM(128, shooterPwr);
        telemetry.addData("Trigger", 2)  ;
        telemetry.update() ;
        sleep(2500);

        shooterPwr = SetRPM(128, shooterPwr);
        telemetry.addData("Trigger", 3)  ;
        telemetry.update() ;
        sleep(2500);

        // Drop off the wobble goal to specific box + align to goal
        if ( objectFound.equals(OpenCVTestPipeline.RingPosition.NONE) ){
            moveWPID(6, -6);
            sleep(500);
            moveWPID(-20, -12);
        }
        else if ( objectFound.equals(OpenCVTestPipeline.RingPosition.ONE) ){
            moveWPID(-12, -84);
            sleep(500);
            moveWPID(-2, 8);
        }
        else if ( objectFound.equals(OpenCVTestPipeline.RingPosition.FOUR)){
            moveWPID(12, -108);
            sleep(500);
            moveWPID(-26, 32);
        }

    }
}
