package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;

import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.io.DenseMatrix64FReader;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.SpatialPhaseModulatorDeviceInterface;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.zernike.TransformMatrices;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.zernike.ZernikePolynomialsDenseMatrix64F;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import ij.IJ;
import ij.ImageJ;
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
    public static void main(String... args) {
        new ImageJ();


        // --------------------------------------------
        // Parameters

        int lImageWidth = 2048;
        int lImageHeight = 2048;

        int lIlluminationZStart = 200;
        int lDetectionZZStart = 200;

        int llightsheet = 0;
        double lLightsheetWidth = 0.45;
        double lLightsheetHeight = 0.0;
        double lLightsheetX = 0;
        double lLightsheetY = 0;

        int lDetectionArmIndex = 0;

        // ----------------------------------------------
        // run the BScope without camera
        BlindBScope lScope = BlindBScope.getInstance();
        //System.out.println(lScope.getDevices());
        LightSheetMicroscopeInterface lLightsheetMicroscope = lScope.getLightSheetMicroscope();
        //-----------------------------------------------------------------------------------------------------------------------
        // build a queue
        LightSheetMicroscopeQueue lQueue = lLightsheetMicroscope.requestQueue();
        //-----------------------------------------------------------------------------------------------------------------------
        // initialize queue
        lQueue.clearQueue();
        lQueue.setCenteredROI(lImageWidth, lImageHeight);

        lQueue.setI(llightsheet, false);
        lQueue.setIW(llightsheet, lLightsheetWidth);
        lQueue.setIH(llightsheet, lLightsheetHeight);
        lQueue.setIX(llightsheet, lLightsheetX);
        lQueue.setIY(llightsheet, lLightsheetY);

        lQueue.setIZ(lIlluminationZStart);
        lQueue.setDZ(lDetectionArmIndex, lDetectionZZStart);
        //lQueue.setC(lDetectionArmIndex, false);

        lQueue.addCurrentStateToQueue();
        //lQueue.addCurrentStateToQueue();

        //--------------------------------------------------------------
        // Acquiring
        lQueue.setExp(0.001);
        lQueue.setI(llightsheet, true);
        lQueue.setIW(llightsheet, lLightsheetWidth);
        lQueue.setIH(llightsheet, lLightsheetHeight);
        lQueue.setIX(llightsheet, lLightsheetX);
        lQueue.setIY(llightsheet, lLightsheetY);

        lQueue.setIZ(lIlluminationZStart);
        lQueue.setDZ(lDetectionArmIndex, lDetectionZZStart);
        //lQueue.setC(lDetectionArmIndex, true);

        lQueue.addCurrentStateToQueue();

        //-----------------------------------------------------------------
        //Closing

        lQueue.setI(llightsheet, false);
        //lQueue.setC(lDetectionArmIndex, false);

        lQueue.addCurrentStateToQueue();


        lQueue.finalizeQueue();

        //-----------------------------------------------------------------

        //Setting Filter Wheel Position
        FilterWheelDeviceInterface lfilter = lScope.getFilterWheelDevice("FLIFilterWheel");
        lfilter.setPosition(7);

        //Turning Laser on
        LaserDeviceInterface lLaser = lScope.getLaserDevice("488");
        lLaser.setTargetPowerInPercent(10);
        lLaser.setLaserOn(true);
        lLaser.setLaserPowerOn(true);
        lLaser.setLaserOn(true);
        lLaser.setLaserPowerOn(true);

        // --------------------------------------------
        // Run a virtual scope that only has a camera
        CameraOnlyScope lCamScope = CameraOnlyScope.getInstance();
        DcamJ1Camera lCamera = (DcamJ1Camera)lCamScope.getDevice(DcamJ1Camera.class, 0, "");
        lCamera.setImageHeight(2048);
        lCamera.setImageWidth(2048);
        lCamera.setExposureTimeInSeconds(0.05);


        // ----------------------------------------------
        boolean lPlayQueueAndWait = false;
        try {
            lPlayQueueAndWait = lLightsheetMicroscope.playQueueAndWaitForStacks(lQueue, 10000 + lQueue.getQueueLength(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        if (!lPlayQueueAndWait){
            System.out.println("Error while imaging");}

        // --------------------------------------------------------------------
        // convert and show
        //EasyScopyUtilities imgStack = EasyScopyUtilities.stackToImg(lStack);
        //ImageJFunctions.show(EasyScopyUtilities.stackToImg(lStack));
        //IJ.run("Enhance Contrast", "saturated=0.35");


        // --------------------------------------------
        // take a first image
        StackInterface lStack = lCamera.acquire();

        RandomAccessibleInterval<UnsignedShortType>
               img = EasyScopyUtilities.stackToImg((OffHeapPlanarStack)lStack);

        ImageJFunctions.show(img);
        IJ.run("Enhance Contrast", "saturated=0.35");

        // --------------------------------------------
        // Configure the deformable mirror
        //DenseMatrix64F lMatrix = new DenseMatrix64F(11,11);
        //File lMatrixFile = new File("C:\\Users\\myerslab\\.clearcontrol\\MirrorModes\\Neuronal_flat_deb.json");

        //DenseMatrix64FReader lMatrixReader = new DenseMatrix64FReader(lMatrixFile, lMatrix);
        //lMatrixReader.read();

        //SpatialPhaseModulatorDeviceInterface lMirror = lScope.getSpatialPhaseModulatorDevice("BIL118");
        //lMirror.getMatrixReference().set(lMatrix);

        // --------------------------------------------
        // Take another image
        //lStack = lCamera.acquire();
        //img = EasyScopyUtilities.stackToImg((OffHeapPlanarStack)lStack);

        //ImageJFunctions.show(img);
        //IJ.run("Enhance Contrast", "saturated=0.35");

        // --------------------------------------------
        // some more spatial phase modulation code snippets; to be extended

        // Get a matrix with a certain Zernike mode
        //ZernikePolynomialsDenseMatrix64F lZernikeModeMatrix20 = new ZernikePolynomialsDenseMatrix64F(11, 11, 2, 0);

        // Sum two matrices
        //DenseMatrix64F lSumMatrix = TransformMatrices.sum(lZernikeModeMatrix20, lMatrix);

        // Multiply an matrix with a scalar
        //DenseMatrix64F lMultipliedMatrix = TransformMatrices.multiply(lSumMatrix, 0.5);

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
        CameraOnlyScope.cleanup();
    }
}
