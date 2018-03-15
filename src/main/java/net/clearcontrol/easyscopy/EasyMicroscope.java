package net.clearcontrol.easyscopy;

import clearcl.ClearCLContext;
import clearcontrol.devices.cameras.CameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.stages.BasicStageInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.SpatialPhaseModulatorDeviceInterface;

import java.util.ArrayList;
import java.util.Arrays;

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

  public LaserDeviceInterface getLaserDevice(String ... pMustContainStrings) {
    return getDevice(LaserDeviceInterface.class, 0, pMustContainStrings);
  }

  public CameraDeviceInterface getCameraDevice(String ... pMustContainStrings) {
    return getDevice(CameraDeviceInterface.class, 0, pMustContainStrings);
  }

  public DetectionArmInterface getDetectionArmDevice(String ... pMustContainStrings) {
    return getDevice(DetectionArmInterface.class, 0, pMustContainStrings);
  }


  public SpatialPhaseModulatorDeviceInterface getSpatialPhaseModulatorDevice(String ... pMustContainStrings) {
    return getDevice(SpatialPhaseModulatorDeviceInterface.class, 0, pMustContainStrings);
  }

  public BasicStageInterface getBasicStageDevice(String ... pMustContainStrings) {
    return getDevice(BasicStageInterface.class, 0, pMustContainStrings);
  }

  public FilterWheelDeviceInterface getFilterWheelDevice(String ... pMustContainStrings) {
    return getDevice(FilterWheelDeviceInterface.class, 0, pMustContainStrings);
  }









  public <O extends Object> O getDevice(Class<O> pClass, int pDeviceIndex, String ... pMustContainStrings)
  {
    int lDeviceIndex = 0;
    //System.out.println(pClass);
    ArrayList<O>
        lDeviceList = mMicroscopeInterface.getDevices(pClass);
    System.out.println("Printing Device List in EasyMicroscopy of "+pClass+" class: "+lDeviceList);
    for (O lDevice : lDeviceList) {
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
          //System.out.println("Returning Device for "+lName);
          return lDevice;
        } else {
          lDeviceIndex++;
        }
      }
    }
    System.out.println("Returning Null Device for "+ Arrays.toString(pMustContainStrings));
    return null;
  }

  public Object getDevice(int pDeviceIndex, String ... pMustContainStrings) {
    return getDevice(Object.class, pDeviceIndex, pMustContainStrings);
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
