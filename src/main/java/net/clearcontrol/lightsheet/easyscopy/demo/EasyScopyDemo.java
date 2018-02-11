package net.clearcontrol.lightsheet.easyscopy.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
import ij.ImageJ;
import net.clearcontrol.lightsheet.easyscopy.EasyScopyUtilities;
import net.clearcontrol.lightsheet.easyscopy.implementations.xwing.XWingScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyDemo
{

  public static void main(String... args) throws InterruptedException
  {
    new ImageJ();

    // for real scope tests:
    // XWingScope.sSimulated = false;

    // The XWingScope is an instance of EasyLightSheetMicroscope
    XWingScope lScope = XWingScope.getInstance();
    Thread.sleep(1000);

    // Turn on a laser
    LaserDeviceInterface lLaser = lScope.getLaserDevice(488);
    lLaser.setTargetPowerInPercent(20);
    lLaser.setLaserOn(true);
    lLaser.setLaserPowerOn(true);

    // Take an image
    DirectImage lImage = lScope.getDirectImage();
    lImage.setImageWidth(2048);
    lImage.setImageHeight(512);
    lImage.setIlluminationZ(25);
    lImage.setDetectionZ(25);

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        img = EasyScopyUtilities.stackToImg(lImage.getImage());

    // take an imagestack
    DirectImageStack lImageStack = lScope.getDirectImageStack();
    lImageStack.setImageWidth(2048);
    lImageStack.setImageHeight(512);
    lImageStack.setIlluminationZ(25);
    lImageStack.setDetectionZ(25);
    lImageStack.setNumberOfRequestedImages(10);
    lImageStack.setDetectionZStepDistance(0);
    lImageStack.setIlluminationZStepDistance(1);

    // start acquisition
    RandomAccessibleInterval<UnsignedShortType>
        imgStack = EasyScopyUtilities.stackToImg(lImage.getImage());

    // show the images
    ImageJFunctions.show(img);
    ImageJFunctions.show(imgStack);


    // That's always a godd idea by the end!
    lScope.shutDownAllLasers();

    // bye bye
    lScope.terminate();
  }
}
