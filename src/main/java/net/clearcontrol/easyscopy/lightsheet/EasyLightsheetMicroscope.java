package net.clearcontrol.easyscopy.lightsheet;

import clearcl.ClearCLContext;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.imaging.*;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionEngine;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.state.AcquisitionType;
import net.clearcontrol.easyscopy.EasyMicroscope;
import net.clearcontrol.easyscopy.EasyScope;
import org.atteo.classindex.ClassIndex;

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




}
