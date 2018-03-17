package net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackends;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
class AcademicScopeBuilder implements LoggingFeature
{
  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();

  LightSheetMicroscope mLightSheetMicroscope;

  ClearCL mClearCL;

  public AcademicScopeBuilder() {

    int lMaxStackProcessingQueueLength = 32;
    int lThreadPoolSize = 1;

    mClearCL =
        new ClearCL(ClearCLBackends.getBestBackend());

    for (ClearCLDevice lClearCLDevice : mClearCL.getAllDevices())
      info("OpenCl devices available: %s \n",
           lClearCLDevice.getName());

    System.out.println("dev: " + sMachineConfiguration.getStringProperty("clearcl.device.fusion",
            ""));

    ClearCLContext lStackFusionContext = mClearCL
        .getDeviceByName("CPU")
        .createContext();

    info("Using device %s for stack fusion \n",
         lStackFusionContext.getDevice());


    ClearCLContext lSimulationContext = mClearCL
        .getDeviceByName("CPU")
        .createContext();


    mLightSheetMicroscope = new AcademicLightScheetMicroscope("Academic scope", lStackFusionContext, lSimulationContext, lMaxStackProcessingQueueLength, lThreadPoolSize);
  }

  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }
}
