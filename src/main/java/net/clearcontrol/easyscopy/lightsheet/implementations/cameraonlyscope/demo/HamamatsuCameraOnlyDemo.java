package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.demo;

import clearcontrol.devices.cameras.CameraDeviceInterface;
import clearcontrol.devices.cameras.StackCameraQueue;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCamera;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCameraQueue;
import clearcontrol.devices.lasers.devices.sim.LaserDeviceSimulator;
import clearcontrol.devices.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import ij.IJ;
import net.clearcontrol.easyscopy.EasyScopyUtilities;
import net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.HamamatsuCameraOnlyScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.camera.device.DcamJ1Camera;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HamamatsuCameraOnlyDemo {

    public static void main(String... args) throws InterruptedException, TimeoutException, ExecutionException {

        // The HamamatsuCameraOnlyLightSheetMicroscope is an instance of EasyLightSheetMicroscope
        HamamatsuCameraOnlyScope lScope = HamamatsuCameraOnlyScope.getInstance();
        System.out.println(lScope.getDevices());

        /*
        LightSheetMicroscope lLightsheetMicroscope = lScope.getLightSheetMicroscope();

        LaserDeviceSimulator lLaser = new LaserDeviceSimulator("488", 0, 488, 100);
        lLightsheetMicroscope.addDevice(0, lLaser);

        LightSheet lLightSheet = new LightSheet("I0", 9.4, 1);
        lLightsheetMicroscope.addDevice(0, lLightSheet);

        SignalGeneratorSimulatorDevice lSignalGenerator = new SignalGeneratorSimulatorDevice();
        LightSheetSignalGeneratorDevice lLightSheetSignalGenerator = LightSheetSignalGeneratorDevice.wrap(lSignalGenerator, true);
        lLightsheetMicroscope.addDevice(0, lLightSheetSignalGenerator);

        LightSheetOpticalSwitch lLightSheetOpticalSwitch = new LightSheetOpticalSwitch("LS Switch", 1);
        lLightsheetMicroscope.addDevice(0, lLightSheetOpticalSwitch);
        */



        // initialize queue
//        LightSheetMicroscopeQueue lQueue = lScope.getQueue();
//        lQueue.clearQueue();
//        lQueue.setCenteredROI(512, 512);
//        lQueue.setExp(0.01);
//        lQueue.setC(0, false);
//        lQueue.addCurrentStateToQueue();
//        lQueue.setC(0, true);
//        lQueue.addCurrentStateToQueue();
//        lScope.getLightSheetMicroscope().playQueueAndWait(lQueue, 10000, TimeUnit.MILLISECONDS);

/*        HamStackCamera lCamera = (HamStackCamera)lScope.getCameraDevice("002147");
        HamStackCameraQueue lQueue = lCamera.requestQueue();
        lQueue.clearQueue();
        lQueue.getExposureInSecondsVariable().set(0.01);
        lQueue.getKeepPlaneVariable().set(true);
        lQueue.addCurrentStateToQueue();
        lQueue.finalizeQueue();
        lCamera.playQueue(lQueue);
        Thread.sleep(1000);
        lCamera.trigger();
        Thread.sleep(1000);
        StackInterface lStack = lCamera.getStackVariable().get();*/
                //lLightsheetMicroscope.getCameraStackVariable(0).get();

        DcamJ1Camera lDJCamera = new DcamJ1Camera();
                //lScope.getDevice(DcamJ1Camera.class, 0, "");
        StackInterface lStack = lDJCamera.acquire();

        //
        //Stack = lCamera.getStackVariable();
        System.out.println(lStack);

        // start acquisition

        RandomAccessibleInterval<UnsignedShortType> img = EasyScopyUtilities.stackToImg((OffHeapPlanarStack) lStack);

        //show the images
        ImageJFunctions.show(img);
        IJ.run("Enhance Contrast", "saturated=0.35");

        // That's always a good idea by the end!
        lScope.shutDownAllLasers();

        HamamatsuCameraOnlyScope.cleanup();
        System.exit(0);

    }
}
