package net.clearcontrol.easyscopy;

import clearcl.ClearCLContext;
import clearcontrol.devices.cameras.CameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.stages.BasicStageInterface;
import clearcontrol.instructions.InstructionInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
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

  public SignalGeneratorInterface getSignalGeneratorDevice(String ... pMustContainStrings) {
    return getDevice(SignalGeneratorInterface.class, 0, pMustContainStrings);
  }

  public InstructionInterface getInstructionDevice(String... pMustContainStrings) {
    return getDevice(InstructionInterface.class, 0, pMustContainStrings);
  }








  public <O extends Object> O getDevice(Class<O> pClass, int pDeviceIndex, String ... pMustContainStrings)
  {
    int lDeviceIndex = 0;
    ArrayList<O>
        lDeviceList = mMicroscopeInterface.getDevices(pClass);
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
          return lDevice;
        } else {
          lDeviceIndex++;
        }
      }
    }
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
