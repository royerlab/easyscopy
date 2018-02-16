package net.clearcontrol.easyscopy;

import clearcl.ClearCLContext;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.MicroscopeInterface;

import java.util.ArrayList;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public abstract class EasyMicroscope
{
  protected MicroscopeInterface mMicroscopeInterface;


  public EasyMicroscope(MicroscopeInterface pMicroscopeInterface) {
    mMicroscopeInterface = pMicroscopeInterface;
  }


  // -----------------------------------------------------------------
  // Lasers

  /**
   * Safty first. This functions shuts down all lasers
   *
   */
  public void shutDownAllLasers()
  {
    ArrayList<LaserDeviceInterface> lList = mMicroscopeInterface.getDevices(LaserDeviceInterface.class);
    for (LaserDeviceInterface lLaser : lList)
    {
      lLaser.setLaserOn(false);
      lLaser.setLaserPowerOn(false);
      lLaser.setTargetPowerInMilliWatt(0);
    }
  }


  // -----------------------------------------------------------------
  // Devices

  public Object getDevice(int pDeviceIndex, String ... pMustContainStrings) {
    int lDeviceIndex = 0;
    ArrayList<Object>
        lDeviceList = mMicroscopeInterface.getDevices(Object.class);
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


  public ArrayList<Object> getDevices()
  {
    return mMicroscopeInterface.getDevices(Object.class);
  }

  public abstract void terminate();
}
