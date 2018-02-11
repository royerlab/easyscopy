package net.clearcontrol.lightsheet.easyscopy;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;

import java.util.ArrayList;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public abstract class EasyLightsheetMicroscope
{
  private LightSheetMicroscope mLightSheetMicroscope;
  public EasyLightsheetMicroscope(LightSheetMicroscope lLightSheetMicroscope) {
    mLightSheetMicroscope = lLightSheetMicroscope;
  }

  // -----------------------------------------------------------------
  // Lasers

  /**
   * Safty first. This functions shuts down all lasers
   *
   */
  public void shutDownAllLasers()
  {
    ArrayList<LaserDeviceInterface> lList = mLightSheetMicroscope.getDevices(LaserDeviceInterface.class);
    for (LaserDeviceInterface lLaser : lList)
    {
      lLaser.setLaserOn(false);
      lLaser.setLaserPowerOn(false);
      lLaser.setTargetPowerInMilliWatt(0);
    }
  }

  /**
   * Get a laser device
   *
   * @param pWavelengthInNanometers
   * @return
   */
  public LaserDeviceInterface getLaserDevice(int pWavelengthInNanometers) {
    ArrayList<LaserDeviceInterface> lList = mLightSheetMicroscope.getDevices(LaserDeviceInterface.class);
    for (LaserDeviceInterface lLaser : lList) {
      if (lLaser.getWavelengthInNanoMeter() == pWavelengthInNanometers) {
        return lLaser;
      }
    }
    return null;
  }

  public ArrayList<Integer> getAvailableLaserWavelengthsInNanometers()
  {
    ArrayList<LaserDeviceInterface> lList = mLightSheetMicroscope.getDevices(LaserDeviceInterface.class);
    ArrayList<Integer> lResultList = new ArrayList<>();
    for (LaserDeviceInterface lLaser : lList)
    {
      lResultList.add(lLaser.getWavelengthInNanoMeter());
    }
    return lResultList;
  }

  // -----------------------------------------------------------------
  // imaging
  public DirectImage getDirectImage() {
    DirectImage lDirectImage = new DirectImage(mLightSheetMicroscope);
    return lDirectImage;
  }

  public DirectImage getDirectImage(int pLightSheetZ, int pDetectionArmZ) {
    DirectImage lDirectImage = getDirectImage();
    lDirectImage.setIlluminationZ(pLightSheetZ);
    lDirectImage.setDetectionZ(pDetectionArmZ);
    return lDirectImage;
  }

  public DirectImageStack getDirectImageStack() {
    return new DirectImageStack(mLightSheetMicroscope);
  }

  // -----------------------------------------------------------------
  // general

  public void terminate() {
    mLightSheetMicroscope.stop();
    mLightSheetMicroscope.close();
  }

  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }
}
