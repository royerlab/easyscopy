package net.clearcontrol.lightsheet.easyscopy.implementations.xwing;

import net.clearcontrol.lightsheet.easyscopy.EasyLightsheetMicroscope;
import xwing.XWingMicroscope;
import xwing.main.XWingMain;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class XWingScope extends EasyLightsheetMicroscope
{
  public static boolean sUseStages = false;

  private static XWingScope sInstance = null;
  public static XWingScope getInstance() {
    if (sInstance == null) {
      sInstance = new XWingScope();
    }
    return sInstance;
  }

  private XWingMicroscope mXWingMicroscope;
  private XWingScope() {
    super(new XWingBuilder(false, sUseStages).getXWingMicroscope());
    mXWingMicroscope = (XWingMicroscope) getLightSheetMicroscope();
  }

  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }
}
