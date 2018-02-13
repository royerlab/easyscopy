package net.clearcontrol.easyscopy.lightsheet.implementations.bscope;

import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import xwing.BScopeMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
@EasyScope
public class SimulatedBScope extends EasyLightsheetMicroscope
{

  public static boolean sUseStages = false;

  private static SimulatedBScope sInstance = null;
  public static SimulatedBScope getInstance() {
    if (sInstance == null) {
      sInstance = new SimulatedBScope();
    }
    return sInstance;
  }

  private BScopeMicroscope mBScopeMicroscope;
  private SimulatedBScope() {
    super(new BScopeBuilder(true).getBScopeMicroscope());
    mBScopeMicroscope = (BScopeMicroscope) getLightSheetMicroscope();
  }

  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }





}
