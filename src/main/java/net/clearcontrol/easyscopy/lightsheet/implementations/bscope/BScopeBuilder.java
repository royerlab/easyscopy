package net.clearcontrol.easyscopy.lightsheet.implementations.bscope;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackends;
import clearcontrol.anything.AnythingDevice;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCamera;
import clearcontrol.devices.lasers.devices.cobolt.CoboltLaserDevice;
import clearcontrol.devices.lasers.devices.omicron.OmicronLaserDevice;
import clearcontrol.devices.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;
import clearcontrol.devices.signalgen.devices.nirio.NIRIOSignalGenerator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.simulation.SimulationUtils;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.scheduler.MirrorModeScheduler;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.SpatialPhaseModulatorDeviceBase;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.devices.alpao.AlpaoDMDevice;
import javafx.stage.Stage;
import net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.BlindCamera;
import xwing.BScopeMicroscope;

import java.util.concurrent.TimeUnit;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class BScopeBuilder implements LoggingFeature
{

  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();

  private BScopeMicroscope mBScopeMicroscope;
  private ClearCL mClearCL;


  public BScopeBuilder(boolean pSimulation) {
    this(pSimulation, false);
  }

  public BScopeBuilder(boolean pSimulation, boolean bBlind) {
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
        /*if (bBlind) {
          //mBScopeMicroscope.addRealHardwareDevices(0,
                  pNumberOfLightSheets);
        } else {
        }*/
          addRealHardwareDevices(pNumberOfDetectionArms,
                  pNumberOfLightSheets, bBlind);

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


  public void addRealHardwareDevices(int pNumberOfDetectionArms, int pNumberOfLightSheets, boolean pBlind) {
    long lDefaultStackWidth = 1024L;
    long lDefaultStackHeight = 2048L;
    OmicronLaserDevice lLaserDevice405 = new OmicronLaserDevice(1);
    mBScopeMicroscope.addDevice(0, lLaserDevice405);
    OmicronLaserDevice lLaserDevice488 = new OmicronLaserDevice(2);
    mBScopeMicroscope.addDevice(0, lLaserDevice488);
    OmicronLaserDevice lLaserDevice515 = new OmicronLaserDevice(3);
    mBScopeMicroscope.addDevice(0, lLaserDevice515);
    CoboltLaserDevice lLaserDevice561 = new CoboltLaserDevice("Jive", 100, 4);
    mBScopeMicroscope.addDevice(1, lLaserDevice561);
    CoboltLaserDevice lLaserDevice594 = new CoboltLaserDevice("Mambo", 100, 5);
    mBScopeMicroscope.addDevice(1, lLaserDevice594);

    for(int c = 0; c < pNumberOfDetectionArms; ++c) {
      if (pBlind) {
        mBScopeMicroscope.addDevice(c, new BlindCamera());
      } else {
        StackCameraDeviceInterface<?> lCamera = HamStackCamera.buildWithExternalTriggering(c);
        lCamera.getStackWidthVariable().set(lDefaultStackWidth);
        lCamera.getStackHeightVariable().set(lDefaultStackHeight);
        lCamera.getExposureInSecondsVariable().set(0.01D);
        mBScopeMicroscope.addDevice(c, lCamera);
      }
    }

    NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();
    LightSheetSignalGeneratorDevice lLSSignalGenerator = LightSheetSignalGeneratorDevice.wrap(lNIRIOSignalGenerator, true);
    mBScopeMicroscope.addDevice(0, lLSSignalGenerator);

    int l;
    for(l = 0; l < pNumberOfDetectionArms; ++l) {
      DetectionArm lDetectionArm = new DetectionArm("D" + l);
      lDetectionArm.getPixelSizeInMicrometerVariable().set(0.26D);
      mBScopeMicroscope.addDevice(l, lDetectionArm);
    }

    for(l = 0; l < pNumberOfLightSheets; ++l) {
      LightSheet lLightSheet = new LightSheet("I" + l, 9.4D, mBScopeMicroscope.getNumberOfLaserLines());
      mBScopeMicroscope.addDevice(l, lLightSheet);
    }

    for(l = 0; l < pNumberOfLightSheets; ++l) {
      for(int c = 0; c < pNumberOfDetectionArms; ++c) {
        StackCameraDeviceInterface<?> lCamera = (StackCameraDeviceInterface)mBScopeMicroscope.getDevice(StackCameraDeviceInterface.class, c);
        LightSheet lLightSheet = (LightSheet)mBScopeMicroscope.getDevice(LightSheet.class, l);
        lCamera.getExposureInSecondsVariable().sendUpdatesTo(lLightSheet.getEffectiveExposureInSecondsVariable());
        lCamera.getStackHeightVariable().sendUpdatesTo(lLightSheet.getImageHeightVariable());
      }
    }

    LightSheetOpticalSwitch lLightSheetOpticalSwitch = new LightSheetOpticalSwitch("OpticalSwitch", pNumberOfLightSheets);
    mBScopeMicroscope.addDevice(0, lLightSheetOpticalSwitch);
    FLIFilterWheelDevice lFLIFilterWheelDevice = new FLIFilterWheelDevice(1);
    //mBScopeMicroscope.addDevice(0, lFLIFilterWheelDevice);
    //SpatialPhaseModulatorDeviceBase lSpatialPhaseModulatorDeviceBase = new AlpaoDMDevice(1);
    //mBScopeMicroscope.addDevice(0, lSpatialPhaseModulatorDeviceBase);
    //MirrorModeScheduler lMirrorModeScheduler = new MirrorModeScheduler(lSpatialPhaseModulatorDeviceBase);
    //mBScopeMicroscope.addDevice(0, lMirrorModeScheduler);
    AnythingDevice lAnyDevice = new AnythingDevice();
    mBScopeMicroscope.addDevice(0, lAnyDevice);
    System.out.println("DEVICES ADDED");
  }


  public BScopeMicroscope getBScopeMicroscope() {
    return mBScopeMicroscope;
  }

}
