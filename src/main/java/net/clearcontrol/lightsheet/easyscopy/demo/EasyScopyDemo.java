package net.clearcontrol.lightsheet.easyscopy.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
import ij.IJ;
import ij.ImageJ;
import net.clearcontrol.lightsheet.easyscopy.EasyLightsheetMicroscope;
import net.clearcontrol.lightsheet.easyscopy.EasyScopyUtilities;
import net.clearcontrol.lightsheet.easyscopy.implementations.bscope.SimulatedBScope;
import net.clearcontrol.lightsheet.easyscopy.implementations.xwing.SimulatedXWingScope;
import net.clearcontrol.lightsheet.easyscopy.implementations.xwing.XWingScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import java.util.ArrayList;
import java.util.logging.SimpleFormatter;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyDemo
{

  public static void main(String... args) throws InterruptedException
  {
    new ImageJ();

    // The scope is an instance of EasyLightsheetMicroscope
    //EasyLightsheetMicroscope lScope = SimulatedXWingScope.getInstance();
    EasyLightsheetMicroscope lScope = SimulatedBScope.getInstance();

    ArrayList<Object> lDeviceList = lScope.getDevices();
    for (Object lDevice : lDeviceList)
    {
      IJ.log(lDevice.toString());
    }

    // Turn on a laser
    LaserDeviceInterface lLaser = (LaserDeviceInterface) lScope.getDevice("Laser", "488");
    lLaser.setTargetPowerInPercent(20);
    lLaser.setLaserOn(true);
    lLaser.setLaserPowerOn(true);


    //lScope.getLightSheetMicroscope().getLightSheet(0).getHeightVariable().set(0);

    // Take an image
    DirectImage lImage = (DirectImage)lScope.getDirectImage();
    lImage.setImageWidth(2048);
    lImage.setImageHeight(512);
    lImage.setIlluminationZ(25);
    lImage.setDetectionZ(25);
    lImage.setExposureTimeInSeconds(0.1);
//    lImage.setLightSheetIndex();

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        img = EasyScopyUtilities.stackToImg(lImage.acquire());

    // show the image
    ImageJFunctions.show(img);


    // take an imagestack
    DirectImageStack lImageStack = lScope.getDirectImageStack();
    lImageStack.setImageWidth(2048);
    lImageStack.setImageHeight(512);
    lImageStack.setIlluminationZ(25);
    lImageStack.setDetectionZ(25);
    lImageStack.setNumberOfRequestedImages(10);
    lImageStack.setDetectionZStepDistance(0);
    lImageStack.setIlluminationZStepDistance(0.1);

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        imgStack = EasyScopyUtilities.stackToImg(lImageStack.acquire());

    // show the images
    ImageJFunctions.show(imgStack);


    // That's always a godd idea by the end!
    lScope.shutDownAllLasers();

    // bye bye
    SimulatedXWingScope.cleanup();
  }
}
