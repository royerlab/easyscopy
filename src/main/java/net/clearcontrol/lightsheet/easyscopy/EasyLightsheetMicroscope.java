package net.clearcontrol.lightsheet.easyscopy;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.imaging.DirectImage;
import clearcontrol.microscope.lightsheet.imaging.DirectImageStack;
import net.clearcontrol.easyscopy.EasyScope;
import org.atteo.classindex.ClassIndex;

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


  // -----------------------------------------------------------------
  // Lasers

  public Object getDevice(int pDeviceIndex, String ... pMustContainStrings) {
    int lDeviceIndex = 0;
    ArrayList<Object> lDeviceList = mLightSheetMicroscope.getDevices(Object.class);
    for (Object lDevice : lDeviceList) {
      String lName = lDevice.toString();
      boolean lNameMatches = true;
      for (String lMustContainString : pMustContainStrings) {
        lNameMatches = lName.contains(lMustContainString);
        if (!lNameMatches) {
          break;
        }
      }
      if (lNameMatches) {
        if (lDeviceIndex == pDeviceIndex)
        {
          return lDevice;
        } else {
          lDeviceIndex++;
        }
      }
    }
    return null;
  }

  public Object getDevice(String... pMustContainStrings) {
    return getDevice(0, pMustContainStrings);
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

  public ArrayList<Object> getDevices()
  {
    return mLightSheetMicroscope.getDevices(Object.class);
  }

}
