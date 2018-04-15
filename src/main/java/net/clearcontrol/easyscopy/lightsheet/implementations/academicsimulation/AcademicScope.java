package net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.enums.ImageChannelDataType;
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
import clearcontrol.microscope.lightsheet.state.ControlPlaneLayout;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.timelapse.SingleViewAcquisitionScheduler;
import clearcontrol.microscope.state.AcquisitionStateManager;
import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import simbryo.synthoscopy.microscope.aberration.IlluminationMisalignment;
import simbryo.synthoscopy.microscope.lightsheet.LightSheetMicroscopeSimulatorOrtho;
import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;
import simbryo.synthoscopy.microscope.lightsheet.organoid.LightSheetMicroscopeSimulatorOrganoid;
import simbryo.synthoscopy.microscope.parameters.PhantomParameter;
import simbryo.synthoscopy.microscope.parameters.UnitConversion;
import simbryo.textures.noise.UniformNoise;

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


  AcademicLightScheetMicroscope mAcademicLightSheetMicroscope;

  public AcademicScope() {
    super(new AcademicScopeBuilder().getLightSheetMicroscope());
    mAcademicLightSheetMicroscope = (AcademicLightScheetMicroscope)getLightSheetMicroscope();
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

  private void mountSample(LightSheetMicroscopeSimulatorOrtho pOrtho) {

    if (getLightSheetMicroscope().getDevice(LightSheetMicroscopeSimulationDevice.class, 0 ) != null) {
      warning("Warning: There is a sample mounted already! You cannot mount another one!");
      return;
    }

    LightSheetMicroscopeSimulationDevice
            lSimulatorDevice =
            getSimulatorDevice(mAcademicLightSheetMicroscope.getSimulationContext(),
                    pOrtho,
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

  public void mountDrosophilaSample(int pDivisionTime) {
    mountSample(getDrosophilaFromVirtualFlyLab(mAcademicLightSheetMicroscope.getSimulationContext(),
            mAcademicLightSheetMicroscope.getNumberOfDetectionArms(),
            mAcademicLightSheetMicroscope.getNumberOfLightSheets(),
            2048,
            pDivisionTime,
            320,
            320,
            320));
  }

  public void mountOrganoidSample(int pDivisionTime) {
    mountSample(getOrganoidFromVirtualWetlab(mAcademicLightSheetMicroscope.getSimulationContext(),
            mAcademicLightSheetMicroscope.getNumberOfDetectionArms(),
            mAcademicLightSheetMicroscope.getNumberOfLightSheets(),
            2048,
            pDivisionTime,
            160,
            160,
            160));
  }

  private LightSheetMicroscopeSimulatorOrtho getDrosophilaFromVirtualFlyLab(ClearCLContext pSimulationContext,
                                                                            int pNumberOfDetectionArms,
                                                                            int pNumberOfLightSheets,
                                                                            int pMaxCameraResolution,
                                                                            float pDivisionTime,
                                                                            int pPhantomWidth,
                                                                            int pPhantomHeight,
                                                                            int pPhantomDepth) {

    LightSheetMicroscopeSimulatorDrosophila lSimulator = new LightSheetMicroscopeSimulatorDrosophila(pSimulationContext,
                                                pNumberOfDetectionArms,
                                                pNumberOfLightSheets,
                                                pMaxCameraResolution,
                                                pDivisionTime,
                                                pPhantomWidth,
                                                pPhantomHeight,
                                                pPhantomDepth);


    // lSimulator.openViewerForControls();
    lSimulator.setFreezedEmbryo(true);
    lSimulator.setNumberParameter(UnitConversion.Length, 0, 700f);

    // lSimulator.addAbberation(new Miscalibration());
    // lSimulator.addAbberation(new SampleDrift());
    lSimulator.addAbberation(IlluminationMisalignment.buildXYZ(0,
                                                               0,
                                                               0));

    return lSimulator;
  }

  private LightSheetMicroscopeSimulatorOrtho getOrganoidFromVirtualWetlab(ClearCLContext pSimulationContext,
                                                                          int pNumberOfDetectionArms,
                                                                          int pNumberOfLightSheets,
                                                                          int pMaxCameraResolution,
                                                                          float pDivisionTime,
                                                                          int pPhantomWidth,
                                                                          int pPhantomHeight,
                                                                          int pPhantomDepth) {

    LightSheetMicroscopeSimulatorOrganoid lSimulator = new LightSheetMicroscopeSimulatorOrganoid(pSimulationContext,
              pNumberOfDetectionArms,
              pNumberOfLightSheets,
              pMaxCameraResolution,
              pDivisionTime,
              pPhantomWidth,
              pPhantomHeight,
              pPhantomDepth);


      // lSimulator.openViewerForControls();
      lSimulator.setFreezedEmbryo(true);
      lSimulator.setNumberParameter(UnitConversion.Length, 0, 700f);

      // lSimulator.addAbberation(new Miscalibration());
      // lSimulator.addAbberation(new SampleDrift());
      lSimulator.addAbberation(IlluminationMisalignment.buildXYZ(0,
              0,
              0));

      return lSimulator;
  }

  private LightSheetMicroscopeSimulationDevice getSimulatorDevice( ClearCLContext pSimulationContext,
                                                                   LightSheetMicroscopeSimulatorOrtho pSimulator,

                     boolean pUniformFluorescence)
  {


    // lSimulator.addAbberation(new DetectionMisalignment());

    /*scheduleAtFixedRate(() -> lSimulator.simulationSteps(1),
    10,
    TimeUnit.MILLISECONDS);/**/

    if (pUniformFluorescence)
    {
      long lEffPhantomWidth = pSimulator.getWidth();
      long lEffPhantomHeight = pSimulator.getHeight();
      long lEffPhantomDepth = pSimulator.getDepth();

      ClearCLImage lFluoPhantomImage =
          pSimulationContext.createSingleChannelImage(
              ImageChannelDataType.Float,
              lEffPhantomWidth,
              lEffPhantomHeight,
              lEffPhantomDepth);

      ClearCLImage lScatterPhantomImage =
          pSimulationContext.createSingleChannelImage(ImageChannelDataType.Float,
                                                      lEffPhantomWidth / 2,
                                                      lEffPhantomHeight / 2,
                                                      lEffPhantomDepth / 2);

      UniformNoise lUniformNoise = new UniformNoise(3);
      lUniformNoise.setNormalizeTexture(false);
      lUniformNoise.setMin(0.25f);
      lUniformNoise.setMax(0.75f);
      lFluoPhantomImage.readFrom(lUniformNoise.generateTexture(lEffPhantomWidth,
                                                               lEffPhantomHeight,
                                                               lEffPhantomDepth),
                                 true);

      lUniformNoise.setMin(0.0001f);
      lUniformNoise.setMax(0.001f);
      lScatterPhantomImage.readFrom(lUniformNoise.generateTexture(lEffPhantomWidth
                                                                  / 2,
                                                                  lEffPhantomHeight
                                                                  / 2,
                                                                  lEffPhantomDepth
                                                                  / 2),
                                    true);

      pSimulator.setPhantomParameter(PhantomParameter.Fluorescence,
                                     lFluoPhantomImage);

      pSimulator.setPhantomParameter(PhantomParameter.Scattering,
                                     lScatterPhantomImage);
    }

    // lSimulator.openViewerForCameraImage(0);
    // lSimulator.openViewerForAllLightMaps();
    // lSimulator.openViewerForScatteringPhantom();

    LightSheetMicroscopeSimulationDevice lLightSheetMicroscopeSimulatorDevice =
        new LightSheetMicroscopeSimulationDevice(pSimulator);

    return lLightSheetMicroscopeSimulatorDevice;
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

    for (int c = 0; c < getLightSheetMicroscope().getNumberOfDetectionArms(); c++) {
      for (int l = 0; l < getLightSheetMicroscope().getNumberOfLightSheets(); l++) {
        getLightSheetMicroscope().addDevice(0, new SingleViewAcquisitionScheduler(c, l, getLightSheetMicroscope().getRecycler(c)));
      }
    }

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

  public void addAcquisitionStateManager(int pNumberOfControlPlanes) {
    AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>> lAcquisitionStateManager;
    lAcquisitionStateManager =
            (AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>>) getLightSheetMicroscope().addAcquisitionStateManager();
    InterpolatedAcquisitionState lAcquisitionState =
            new InterpolatedAcquisitionState("default",
                    getLightSheetMicroscope());
    lAcquisitionState.setupControlPlanes(pNumberOfControlPlanes,
            ControlPlaneLayout.Circular);
    lAcquisitionState.copyCurrentMicroscopeSettings();
    lAcquisitionStateManager.setCurrentState(lAcquisitionState);
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


