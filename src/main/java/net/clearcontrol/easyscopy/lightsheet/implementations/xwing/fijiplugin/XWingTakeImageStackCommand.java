package net.clearcontrol.easyscopy.lightsheet.implementations.xwing.fijiplugin;

import clearcl.imagej.ClearCLIJ;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import ij.IJ;
import ij.ImageJ;
import ij.gui.GenericDialog;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.SimulatedXWingScope;
import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.XWingScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import xwing.XWingMicroscope;

@Plugin(type = Command.class, menuPath = "XWing>Microscope>Take image with XWing")
public class XWingTakeImageStackCommand implements Command
{
  private static int mImageWidth = 1024;
  private static int mImageHeight = 1024;

  private final static String[] cImageSizeChoices = new String[]{"128", "256", "512", "1024", "2048"};

  private final static String[] cLaserChoices = new String[]{"488", "594"};
  private static int mLaserWavelength = 488;
  private static double mLaserPowerInPercent = 20;

  private static double mIlluminationX = 0;
  private static double mIlluminationY = 0;
  private static double mIlluminationZ = 0;
  private static double mIlluminationWidth = 0.45;
  private static double mIlluminationPower = 1.0;
  private static double mIlluminationHeight = 500;
  private static double mIlluminationStepDeltaZ = 0;

  private static double mIlluminationAlpha = 0;
  private static double mIlluminationBeta = 0;

  private static boolean mUseSimulation = true;
  private static boolean mKeepConnectionOpen = true;

  private static double mDetectionZ = 0;
  private static double mDetectionStepDeltaZ = 0;
  private static int mIlluminationArmIndex = 0;
  private static int mDetectionArmIndex = 0;
  private static double mExposureTimeInSeconds = 0.01;

  private static int mNumberOfSteps = 1;
  private int sImageCount = 1;

  @Override public void run()
  {
    if (!showDialog()){
      return;
    }

    EasyLightsheetMicroscope lScope = null;

    if (mUseSimulation) {
      lScope = SimulatedXWingScope.getInstance();
    } else {
      lScope = XWingScope.getInstance();
    }
    XWingMicroscope lXWingMicroscope = (XWingMicroscope) lScope.getLightSheetMicroscope();

    try
    {

      // configure laser
      LaserDeviceInterface
          lLaser =
          (LaserDeviceInterface) lScope.getDevice("Laser", "" + mLaserWavelength);
      lLaser.setTargetPowerInPercent(mLaserPowerInPercent);
      lLaser.setLaserOn(true);
      lLaser.setLaserPowerOn(true);
      lLaser.setLaserOn(true);
      lLaser.setLaserPowerOn(true);

      // configure light sheet
      LightSheetInterface lLightSheet = lXWingMicroscope.getLightSheet(mIlluminationArmIndex);
      lLightSheet.getHeightVariable().set(mIlluminationHeight);
      lLightSheet.getWidthVariable().set(mIlluminationWidth);
      lLightSheet.getPowerVariable().set(mIlluminationPower);
      lLightSheet.getAlphaInDegreesVariable().set(mIlluminationAlpha);
      lLightSheet.getBetaInDegreesVariable().set(mIlluminationBeta);
      lLightSheet.getXVariable().set(mIlluminationX);
      lLightSheet.getYVariable().set(mIlluminationY);

      DirectImageStack lDirectImageStack = lScope.getDirectImageStack();
      lDirectImageStack.setDetectionZ(mDetectionZ);
      lDirectImageStack.setDetectionZStepDistance(mDetectionStepDeltaZ);
      lDirectImageStack.setDetectionArmIndex(mDetectionArmIndex);

      lDirectImageStack.setIlluminationZ(mIlluminationZ);
      lDirectImageStack.setIlluminationZStepDistance(
          mIlluminationStepDeltaZ);
      lDirectImageStack.setLightSheetIndex(mIlluminationArmIndex);

      lDirectImageStack.setImageWidth(mImageWidth);
      lDirectImageStack.setImageHeight(mImageHeight);

      lDirectImageStack.setNumberOfRequestedImages(mNumberOfSteps);

      lDirectImageStack.setExposureTimeInSeconds(mExposureTimeInSeconds);

      StackInterface lStack = lDirectImageStack.acquire();

      // coonvert and show
      RandomAccessibleInterval<UnsignedShortType>
          img = ClearCLIJ.getInstance().converter((OffHeapPlanarStack)lStack).getRandomAccessibleInterval();

      ImageJFunctions.wrap(img, "Acquired image stack " + sImageCount).show();
      IJ.run("Enhance Contrast", "saturated=0.35");
      sImageCount++;


      lScope.shutDownAllLasers();

    } catch (Exception e) {
      lScope.shutDownAllLasers();
      e.printStackTrace();
    }

    if (!mKeepConnectionOpen) {
      if (mUseSimulation) {
        SimulatedXWingScope.cleanup();
      } else {
        XWingScope.cleanup();
      }
    }
  }

