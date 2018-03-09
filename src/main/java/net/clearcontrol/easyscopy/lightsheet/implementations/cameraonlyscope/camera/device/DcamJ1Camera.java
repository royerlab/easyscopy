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


    public DcamJ1Camera() {
        //byte[] lBuffer = new byte[(int)pDcamFrame.getTotalSizeInBytesForAllPlanes()];
    }

    public StackInterface acquire() {

        final DcamAcquisition lDcamAcquisition = new DcamAcquisition(0);

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

                OffHeapPlanarStack lStack = mAcquiredStack;
                if (lStack == null) {
                    lStack = new OffHeapPlanarStack(true, 0, NativeTypeEnum.UnsignedShort, 1, new long[]{pDcamFrame.getWidth(), pDcamFrame.getHeight(), pDcamFrame.getDepth()});
                }
                Pointer<Byte> lPointer = lStack.getContiguousMemory().getBridJPointer(Byte.class);
                pDcamFrame.copyAllPlanesToSinglePointer(lPointer);

                mAcquiredStack = lStack;
            }
        });

        assertTrue(lDcamAcquisition.open());
        lDcamAcquisition.getProperties().setOutputTriggerToProgrammable();
        lDcamAcquisition.getProperties().setBinning(1);
        lDcamAcquisition.getProperties().setExposure(mExposureTimeInSeconds);
        lDcamAcquisition.getProperties().setCenteredROI(	mImageWidth,
                mImageHeight);

        lDcamAcquisition.startAcquisition();
        try {
            Thread.sleep((long)(mTimeOutInSeconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lDcamAcquisition.stopAcquisition();
        lDcamAcquisition.close();
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
}
