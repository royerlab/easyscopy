package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.slm.slms.DeformableMirrorDevice;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.io.DenseMatrix64FReader;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.SpatialPhaseModulatorDeviceInterface;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.zernike.TransformMatrices;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.zernike.ZernikePolynomialsDenseMatrix64F;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import net.clearcontrol.easyscopy.EasyScopyUtilities;
import net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.BlindBScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.CameraOnlyScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.camera.device.DcamJ1Camera;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.ejml.data.DenseMatrix64F;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import java.io.File;
import java.util.concurrent.TimeoutException;

/**
 * This demo shows how to initialize two scopes: A BScope without camera and another
 * scope only controlling one camera. It furthermore allows to change the mirror mode of the deformable mirror
 *
 *
 *
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
public class BlindBScopeDemo {
    public static void main(String... args) throws InterruptedException, ExecutionException, TimeoutException {
        new ImageJ();


        // --------------------------------------------
        // Parameters

        int lImageWidth = 2048;
        int lImageHeight = 2048;

        int lIlluminationZStart = 50;
        int lDetectionZZStart = 50;

        int lDetectionArmIndex = 0;

        double lLightSheetW = 0.45;
        double lLightSheetH = 100.0;
        double lLightSheetX = 0;
        double lLightSheetY = 50;
        double lLightSheetZ = 50;

        int lNumberOfStates = 2;
        double lNumberOfImagesToTake = 2;

        double lExposureTimeInSeconds = 1;
        // ----------------------------------------------
        // run the BScope without camera
        BlindBScope lScope = BlindBScope.getInstance();
        LightSheetMicroscope lLightSheetMicroscope = lScope.getLightSheetMicroscope();


        System.out.println("I AM HERE: "+lScope.getDevices());
        //---------------------------------------------------
        //Setting Filter Wheel Position
        FilterWheelDeviceInterface lfilter = lScope.getFilterWheelDevice("FLIFilterWheel");
        lfilter.setPosition(7);

        //----------------------------------------------------------------
        //Turning Laser on
        LaserDeviceInterface lLaser = lScope.getLaserDevice("488");
        lLaser.setTargetPowerInPercent(2.0);
        lLaser.setLaserOn(true);
        lLaser.setLaserPowerOn(true);
        lLaser.setLaserOn(true);
        lLaser.setLaserPowerOn(true);


        //CameraOnlyScope lCamScope = CameraOnlyScope.getInstance();
        //DcamJ1Camera lCamera = (DcamJ1Camera) lCamScope.getDevice(DcamJ1Camera.class, 0, "");


        while (true) {
            // ask for user config
            GenericDialogPlus lDialog = new GenericDialogPlus("Lightsheet config");
            lDialog.addNumericField("X", lLightSheetX, 2);
            lDialog.addNumericField("Y", lLightSheetY, 2);
            lDialog.addNumericField("Z", lLightSheetZ, 2);
            lDialog.addNumericField("H", lLightSheetH, 2);
            lDialog.addNumericField("Number of images", lNumberOfStates, 2);
            lDialog.addNumericField("Exposure time", lExposureTimeInSeconds, 2);
            lDialog.showDialog();
            if (lDialog.wasCanceled()) {
                break;
            }
            lLightSheetX = lDialog.getNextNumber();
            lLightSheetY = lDialog.getNextNumber();
            lLightSheetZ = lDialog.getNextNumber();
            lLightSheetH = lDialog.getNextNumber();
            lNumberOfImagesToTake = lDialog.getNextNumber();
            lExposureTimeInSeconds = lDialog.getNextNumber();


            // set the microscope to the requested state

            LightSheetMicroscopeQueue lQueue = lLightSheetMicroscope.requestQueue();
            lQueue.clearQueue();
            lQueue.setI(0, true);
            lQueue.setIX(0, lLightSheetX);
            lQueue.setIY(0, lLightSheetY);
            lQueue.setIZ(0, lLightSheetZ);
            lQueue.setIH(0, lLightSheetH);
            lQueue.setIW(0, lLightSheetW);
            lQueue.setExp(lExposureTimeInSeconds);

            for(int n=1;n<=lNumberOfStates;n++)
            {
                lQueue.addCurrentStateToQueue();
            }

            lQueue.finalizeQueue();

            // execute state!
            /*boolean lplayingQueue = lScope.getLightSheetMicroscope().playQueueAndWait(lQueue,100000,TimeUnit.SECONDS);
            for (int imageNo = 0; imageNo < lNumberOfImagesToTake; imageNo++) {
                // take an image
                lCamera.setImageHeight(lImageHeight);
                lCamera.setImageWidth(lImageWidth);
                lCamera.setExposureTimeInSeconds(lExposureTimeInSeconds);
                StackInterface lStack = lCamera.acquire();

                WindowManager.closeAllWindows();

                // convert and show
                ImageJFunctions.show(EasyScopyUtilities.stackToImg((OffHeapPlanarStack) lStack));
                IJ.run("Enhance Contrast", "saturated=0.35");
            }*/
        }
        //lCamera.close();


        // --------------------------------------------
        // Configure the deformable mirror
        /*DenseMatrix64F lMatrix = new DenseMatrix64F(11,11);
        File lMatrixFile = new File("C:\\Users\\myerslab\\.clearcontrol\\MirrorModes\\Neuronal_flat_deb.json");

        DenseMatrix64FReader lMatrixReader = new DenseMatrix64FReader(lMatrixFile, lMatrix);
        lMatrixReader.read();

        SpatialPhaseModulatorDeviceInterface lMirror = lScope.getSpatialPhaseModulatorDevice("BIL118");
        lMirror.getMatrixReference().set(lMatrix);*/

        // --------------------------------------------
        // some more spatial phase modulation code snippets; to be extended

        // Get a matrix with a certain Zernike mode
        //ZernikePolynomialsDenseMatrix64F lZernikeModeMatrix20 = new ZernikePolynomialsDenseMatrix64F(11, 11, 2, 0);

        // Sum two matrices
        //DenseMatrix64F lSumMatrix = TransformMatrices.sum(lZernikeModeMatrix20, lMatrix);

        // Multiply an matrix with a scalar
        //DenseMatrix64F lMultipliedMatrix = TransformMatrices.multiply(lSumMa trix, 0.5);

        // change a single matrix entry
        //double value = lMultipliedMatrix.get(5,6);
        //value = value + 0.1;
        //lMultipliedMatrix.set(5, 6, value);

        // rotate a matrix
        //DenseMatrix64F lRotatedMatrix = lMatrix.copy();
        //TransformMatrices.rotateClockwise(lMatrix, lRotatedMatrix);


        // --------------------------------------------
        // finalize

        // That's always a good idea by the end
        lScope.shutDownAllLasers();

        // cleanup lab
        BlindBScope.cleanup();
        //CameraOnlyScope.cleanup();
    }
}
