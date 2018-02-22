package net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.stack.OffHeapPlanarStack;
import ij.IJ;
import ij.ImageJ;
import net.clearcontrol.easyscopy.EasyScopyUtilities;
import net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation.AcademicScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * In the academic scope demo you learn how to assemle a microscope by
 * adding devices to an empty scope. The order how devices are added is
 * decisive
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class AcademicScopeDemo
{
  public static void main(String... args) {
    new ImageJ();

    AcademicScope
        lAcademicScope = AcademicScope.getInstance();

    lAcademicScope.addLaser(488);
    lAcademicScope.addLaser(592);

    lAcademicScope.addCamera(2048, 2048);
    lAcademicScope.addLightSheet();
    lAcademicScope.addLightSheet();
    lAcademicScope.addLightSheet();
    lAcademicScope.addScalingAmplifier();
    lAcademicScope.addOpticalSwitch();
    lAcademicScope.addSignalGenerator();
    lAcademicScope.mountDrosophilaSample(11);

    lAcademicScope.turnOn();


    System.out.println("The academic scope consists of these devices:");
    for (Object lDevice : lAcademicScope.getDevices())
    {
      System.out.println(" * " + lDevice.toString());
    }


    // Turn on a laser
    LaserDeviceInterface
        lLaser = (LaserDeviceInterface) lAcademicScope.getDevice("Laser", "488");
    lLaser.setTargetPowerInPercent(10);
    lLaser.setLaserOn(true);
    lLaser.setLaserPowerOn(true);


    //lScope.getLightSheetMicroscope().getLightSheet(0).getHeightVariable().set(0);

    for (int l = 0; l < lAcademicScope.getLightSheetMicroscope().getNumberOfLightSheets(); l++)
    {
      lLaser.setTargetPowerInPercent(10);
      lLaser.setLaserOn(true);
      lLaser.setLaserPowerOn(true);
      lLaser.setTargetPowerInPercent(10);
      lLaser.setLaserOn(true);
      lLaser.setLaserPowerOn(true);

      // Take an image
      DirectImage lImage = (DirectImage) lAcademicScope.getDirectImage();
      lImage.setImageWidth(1024);
      lImage.setImageHeight(2048);
      lImage.setIlluminationZ(25);
      lImage.setDetectionZ(25);
      lImage.setLightSheetIndex(l);
      //    lImage.setLightSheetIndex();

      OffHeapPlanarStack lStack = lImage.acquire();

      // start acquisition
      RandomAccessibleInterval<UnsignedShortType>
          img =
          EasyScopyUtilities.stackToImg(lStack);

      // show the image
      ImageJFunctions.show(img);
      IJ.run("Enhance Contrast", "saturated=0.35");
    }
  }
}
