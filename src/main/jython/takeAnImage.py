from clearcontrol.devices.lasers import LaserDeviceInterface;
from clearcontrol.microscope.lightsheet.imaging import DirectImage;
from clearcontrol.microscope.lightsheet.imaging import DirectImageStack;
from net.clearcontrol.easyscopy import EasyScopyUtilities;
from net.clearcontrol.easyscopy.lightsheet.implementations.xwing import SimulatedXWingScope;
from net.imglib2 import RandomAccessibleInterval;
from net.imglib2.img.display.imagej import ImageJFunctions;
from net.imglib2.type.numeric.integer import UnsignedShortType;


# The XWingScope is an instance of EasyLightSheetMicroscope
lScope = SimulatedXWingScope.getInstance();

# Turn on a laser
lLaser = lScope.getDevice("Laser", "488");
lLaser.setTargetPowerInPercent(10);
lLaser.setLaserOn(True);
lLaser.setLaserPowerOn(True);
lLaser.setLaserOn(True);
lLaser.setLaserPowerOn(True);


# Take an image
lImage = lScope.getDirectImage();
lImage.setImageWidth(1024);
lImage.setImageHeight(2048);
lImage.setIlluminationZ(25);
lImage.setDetectionZ(25);

# start acquisition
img = EasyScopyUtilities.stackToImg(lImage.acquire());

# show the images
ImageJFunctions.show(img);
IJ.run("Enhance Contrast", "saturated=0.35");


# take an imagestack
lImageStack = lScope.getDirectImageStack();
lImageStack.setImageWidth(1024);
lImageStack.setImageHeight(2048);
lImageStack.setIlluminationZ(25);
lImageStack.setDetectionZ(25);
lImageStack.setNumberOfRequestedImages(10);
lImageStack.setDetectionZStepDistance(1);
lImageStack.setIlluminationZStepDistance(1);

# start acquisition
imgStack = EasyScopyUtilities.stackToImg(lImageStack.acquire());

# show the images
ImageJFunctions.show(imgStack);
IJ.run("Enhance Contrast", "saturated=0.35");


# That's always a godd idea by the end!
lScope.shutDownAllLasers();

# bye bye
# XWingScope.cleanup();



