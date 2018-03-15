package net.clearcontrol.easyscopy.lightsheet.implementations.cameraonlyscope.camera.device;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import coremem.enums.NativeTypeEnum;
import dcamj1.DcamAcquisition;
import dcamj1.DcamAcquisitionListener;
import dcamj1.DcamFrame;
import org.bridj.Pointer;

import static org.junit.Assert.assertTrue;

public class DcamJ1Camera {

    double mTimeOutInSeconds = 0.02;
    double mExposureTimeInSeconds = 0.05;

    long mImageWidth = 2048;
    long mImageHeight = 2048;


    OffHeapPlanarStack mAcquiredStack = null;


    final DcamAcquisition lDcamAcquisition;
    public DcamJ1Camera() {
        //byte[] lBuffer = new byte[(int)pDcamFrame.getTotalSizeInBytesForAllPlanes()];

        lDcamAcquisition = new DcamAcquisition(0);
        assertTrue(lDcamAcquisition.open());
    }

    public StackInterface acquire() {
        // Create an acquisition

        // add a listener which is called whenever an image arrives
        // e.g. it might be called every 0.05 s if 0.05 is exposure
        // time
        lDcamAcquisition.addListener(new DcamAcquisitionListener()
        {
            @Override
            public void frameArrived(	final DcamAcquisition pDcamAquisition,
                                         final long pAbsoluteFrameIndex,
                                         final long pArrivalTimeStamp,
                                         final long pFrameIndexInBufferList,
                                         final DcamFrame pDcamFrame)
            {
                System.out.format("Frame %d in buffer %d arrived at %d \n",
                        pAbsoluteFrameIndex,
                        pFrameIndexInBufferList,
                        pArrivalTimeStamp);
                System.out.format("frameArrived: hashcode= %d index=%d dimensions: (%d,%d) \n ",
                        pDcamFrame.hashCode(),
                        pDcamFrame.getIndex(),
                        pDcamFrame.getWidth(),
                        pDcamFrame.getHeight());

                // transform the arriving data to an OffHeapPlanarStack,
                // the usual data structure for image stacks in clearcontrol
                OffHeapPlanarStack lStack = mAcquiredStack;
                if (lStack == null) {
                    lStack = new OffHeapPlanarStack(true, 0, NativeTypeEnum.UnsignedShort, 1, new long[]{pDcamFrame.getWidth(), pDcamFrame.getHeight(), pDcamFrame.getDepth()});
                }
                Pointer<Byte> lPointer = lStack.getContiguousMemory().getBridJPointer(Byte.class);
                pDcamFrame.copyAllPlanesToSinglePointer(lPointer);

                mAcquiredStack = lStack;
            }
        });

        // configure the camera
        lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();
        lDcamAcquisition.getProperties().setBinning(1);
        lDcamAcquisition.getProperties().setExposure(mExposureTimeInSeconds);
        lDcamAcquisition.getProperties().setCenteredROI(	mImageWidth,
                mImageHeight);

        // start imaging, wait for a given timeout; within this timeout
        // the listener above should be called
        System.out.println("Starting acquisition");
        lDcamAcquisition.startAcquisition();
        try {
            Thread.sleep((long)(mTimeOutInSeconds * 10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // stop imaging
        System.out.println("Stoping acquisition");
        lDcamAcquisition.stopAcquisition();
        return mAcquiredStack;
    }

    public void setExposureTimeInSeconds(double pExposureTimeInSeconds) {
        this.mExposureTimeInSeconds = pExposureTimeInSeconds;
        this.mTimeOutInSeconds = 1.9 * pExposureTimeInSeconds;
    }

    public void setImageWidth(long pImageWidth) {
        this.mImageWidth = pImageWidth;
    }

    public void setImageHeight(long pImageHeight) {
        this.mImageHeight = pImageHeight;
    }

    public void close() {

        lDcamAcquisition.close();
    }
}
