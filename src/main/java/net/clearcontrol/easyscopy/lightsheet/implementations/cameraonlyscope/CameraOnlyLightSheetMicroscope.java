package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope;

import clearcl.ClearCLContext;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCamera;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.camera.device.DcamJ1Camera;

/**
 * The CameraOnlyLightSheetMicroscope does not represent a functional microscope. It was created to access a single
 * camera attached to the computer using the same API like with functional microscopes.
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
public class CameraOnlyLightSheetMicroscope extends
                                                     LightSheetMicroscope
{
  /**
   * Instantiates a lightsheet microscope with a given name.
   *
   * @param pStackFusionContext            ClearCL context for stack fusion
   * @param pMaxStackProcessingQueueLength max stack processing queue length
   * @param pThreadPoolSize
   */
  public CameraOnlyLightSheetMicroscope(ClearCLContext pStackFusionContext,
                                                 int pMaxStackProcessingQueueLength,
                                                 int pThreadPoolSize)
  {
    super("Hamamatsu Orca Flash Camera only microscope",
          pStackFusionContext,
          pMaxStackProcessingQueueLength,
          pThreadPoolSize);

    long lDefaultStackWidth = 512;
    long lDefaultStackHeight = 512;

    /*
    StackCameraDeviceInterface<?> lCamera =
        //HamStackCamera.buildWithInternalTriggering(0);
            HamStackCamera.buildWithSoftwareTriggering(0);
            //HamStackCamera.buildWithExternalTriggering(0);

    lCamera.getStackWidthVariable().set(lDefaultStackWidth);
    lCamera.getStackHeightVariable().set(lDefaultStackHeight);
    lCamera.getExposureInSecondsVariable().set(0.010);

    // lCamera.getStackVariable().addSetListener((o,n)->
    // {System.out.println("camera output:"+n);} );

    addDevice(0, lCamera);*/

    DcamJ1Camera lDJCamera = new DcamJ1Camera();
    addDevice(0, lDJCamera);

    useRecycler("adaptation", 1, 4, 4);

  }
}
