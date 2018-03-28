package net.clearcontrol.easyscopy.lightsheet.implementations.xwing.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectFusedImageStack;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import clearcontrol.microscope.state.AcquisitionType;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import ij.ImageJ;
import net.clearcontrol.easyscopy.EasyScopyUtilities;
import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.SimulatedXWingScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.XWingScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
public class SimulatedXWingFusionDemo
{
  public static void main(String... args){
    new ImageJ();

    // workaround to prevend initialisation of stages:
    XWingScope.sUseStages = false;

    XWingScope lScope = XWingScope.getInstance();

    LaserDeviceInterface lLaser = lScope.getLaserDevice("488");
    lLaser.setTargetPowerInPercent(20);
    lLaser.setLaserPowerOn(true);
    lLaser.setLaserOn(true);

    DirectFusedImageStack lFusedStack = lScope.getDirectFusedImageStack();
    lFusedStack.setAcquisitionType(AcquisitionType.TimeLapseOpticallyCameraFused);
    lFusedStack.setImageWidth(1024);
    lFusedStack.setImageHeight(2048);
    lFusedStack.setMinZ(0);
    lFusedStack.setMaxZ(270);
    lFusedStack.setExposureTimeInSeconds(0.01);
    lFusedStack.setSliceDistance(2);

    StackInterface lStack = lFusedStack.acquire();

    RandomAccessibleInterval<UnsignedShortType> img =
        EasyScopyUtilities.stackToImg((OffHeapPlanarStack) lStack);

    ImageJFunctions.show(img);
    
    lScope.shutDownAllLasers();

    XWingScope.cleanup();
  }
}
