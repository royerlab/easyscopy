package clearcl.imagej.utilities;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.ClearCLKernel;
import fastfuse.FastFusionEngine;
import fastfuse.FastFusionEngineInterface;
import fastfuse.tasks.TaskBase;

import fastfuse.tasks.TaskHelper;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.util.Map;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class GenericBinaryFastFuseTask extends TaskBase
{
  ClearCLContext mContext;

  String mKernelName;
  String mSrcImageSlotKey;
  String mDstImageSlotKey;
  String mKernelInputImageParameterName;
  String mKernelOutputImageParameterName;
  Map<String, Object> mParameterMap;




  public GenericBinaryFastFuseTask(FastFusionEngine pFastFusionEngine,
                                   Class pAnchorClass,
                                   String pProgramFilename,
                                   String pKernelName,
                                   String pKernelInputImageParameterName,
                                   String pKernelOutputImageParameterName,
                                   String pSrcImageSlotKey,
                                   String pDstImageSlotKey) throws
                                                           IOException
  {
    super(pSrcImageSlotKey);
    setupProgram(pAnchorClass, pProgramFilename);
    mSrcImageSlotKey = pSrcImageSlotKey;
    mDstImageSlotKey = pDstImageSlotKey;
    mKernelInputImageParameterName = pKernelInputImageParameterName;
    mKernelOutputImageParameterName = pKernelOutputImageParameterName;

    mKernelName = pKernelName;
    mContext = pFastFusionEngine.getContext();
  }

  public void setParameterMap(Map<String, Object> pParameterMap)
  {
    mParameterMap = pParameterMap;
  }

  @Override public boolean enqueue(FastFusionEngineInterface pFastFusionEngine,
                                   boolean pWaitToFinish)
  {
    System.out.println("available images are");

    for (String Key : pFastFusionEngine.getAvailableImagesSlotKeys())
    {
      System.out.println(Key);
    }

    if (pFastFusionEngine.isImageAvailable(mSrcImageSlotKey))
    {
      System.out.println(mSrcImageSlotKey
                         + " is being cached and processed");

      ClearCLImage
          lSrcImage =
          pFastFusionEngine.getImage(mSrcImageSlotKey);

      MutablePair<Boolean, ClearCLImage> lFlagAndDstImage =
          pFastFusionEngine.ensureImageAllocated(mDstImageSlotKey,
                                                 lSrcImage.getChannelDataType(),
                                                 lSrcImage.getDimensions());
      ClearCLImage lDstImage = lFlagAndDstImage.getRight();

      try
      {
        ClearCLKernel lClearCLKernel =
            getKernel(mContext, mKernelName,
                      TaskHelper.getOpenCLDefines(lSrcImage,
                                                  lDstImage));


        lClearCLKernel.setGlobalSizes(lDstImage.getDimensions());
        //lKernel.setArguments(lDstImage, lSrcImage);

        lClearCLKernel.setArgument(mKernelInputImageParameterName, lSrcImage);
        lClearCLKernel.setArgument(mKernelOutputImageParameterName, lDstImage);
        if (mParameterMap != null) {
          for (String key : mParameterMap.keySet()) {
            lClearCLKernel.setArgument(key, mParameterMap.get(key));
          }
        }
        runKernel(lClearCLKernel, pWaitToFinish);
        lFlagAndDstImage.setLeft(true);


        System.out.println("available images are now ");

        for (String Key : pFastFusionEngine.getAvailableImagesSlotKeys())
        {
          System.out.println(Key);
        }

      }
      catch (IOException e)
      {
        e.printStackTrace();
        return false;
      }
    }
    else
    {
      System.out.println(mSrcImageSlotKey + " was not available");
    }

    return true;
  }
}
