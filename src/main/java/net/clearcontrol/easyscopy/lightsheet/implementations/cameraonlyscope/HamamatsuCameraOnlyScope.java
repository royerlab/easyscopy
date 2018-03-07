package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
@EasyScope
public class HamamatsuCameraOnlyScope extends EasyLightsheetMicroscope
{


  private static HamamatsuCameraOnlyScope sInstance = null;
  public static HamamatsuCameraOnlyScope getInstance() {
    if (sInstance == null) {
      sInstance = new HamamatsuCameraOnlyScope();
    }
    return sInstance;
  }

  public HamamatsuCameraOnlyScope()
  {
    super(new HamamatsuCameraOnlyScopeBuilder().getLightSheetMicroscope());
  }


  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }

}
