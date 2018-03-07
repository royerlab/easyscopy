package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.backend.ClearCLBackends;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import xwing.BScopeMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
class CameraOnlyScopeBuilder implements LoggingFeature
{
  private LightSheetMicroscope mLightSheetMicroscope;
  private ClearCL mClearCL;

  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();

  public CameraOnlyScopeBuilder() {
    int lMaxStackProcessingQueueLength = 32;
    int lThreadPoolSize = 1;

    mClearCL =
        new ClearCL(ClearCLBackends.getBestBackend());



    ClearCLContext lStackFusionContext = mClearCL
        .getDeviceByName(sMachineConfiguration.getStringProperty("clearcl.device.fusion",
                                                                 ""))
        .createContext();

    info("Using device %s for stack fusion \n",
         lStackFusionContext.getDevice());



    mLightSheetMicroscope =
        new CameraOnlyLightSheetMicroscope(lStackFusionContext,
                             lMaxStackProcessingQueueLength,
                             lThreadPoolSize);

  }

  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }
}
