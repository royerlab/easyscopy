package net.clearcontrol.easyscopy.lightsheet.implementations.academicsimulation;

import clearcl.ClearCLContext;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class AcademicLightScheetMicroscope extends
                                           LightSheetMicroscope
{

  ClearCLContext mFusionContext;
  ClearCLContext mSimulationContext;

  /**
   * Instantiates a lightsheet microscope with a given name.
   *
   * @param pDeviceName                    device name
   * @param pStackFusionContext            ClearCL context for stack fusion
   * @param pMaxStackProcessingQueueLength max stack processing queue length
   * @param pThreadPoolSize
   */
  public AcademicLightScheetMicroscope(String pDeviceName,
                                       ClearCLContext pStackFusionContext,
                                       ClearCLContext pSimulationContext,
                                       int pMaxStackProcessingQueueLength,
                                       int pThreadPoolSize)
  {
    super(pDeviceName,
          pStackFusionContext,
          pMaxStackProcessingQueueLength,
          pThreadPoolSize);

    mFusionContext = pStackFusionContext;
    mSimulationContext = pSimulationContext;

    addTimelapse();
  }

  ClearCLContext getFusionContext()
  {
    return mFusionContext;
  }

  ClearCLContext getSimulationContext()
  {
    return mSimulationContext;
  }
}
