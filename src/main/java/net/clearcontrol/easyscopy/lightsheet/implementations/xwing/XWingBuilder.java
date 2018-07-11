package net.clearcontrol.easyscopy.lightsheet.implementations.xwing;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackends;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.simulation.SimulationUtils;
import xwing.XWingMicroscope;

import java.util.concurrent.TimeUnit;

/**
 *
 * Todo: this class contains code duplications from XWingMain. Refactoring of both is recommended
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
class XWingBuilder implements LoggingFeature
{
  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();

  private XWingMicroscope mXWingMicroscope;
  private ClearCL mClearCL;

  public XWingBuilder(boolean pSimulated, boolean pUseStages) {
    int pNumberOfDetectionArms = 2;
    int pNumberOfLightSheets = 4;

    int lMaxStackProcessingQueueLength = 32;
    int lThreadPoolSize = 1;
    int lNumberOfControlPlanes = 8;

    mClearCL = new ClearCL(ClearCLBackends.getBestBackend());

    for (ClearCLDevice lClearCLDevice : mClearCL.getAllDevices())
      info("OpenCl devices available: %s \n",
           lClearCLDevice.getName());

    ClearCLContext
        lStackFusionContext =
        mClearCL.getDeviceByName(sMachineConfiguration.getStringProperty(
            "clearcl.device.fusion",
            "")).createContext();

    info("Using device %s for stack fusion \n",
         lStackFusionContext.getDevice());

    mXWingMicroscope =
        new XWingMicroscope(lStackFusionContext,
                            lMaxStackProcessingQueueLength,
                            lThreadPoolSize);
    if (pSimulated)
    {
      ClearCLContext
          lSimulationContext =
          mClearCL.getDeviceByName(sMachineConfiguration.getStringProperty(
              "clearcl.device.simulation",
              "HD")).createContext();

      info("Using device %s for simulation (Simbryo) \n",
           lSimulationContext.getDevice());

      LightSheetMicroscopeSimulationDevice
          lSimulatorDevice =
          SimulationUtils.getSimulatorDevice(lSimulationContext,
                                             pNumberOfDetectionArms,
                                             pNumberOfLightSheets,
                                             2048,
                                             11,
                                             320,
                                             320,
                                             320,
                                             false);

      mXWingMicroscope.addSimulatedDevices(false,
                                           false,
                                           true,
                                           lSimulatorDevice);
    }
    else
    {
      mXWingMicroscope.addRealHardwareDevices(pNumberOfDetectionArms,
                                              pNumberOfLightSheets,
                                              pUseStages);
    }
    mXWingMicroscope.addStandardDevices(lNumberOfControlPlanes);

    //EDFImagingEngine
    //    lDepthOfFocusImagingEngine =
    //    new EDFImagingEngine(lStackFusionContext, mXWingMicroscope);
    //mXWingMicroscope.addDevice(0, lDepthOfFocusImagingEngine);


    info("Opening microscope devices...");
    if (mXWingMicroscope.open())
    {
      info("Starting microscope devices...");
      if (mXWingMicroscope.start())
      {
        mXWingMicroscope.useRecycler("adaptation", 1, 4, 4);

        /*if (pPrimaryStage != null)
        {
          XWingGui lXWingGui;

          info("Setting up XWing GUI...");
          lXWingGui =
              new XWingGui(lXWingMicroscope,
                           pPrimaryStage,
                           p2DDisplay,
                           p3DDisplay);
          lXWingGui.setup();
          info("Opening XWing GUI...");
          lXWingGui.open();

          lXWingGui.waitForVisible(true, 1L, TimeUnit.MINUTES);

          lXWingGui.connectGUI();
          lXWingGui.waitForVisible(false, null, null);

          lXWingGui.disconnectGUI();
          info("Closing XWing GUI...");
          lXWingGui.close();

          info("Stopping microscope devices...");
          lXWingMicroscope.stop();
          info("Closing microscope devices...");
          lXWingMicroscope.close();
        }
        else {
          mClearCL = mClearCL;*/
        /*}*/
      }
      else
        severe("Not all microscope devices started!");
    }
    else
      severe("Not all microscope devices opened!");

    ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);


    /*
    if (pPrimaryStage != null)
    {
      System.exit(0);
    }
    return null;
*/
  }

  public XWingMicroscope getXWingMicroscope() {
    return mXWingMicroscope;
  }
}
