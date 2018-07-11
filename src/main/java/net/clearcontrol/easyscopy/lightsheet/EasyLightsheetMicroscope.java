package net.clearcontrol.easyscopy.lightsheet;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.instructions.LaserOnOffInstruction;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.imaging.*;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.state.instructions.AcquisitionStateBackupRestoreInstruction;
import clearcontrol.microscope.lightsheet.state.instructions.AcquisitionStateResetInstruction;
import clearcontrol.microscope.lightsheet.state.io.InterpolatedAcquisitionStateReader;
import clearcontrol.microscope.lightsheet.state.io.InterpolatedAcquisitionStateWriter;
import clearcontrol.microscope.lightsheet.timelapse.LightSheetTimelapse;
import clearcontrol.microscope.state.AcquisitionType;
import net.clearcontrol.easyscopy.EasyMicroscope;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public abstract class EasyLightsheetMicroscope extends EasyMicroscope
{
  private LightSheetMicroscope mLightSheetMicroscope;

  public EasyLightsheetMicroscope(LightSheetMicroscope lLightSheetMicroscope) {
    super(lLightSheetMicroscope);
    mLightSheetMicroscope = lLightSheetMicroscope;
  }


  // -----------------------------------------------------------------
  // imaging

  public SingleViewPlaneImager getSingleViewPlaneImager() {
    return new SingleViewPlaneImager(mLightSheetMicroscope, 0);
  }

  public SingleViewStackImager getSingleViewStackImager() {
    return new SingleViewStackImager(mLightSheetMicroscope);
  }

  /**
   * Depreacted because this imager ignores the current acquisition state. Use getSingleViewPlaneImager() instead
   * @return
   */
  @Deprecated
  public SinglePlaneImager getSinglePlaneImager() {
    return new SinglePlaneImager(mLightSheetMicroscope);
  }

  /**
   * Depreacted because this imager ignores the current acquisition state. Use getSingleViewPlaneImager() instead
   * @param pLightSheetZ
   * @param pDetectionArmZ
   * @return
   */
  @Deprecated
  public SinglePlaneImager getSinglePlaneImager(int pLightSheetZ, int pDetectionArmZ) {
    SinglePlaneImager imager = new SinglePlaneImager(mLightSheetMicroscope);
    imager.setIlluminationZ(pLightSheetZ);
    imager.setDetectionZ(pDetectionArmZ);
    return imager;
  }

  /**
   * Depreacted because this imager ignores the current acquisition state. Use getSingleViewStackImager() instead
   * @return
   */
  @Deprecated
  public SingleStackImager getSingleStackImager() {
    return new SingleStackImager(mLightSheetMicroscope);
  }

  public FusedStackImager getSequentialFusedStackImager() {
    FusedStackImager imager = new FusedStackImager(mLightSheetMicroscope);
    imager.setAcquisitionType(AcquisitionType.TimelapseSequential);
    return imager;
  }

  public FusedStackImager getInterleavedFusedStackImager() {
    FusedStackImager imager = new FusedStackImager(mLightSheetMicroscope);
    imager.setAcquisitionType(AcquisitionType.TimeLapseInterleaved);
    return imager;
  }

  public FusedStackImager getOpticsPrefusedFusedStackImager() {
    FusedStackImager imager = new FusedStackImager(mLightSheetMicroscope);
    imager.setAcquisitionType(AcquisitionType.TimeLapseOpticallyCameraFused);
    return imager;
  }

  /**
   * Deprecated: use getSinglePlaneImager instead!
   * @return
   */
  @Deprecated
  public DirectImage getDirectImage() {
    DirectImage lDirectImage = new DirectImage(mLightSheetMicroscope);
    return lDirectImage;
  }

  /**
   * Deprecated: use getSinglePlaneImager instead!
   * @return
   */
  @Deprecated
  public DirectImage getDirectImage(int pLightSheetZ, int pDetectionArmZ) {
    DirectImage lDirectImage = getDirectImage();
    lDirectImage.setIlluminationZ(pLightSheetZ);
    lDirectImage.setDetectionZ(pDetectionArmZ);
    return lDirectImage;
  }

  /**
   * Deprecated: use getSingleStackImager instead!
   * @return
   */
  @Deprecated
  public DirectImageStack getDirectImageStack() {
    return new DirectImageStack(mLightSheetMicroscope);
  }

  /**
   * Deprecated. Use getSequentialFusedStackImager()
   */
  @Deprecated
  public DirectFusedImageStack getDirectFusedImageStack(){
    return new DirectFusedImageStack(mLightSheetMicroscope);
  }

  // -----------------------------------------------------------------
  // general
  @Override
  public void terminate() {
    mLightSheetMicroscope.stop();
    mLightSheetMicroscope.close();
  }

  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }


  public LightSheetInterface getLightSheetDevice(String ... pMustContainStrings) {
    return getDevice(LightSheetInterface.class, 0, pMustContainStrings);
  }


  public LightSheetMicroscopeQueue getQueue() {
    return mLightSheetMicroscope.requestQueue();
  }

  public InterpolatedAcquisitionState getAcquisitionState() {
    return (InterpolatedAcquisitionState)mLightSheetMicroscope.getAcquisitionStateManager().getCurrentState();
  }

  public LightSheetTimelapse getLightSheetTimelapse() {
    return mLightSheetMicroscope.getDevice(LightSheetTimelapse.class, 0);
  }


  public LightSheetSignalGeneratorDevice getLightSheetSignalGeneratorDevice(String ... pMustContainStrings) {
    return getDevice(LightSheetSignalGeneratorDevice.class, 0, pMustContainStrings);
  }

  public boolean saveAcquisitionState(String pFilename, InterpolatedAcquisitionState pState){
    File file = new File(pFilename);
    return new InterpolatedAcquisitionStateWriter(file, pState).write();
  }

  public boolean loadAcquisitionState(String pFilename, InterpolatedAcquisitionState pState){
    File file = new File(pFilename);
    return new InterpolatedAcquisitionStateReader(file, pState).read();
  }

  public boolean backupAcquisitionState() {
    ArrayList<AcquisitionStateBackupRestoreInstruction> lSchedulerList = mLightSheetMicroscope.getDevices(AcquisitionStateBackupRestoreInstruction.class);
    for (AcquisitionStateBackupRestoreInstruction lScheduler : lSchedulerList) {
      if (lScheduler.isBackup()) {
        lScheduler.initialize();
        return lScheduler.enqueue(-1);
      }
    }
    return false;
  }

  public boolean restoreAcquisitionState() {
    ArrayList<AcquisitionStateBackupRestoreInstruction> lSchedulerList = mLightSheetMicroscope.getDevices(AcquisitionStateBackupRestoreInstruction.class);
    for (AcquisitionStateBackupRestoreInstruction lScheduler : lSchedulerList) {
      if (!lScheduler.isBackup()) {
        lScheduler.initialize();
        return lScheduler.enqueue(-1);
      }
    }
    return false;
  }

  public boolean resetAcquisitionState() {
    AcquisitionStateResetInstruction
        lResetter = (AcquisitionStateResetInstruction)mLightSheetMicroscope.getDevice(AcquisitionStateResetInstruction.class, 0);
    lResetter.initialize();
    lResetter.enqueue(0);
    return true;
  }

  public boolean turnLaserOn(String pLaserNameMustContain) {
    LaserDeviceInterface lLaser = getLaserDevice(pLaserNameMustContain);

    LaserOnOffInstruction
        lLaserOnOffScheduler = (LaserOnOffInstruction)new LaserOnOffInstruction(lLaser, true);
    return lLaserOnOffScheduler.enqueue(0);
  }


  public boolean turnLaserOff(String pLaserNameMustContain) {
    LaserDeviceInterface lLaser = getLaserDevice(pLaserNameMustContain);

    LaserOnOffInstruction lLaserOnOffScheduler = (LaserOnOffInstruction)new LaserOnOffInstruction(lLaser, false);
    return lLaserOnOffScheduler.enqueue(0);
  }





}
