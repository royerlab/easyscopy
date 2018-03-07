package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope;

import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;

/**
 * The CameraOnlyScope allows accessing a single camera via the EasyLightsheedMicroscope API.
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
@EasyScope
public class CameraOnlyScope extends EasyLightsheetMicroscope
{


  private static CameraOnlyScope sInstance = null;
  public static CameraOnlyScope getInstance() {
    if (sInstance == null) {
      sInstance = new CameraOnlyScope();
    }
    return sInstance;
  }

  public CameraOnlyScope()
  {
    super(new CameraOnlyScopeBuilder().getLightSheetMicroscope());
  }


  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }

}
