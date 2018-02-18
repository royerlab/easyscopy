package net.clearcontrol.easyscopy.demo;

import clearcl.ClearCLImage;
import clearcl.imagej.ClearCLIJ;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
import clearcontrol.stack.OffHeapPlanarStack;
import fastfuse.tasks.GaussianBlurTask;
import ij.IJ;
import ij.ImageJ;
import net.clearcontrol.easyscopy.EasyScopy;
import net.clearcontrol.easyscopy.EasyScopyUtilities;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import net.clearcontrol.easyscopy.lightsheet.implementations.bscope.SimulatedBScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.SimulatedXWingScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyDemo
{

  public static void main(String... args) throws
                                          InterruptedException,
                                          IOException
  {
    new ImageJ();

    IJ.log("Supported microscopes:");
    for (Class lMicroscopeClass : EasyScopy.listEasyScopes())
    {
      IJ.log(" * " + lMicroscopeClass.toString());
    }

    // CLIJ helps converting image types and working with OpenCL/GPU
    ClearCLIJ lCLIJ = ClearCLIJ.getInstance();

    // The scope is an instance of EasyLightsheetMicroscope
    //EasyLightsheetMicroscope lScope = SimulatedBScope.getInstance();
    EasyLightsheetMicroscope lScope = SimulatedXWingScope.getInstance();

    ArrayList<Object> lDeviceList = lScope.getDevices();
    IJ.log(lScope.toString() + " devices:");
    for (Object lDevice : lDeviceList)
    {
      IJ.log(" * " + lDevice.toString());
    }

    // Turn on a laser
    LaserDeviceInterface lLaser = (LaserDeviceInterface) lScope.getDevice("Laser", "488");
    lLaser.setTargetPowerInPercent(10);
    lLaser.setLaserOn(true);
    lLaser.setLaserPowerOn(true);


    //lScope.getLightSheetMicroscope().getLightSheet(0).getHeightVariable().set(0);

    // Take an image
    DirectImage lImage = (DirectImage)lScope.getDirectImage();
    lImage.setImageWidth(1024);
    lImage.setImageHeight(2048);
    lImage.setIlluminationZ(25);
    lImage.setDetectionZ(25);
//    lImage.setLightSheetIndex();

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        img = EasyScopyUtilities.stackToImg(lImage.acquire());

    // show the image
    ImageJFunctions.show(img);
    IJ.run("Enhance Contrast", "saturated=0.35");


    // take an imagestack
    DirectImageStack lImageStack = lScope.getDirectImageStack();
    lImageStack.setImageWidth(1024);
    lImageStack.setImageHeight(2048);
    lImageStack.setIlluminationZ(25);
    lImageStack.setDetectionZ(25);
    lImageStack.setNumberOfRequestedImages(100);
    lImageStack.setDetectionZStepDistance(1);
    lImageStack.setIlluminationZStepDistance(1);

    OffHeapPlanarStack lAcquiredStack = lImageStack.acquire();

    // Convert to GPU and do postprocessing
    ClearCLImage lInputImage = lCLIJ.converter(lAcquiredStack).getClearCLImage();
    ClearCLImage lOutputImage = lCLIJ.converter(lAcquiredStack).getClearCLImage();

    Map<String, Object> lParameters = new HashMap<String, Object>();
    lParameters.put("src", lInputImage);
    lParameters.put("dst", lOutputImage);
    lParameters.put("Nx", 5);
    lParameters.put("Ny", 5);
    lParameters.put("Nz", 5);
    lParameters.put("sx", 4.0f);
    lParameters.put("sy", 4.0f);
    lParameters.put("sz", 4.0f);

    lCLIJ.execute(GaussianBlurTask.class, "kernels/blur.cl", "gaussian_blur_image3d", lParameters);

    // start acquisition
    RandomAccessibleInterval
        imgStack = lCLIJ.converter(lOutputImage).getRandomAccessibleInterval();

    // show the images
    ImageJFunctions.show(imgStack);
    IJ.run("Enhance Contrast", "saturated=0.35");


    // That's always a good idea by the end!
    lScope.shutDownAllLasers();

    // bye bye
    SimulatedXWingScope.cleanup();
  }
}
