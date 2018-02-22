package net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.devices.sim.LaserDeviceSimulator;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.devices.signalamp.devices.sim.ScalingAmplifierSimulator;
import clearcontrol.devices.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.simulation.LightSheetSimulationStackProvider;
import clearcontrol.microscope.lightsheet.simulation.SimulationUtils;
import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */

@EasyScope
public class AcademicScope extends EasyLightsheetMicroscope implements
                                                                      LoggingFeature
{

  static final MachineConfiguration
      sMachineConfiguration =
      MachineConfiguration.get();


  Variable<Boolean> mCameraTrigger =
      new Variable<Boolean>("CameraTrigger",
                            false);


  AcademicLightScheetMicroscope mAcademicLightScheetMicroscope;

  private static AcademicScope sInstance = null;
  public static AcademicScope getInstance() {
    if (sInstance == null) {
      sInstance = new AcademicScope();
    }
    return sInstance;
  }

  private AcademicScope() {
    super(new AcademicScopeBuilder().getLightSheetMicroscope());
    mAcademicLightScheetMicroscope = (AcademicLightScheetMicroscope)getLightSheetMicroscope();
  }

  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }

  public void addLaser(int lWaveLengthInNanometers ) {

    LaserDeviceInterface lLaser =
        new LaserDeviceSimulator("Laser "
                                 + lWaveLengthInNanometers,
                                 0,
                                 lWaveLengthInNanometers,
                                 100);
    getLightSheetMicroscope().addDevice(0, lLaser);
  }

  public void mountDrosophilaSample(int pDivisionTime) {

    if (getLightSheetMicroscope().getDevice(LightSheetMicroscopeSimulationDevice.class, 0 ) != null) {
      warning("Warning: There is a sample mounted already! You cannot mount another one!");
      return;
    }

    LightSheetMicroscopeSimulationDevice
        lSimulatorDevice =
        SimulationUtils
            .getSimulatorDevice(mAcademicLightScheetMicroscope.getSimulationContext(),
                                mAcademicLightScheetMicroscope.getNumberOfDetectionArms(),
                                mAcademicLightScheetMicroscope.getNumberOfLightSheets(),
                                2048,
                                pDivisionTime,
                                320,
                                320,
                                320,
                                false);

    getLightSheetMicroscope().addDevice(0, lSimulatorDevice);

    lSimulatorDevice.connectTo(getLightSheetMicroscope());

    int count = 0;
    for (StackCameraDeviceSimulator lCameraDevice : getLightSheetMicroscope().getDevices(StackCameraDeviceSimulator.class))
    {
      LightSheetSimulationStackProvider
          lStackProvider =
          lSimulatorDevice.getStackProvider(count);
      lCameraDevice.setStackCameraSimulationProvider(lStackProvider);
      count++;
    }
  }

  private int mMaxCameraResolution = 256;

  public void addCamera(int pImageWidth, int pImageHeight) {
    if (getLightSheetMicroscope().getDevice(LightSheetMicroscopeSimulationDevice.class, 0) != null) {
      warning("A sample is mounted already. You cannot add cameras any more.");
      return;
    }
    if (pImageWidth > mMaxCameraResolution) {
      mMaxCameraResolution = pImageHeight;
    }
    if (pImageWidth > mMaxCameraResolution) {
      mMaxCameraResolution = pImageHeight;
    }

    double lPixelSize = 0.26;


    final StackCameraDeviceSimulator lCamera =
        new StackCameraDeviceSimulator("StackCamera"
                                       + getLightSheetMicroscope().getNumberOfDetectionArms(),
                                       mCameraTrigger);

    long lMaxWidth = pImageWidth;

    long lMaxHeight = pImageHeight;

    lCamera.getMaxWidthVariable().set(lMaxWidth);
    lCamera.getMaxHeightVariable().set(lMaxHeight);
    lCamera.getStackWidthVariable().set(lMaxWidth / 2);
    lCamera.getStackHeightVariable().set(lMaxHeight);
    lCamera.getExposureInSecondsVariable().set(0.010);


    // lCamera.getStackVariable().addSetListener((o,n)->
    // {System.out.println("camera output:"+n);} );

    getLightSheetMicroscope().addDevice(0, lCamera);

    final DetectionArm
        lDetectionArm = new DetectionArm("D" + getLightSheetMicroscope().getNumberOfDetectionArms());
    lDetectionArm.getPixelSizeInMicrometerVariable()
                 .set(lPixelSize);

    getLightSheetMicroscope().addDevice(getLightSheetMicroscope().getNumberOfDetectionArms(), lDetectionArm);
  }

  public void addLightSheet() {
    if (getLightSheetMicroscope().getDevice(LightSheetOpticalSwitch.class, 0) != null) {
      warning("An optical switch is mounted already. You cannot mount any lights after the switch was mounted.");
      return;
    }
    final LightSheet lLightSheet =
        new LightSheet("I" + getLightSheetMicroscope().getNumberOfLightSheets(),
                       9.4,
                       getLightSheetMicroscope().getNumberOfLaserLines());
    getLightSheetMicroscope().addDevice(getLightSheetMicroscope().getNumberOfLightSheets(), lLightSheet);
  }

  public void addOpticalSwitch() {
    if (getLightSheetMicroscope().getDevice(LightSheetOpticalSwitch.class, 0) != null) {
      warning("You cannot mount several optical switches!");
      return;
    }
    LightSheetOpticalSwitch lLightSheetOpticalSwitch =
        new LightSheetOpticalSwitch("OpticalSwitch",
                                    getLightSheetMicroscope().getNumberOfLightSheets());

    getLightSheetMicroscope().addDevice(0, lLightSheetOpticalSwitch);
  }

  public void addScalingAmplifier() {

    ScalingAmplifierDeviceInterface lScalingAmplifier1 =
        new ScalingAmplifierSimulator("ScalingAmplifier" + getLightSheetMicroscope().getDevices(ScalingAmplifierSimulator.class).size());
    getLightSheetMicroscope().addDevice(0, lScalingAmplifier1);
  }

  public void addSignalGenerator() {
    SignalGeneratorSimulatorDevice
        lSignalGeneratorSimulatorDevice =
        new SignalGeneratorSimulatorDevice();

    // addDevice(0, lSignalGeneratorSimulatorDevice);
    lSignalGeneratorSimulatorDevice.getTriggerVariable()
                                   .sendUpdatesTo(mCameraTrigger);/**/

    final LightSheetSignalGeneratorDevice
        lLightSheetSignalGeneratorDevice =
        LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorSimulatorDevice,
                                             true);

    getLightSheetMicroscope().addDevice(0, lLightSheetSignalGeneratorDevice);
  }

  public boolean turnOn()
  {

    info("Opening microscope devices...");
    if (getLightSheetMicroscope().open())
    {
      info("Starting microscope devices...");
      if (getLightSheetMicroscope().start())
      {
        getLightSheetMicroscope().useRecycler("adaptation", 1, 4, 4);
        return true;
      }
    }
    return false;
  }

}