  private boolean showDialog()
  {
    GenericDialog lGenericDialog = new GenericDialog("Take image with XWing");

    lGenericDialog.addChoice("Laser", cLaserChoices, "" + mLaserWavelength);
    lGenericDialog.addNumericField("Laser power in percent", mLaserPowerInPercent, 1);

    lGenericDialog.addNumericField("Illumination arm Z (in microns)", mIlluminationZ, 2);
    lGenericDialog.addNumericField("Detection arm Z (in microns)", mDetectionZ, 2);
    lGenericDialog.addNumericField("Exposure time (in seconds)", mExposureTimeInSeconds, 4);

    lGenericDialog.addNumericField("Illumination arm index", mIlluminationArmIndex, 0);
    lGenericDialog.addNumericField("Detection arm index", mDetectionArmIndex, 0);

    lGenericDialog.addChoice("Image width (pixels)", cImageSizeChoices, "" + mImageWidth);
    lGenericDialog.addChoice("Image height (pixels)", cImageSizeChoices, "" + mImageHeight);


    lGenericDialog.addCheckbox("Use simulated XWing", mUseSimulation);
    lGenericDialog.addCheckbox("Keep connection open", mKeepConnectionOpen);

    lGenericDialog.addMessage("Stack acquisition");
    lGenericDialog.addNumericField("Illumination arm delta Z per slice (in microns)",
                                   mIlluminationStepDeltaZ, 2);
    lGenericDialog.addNumericField("Detection arm delta Z per slice (in microns)", mDetectionStepDeltaZ, 2);
    lGenericDialog.addNumericField("Number of slices to acquire", mNumberOfSteps, 0);

    lGenericDialog.addMessage("Advanced parameters:");
    lGenericDialog.addNumericField("Light sheet width (0 thick, 0.5 thin)",
                                   mIlluminationWidth, 0);
    lGenericDialog.addNumericField("Light sheet height (0 single beam, 500 light sheet)",
                                   mIlluminationHeight, 0);
    lGenericDialog.addNumericField("Laser power (fraction)",
                                   mIlluminationPower, 0);

    lGenericDialog.addMessage(" ");

    lGenericDialog.addNumericField("mIlluminationX", mIlluminationX, 2);
    lGenericDialog.addNumericField("mIlluminationY", mIlluminationY, 2);
    lGenericDialog.addNumericField("mIlluminationAlpha (in degrees)", mIlluminationAlpha, 2);
    lGenericDialog.addNumericField("mIlluminationBeta (in degrees, not functional yet)", mIlluminationBeta, 2);

    lGenericDialog.showDialog();

    if (lGenericDialog.wasCanceled()) {
      return false;
    }

    mLaserWavelength = Integer.parseInt(lGenericDialog.getNextChoice());
    mLaserPowerInPercent = lGenericDialog.getNextNumber();

    mIlluminationZ = lGenericDialog.getNextNumber();
    mDetectionZ = lGenericDialog.getNextNumber();
    mExposureTimeInSeconds = lGenericDialog.getNextNumber();
    mIlluminationArmIndex = (int)lGenericDialog.getNextNumber();
    mDetectionArmIndex = (int)lGenericDialog.getNextNumber();

    mImageWidth = Integer.parseInt(lGenericDialog.getNextChoice());
    mImageHeight = Integer.parseInt(lGenericDialog.getNextChoice());

    mUseSimulation = lGenericDialog.getNextBoolean();
    mKeepConnectionOpen = lGenericDialog.getNextBoolean();

    mIlluminationStepDeltaZ = lGenericDialog.getNextNumber();
    mDetectionStepDeltaZ = lGenericDialog.getNextNumber();
    mNumberOfSteps = (int)lGenericDialog.getNextNumber();

    mIlluminationWidth = lGenericDialog.getNextNumber();
    mIlluminationHeight = lGenericDialog.getNextNumber();
    mIlluminationPower = lGenericDialog.getNextNumber();
    mIlluminationX = lGenericDialog.getNextNumber();
    mIlluminationY = lGenericDialog.getNextNumber();
    mIlluminationAlpha = lGenericDialog.getNextNumber();
    mIlluminationBeta = lGenericDialog.getNextNumber();

    return true;
  }

  public static void main(String... args) {
    new ImageJ();

    new XWingTakeImageStackCommand().run();

  }
}
