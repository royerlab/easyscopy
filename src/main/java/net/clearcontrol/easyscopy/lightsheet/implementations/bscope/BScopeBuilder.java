package net.clearcontrol.easyscopy.lightsheet.implementations.bscope;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackends;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.simulation.SimulationUtils;
import javafx.stage.Stage;
import xwing.BScopeMicroscope;

import java.util.concurrent.TimeUnit;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
class BScopeBuilder implements LoggingFeature
{

  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();

  private BScopeMicroscope mBScopeMicroscope;
  private ClearCL mClearCL;

  public BScopeBuilder(boolean pSimulation) {
    int pNumberOfDetectionArms = 1;
    int pNumberOfLightSheets = 1;

    Stage pPrimaryStage = null;
    boolean p2DDisplay = false;
    boolean p3DDisplay = false;

    int lMaxStackProcessingQueueLength = 32;
    int lThreadPoolSize = 1;
    int lNumberOfControlPlanes = 7;

    mClearCL =
            new ClearCL(ClearCLBackends.getBestBackend());

      for (ClearCLDevice lClearCLDevice : mClearCL.getAllDevices())
        info("OpenCl devices available: %s \n",
             lClearCLDevice.getName());

      ClearCLContext lStackFusionContext = mClearCL
          .getDeviceByName(sMachineConfiguration.getStringProperty("clearcl.device.fusion",
                                                                   ""))
          .createContext();

      info("Using device %s for stack fusion \n",
           lStackFusionContext.getDevice());

      mBScopeMicroscope =
          new BScopeMicroscope(lStackFusionContext,
                               lMaxStackProcessingQueueLength,
                               lThreadPoolSize);

      if (pSimulation)
      {
        ClearCLContext lSimulationContext = mClearCL
            .getDeviceByName(sMachineConfiguration.getStringProperty("clearcl.device.simulation",
                                                                     "HD"))
            .createContext();

        info("Using device %s for simulation (Simbryo) \n",
             lSimulationContext.getDevice());

        LightSheetMicroscopeSimulationDevice
            lSimulatorDevice =
            SimulationUtils
                .getSimulatorDevice(lSimulationContext,
                                    pNumberOfDetectionArms,
                                    pNumberOfLightSheets,
                                    2048,
                                    11,
                                    320,
                                    320,
                                    320,
                                    false);

        mBScopeMicroscope.addSimulatedDevices(false,
                                              false,
                                              true,
                                              lSimulatorDevice);
      }
      else
      {
        mBScopeMicroscope.addRealHardwareDevices(pNumberOfDetectionArms,
                                                 pNumberOfLightSheets);
      }
      mBScopeMicroscope.addStandardDevices(lNumberOfControlPlanes);

      info("Opening microscope devices...");
      if (mBScopeMicroscope.open())
      {
        info("Starting microscope devices...");
        if (mBScopeMicroscope.start())
        {
          mBScopeMicroscope.useRecycler("adaptation", 1, 4, 4);

/*
          info("Setting up BScope GUI...");
          BScopeGui lBScopeGui = new BScopeGui(lBScopeMicroscope,
                                               pPrimaryStage,
                                               p2DDisplay,
                                               p3DDisplay);
          lBScopeGui.setup();
          info("Opening BScope GUI...");
          lBScopeGui.open();

          lBScopeGui.waitForVisible(true, 1L, TimeUnit.MINUTES);

          lBScopeGui.connectGUI();
          lBScopeGui.waitForVisible(false, null, null);

          lBScopeGui.disconnectGUI();
          info("Closing BScope GUI...");
          lBScopeGui.close();

          info("Stopping microscope devices...");
          */

          /*
          lBScopeMicroscope.stop();
          info("Closing microscope devices...");
          lBScopeMicroscope.close();
          */
        }
        else
          severe("Not all microscope devices started!");
      }
      else
        severe("Not all microscope devices opened!");

      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);


//    System.exit(0);
  }


  public BScopeMicroscope getBScopeMicroscope() {
    return mBScopeMicroscope;
  }

}
