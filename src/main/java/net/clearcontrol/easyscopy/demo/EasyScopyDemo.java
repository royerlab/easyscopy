package net.clearcontrol.easyscopy.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
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

import java.util.ArrayList;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyDemo
{

  public static void main(String... args) throws InterruptedException
  {
    new ImageJ();

    IJ.log("Supported microscopes:");
    for (Class lMicroscopeClass : EasyScopy.listEasyScopes())
    {
      IJ.log(" * " + lMicroscopeClass.toString());
    }

    // The scope is an instance of EasyLightsheetMicroscope
    EasyLightsheetMicroscope lScope = SimulatedBScope.getInstance();
    //EasyLightsheetMicroscope lScope = SimulatedXWingScope.getInstance();

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

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        imgStack = EasyScopyUtilities.stackToImg(lImageStack.acquire());

    // show the images
    ImageJFunctions.show(imgStack);
    IJ.run("Enhance Contrast", "saturated=0.35");


    // That's always a godd idea by the end!
    lScope.shutDownAllLasers();

    // bye bye
    SimulatedXWingScope.cleanup();
  }
}
