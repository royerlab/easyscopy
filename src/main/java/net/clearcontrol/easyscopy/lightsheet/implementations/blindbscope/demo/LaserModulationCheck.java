package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import ij.ImageJ;
import net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.BlindBScope;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class LaserModulationCheck {
    public static void main(String... args) throws InterruptedException, ExecutionException, TimeoutException {
        new ImageJ();

        BlindBScope lScope = BlindBScope.getInstance();
        LaserDeviceInterface lLaser = lScope.getLaserDevice("488");
        lLaser.setTargetPowerInPercent(2.0);
        //lLaser.setLaserOn(true);
        lLaser.setLaserPowerOn(true);
        lLaser.setLaserOn(true);;
        TimeUnit.MILLISECONDS.sleep(5000);
        lLaser.setLaserOn(false);
        //lLaser.setLaserPowerOn(false);
        System.out.println("Current Power "+lLaser.getCurrentPowerInMilliWatt()+" mW");
        System.out.println("Current Power "+lLaser.getCurrentPowerInPercent()+" %");

        //lScope.shutDownAllLasers();
        //BlindBScope.cleanup();
    }
}
