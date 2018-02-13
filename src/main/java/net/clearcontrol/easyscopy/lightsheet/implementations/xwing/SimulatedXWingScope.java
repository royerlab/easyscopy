package net.clearcontrol.easyscopy.lightsheet.implementations.xwing;

import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import xwing.XWingMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */

@EasyScope
public class SimulatedXWingScope extends EasyLightsheetMicroscope
{

  public static boolean sUseStages = false;

  private static SimulatedXWingScope sInstance = null;
  public static SimulatedXWingScope getInstance() {
    if (sInstance == null) {
      sInstance = new SimulatedXWingScope();
    }
    return sInstance;
  }

  private XWingMicroscope mXWingMicroscope;
  private SimulatedXWingScope() {
    super(new XWingBuilder(true, sUseStages).getXWingMicroscope());
    mXWingMicroscope = (XWingMicroscope) getLightSheetMicroscope();
  }

  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }

}
