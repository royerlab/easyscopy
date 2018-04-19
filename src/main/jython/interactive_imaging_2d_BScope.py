# This demo script allows continous interactive imaging. 
# End the script by closing the window it opens.
# 
# Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
# April 2018
#
#########################################################################################
from net.clearcontrol.easyscopy.lightsheet.implementations.xwing import SimulatedXWingScope;
from net.clearcontrol.easyscopy.lightsheet.implementations.xwing import XWingScope;

from net.clearcontrol.easyscopy.lightsheet.implementations.bscope import BScope;

from fastfuse.tasks import GaussianBlurTask
from net.imglib2.img.display.imagej import ImageJFunctions;
from clearcontrol.microscope.state import AcquisitionType;
from net.clearcontrol.easyscopy import EasyScopyUtilities;
from clearcl.imagej import ClearCLIJ;
from java.lang import Float;
from java.lang import Integer;
from java.lang import Thread;
from ij import IJ;

# sample configuration (in microns)
slicePositionToImageMax = 200
slicePositionToImageMin = 100


# excitation configuration
exposureTimeInSeconds = 0.05
laserPowerInPercent = 20
frameDelayInSeconds = 0.1

# microscope device / imaging configuration
lightSheetIndex = 0 
detectionArmIndex = 0

# IMAGING using EasyScopy 
scope = BScope.getInstance();

# IMAGE POST-PROCESSING ClearCLIJ
clij = ClearCLIJ.getInstance();

# initialize laser, focus and imaging
scope.shutDownAllLasers();

laserDevice = scope.getLaserDevice("488");
laserDevice.setTargetPowerInPercent(laserPowerInPercent);
laserDevice.setLaserPowerOn(True);
laserDevice.setLaserOn(True);
laserDevice.setLaserPowerOn(True);
laserDevice.setLaserOn(True);

# configure imaging
singlePlaneImager = scope.getSingleViewPlaneImager();
singlePlaneImager.setImageWidth(1024);
singlePlaneImager.setImageHeight(1024);
singlePlaneImager.setMinZ(slicePositionToImageMin);
singlePlaneImager.setExposureTimeInSeconds(exposureTimeInSeconds);

# acquire a first image
image = singlePlaneImager.acquire();
imagePlus = clij.converter(image).getImagePlus();
imagePlus.show();

pos = slicePositionToImageMin

while(True):
	# acquire another image and put it in the window of the first one
	pos = pos + 5
	if (pos > slicePositionToImageMax):
		pos = slicePositionToImageMin;

	singlePlaneImager.setMinZ(pos);

	
	image = singlePlaneImager.acquire();
	updatedImagePlus = clij.converter(image).getImagePlus();
	imagePlus.setProcessor(updatedImagePlus.getProcessor());
	imagePlus.updateAndDraw();
	imagePlus.killRoi();
	IJ.run(imagePlus, "Enhance Contrast", "saturated=0.35");

	# do online post-processing wiht ImageJ
	# IJ.run(imagePlus, "Find Maxima...", "noise=200 output=[Point Selection]");

    # sleep and potentially exit the loop if the user closed the window
	Thread.sleep((int)(frameDelayInSeconds * 1000));
	if (imagePlus.getWindow() is None):
		print("Window closed");
		break;

#thats always a good idea by the end
scope.shutDownAllLasers();

#BScope.cleanup();

print("Bye");



