package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.slm.slms.DeformableMirrorDevice;
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

import java.io.File;

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
        // run the BScope without camera
        BlindBScope lScope = BlindBScope.getInstance();

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

        // --------------------------------------------
        // take a first image
        StackInterface lStack = lCamera.acquire();

        RandomAccessibleInterval<UnsignedShortType>
                img = EasyScopyUtilities.stackToImg((OffHeapPlanarStack)lStack);

        ImageJFunctions.show(img);
        IJ.run("Enhance Contrast", "saturated=0.35");

        // --------------------------------------------
        // Configure the deformable mirror
        DenseMatrix64F lMatrix = new DenseMatrix64F(11,11);
        File lMatrixFile = new File("C:\\Users\\myerslab\\.clearcontrol\\MirrorModes\\Neuronal_flat_deb.json");

        DenseMatrix64FReader lMatrixReader = new DenseMatrix64FReader(lMatrixFile, lMatrix);
        lMatrixReader.read();

        SpatialPhaseModulatorDeviceInterface lMirror = lScope.getSpatialPhaseModulatorDevice("BIL118");
        lMirror.getMatrixReference().set(lMatrix);

        // --------------------------------------------
        // Take another image
        lStack = lCamera.acquire();
        img = EasyScopyUtilities.stackToImg((OffHeapPlanarStack)lStack);

        ImageJFunctions.show(img);
        IJ.run("Enhance Contrast", "saturated=0.35");

        // --------------------------------------------
        // some more spatial phase modulation code snippets; to be extended

        // Get a matrix with a certain Zernike mode
        ZernikePolynomialsDenseMatrix64F lZernikeModeMatrix20 = new ZernikePolynomialsDenseMatrix64F(11, 11, 2, 0);

        // Sum two matrices
        DenseMatrix64F lSumMatrix = TransformMatrices.sum(lZernikeModeMatrix20, lMatrix);

        // Multiply an matrix with a scalar
        DenseMatrix64F lMultipliedMatrix = TransformMatrices.multiply(lSumMatrix, 0.5);

        // change a single matrix entry
        double value = lMultipliedMatrix.get(5,6);
        value = value + 0.1;
        lMultipliedMatrix.set(5, 6, value);

        // rotate a matrix
        DenseMatrix64F lRotatedMatrix = lMatrix.copy();
        TransformMatrices.rotateClockwise(lMatrix, lRotatedMatrix);


        // --------------------------------------------
        // finalize

        // That's always a good idea by the end
        lScope.shutDownAllLasers();

        // cleanup lab
        BlindBScope.cleanup();
        CameraOnlyScope.cleanup();
    }
}
