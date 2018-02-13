# EasyScopy
Fiji plugin for microscope control scripting. 

After installation of the easyscopy.jar and its dependencies, you can communicate to your microscope and take images from Fiji scripts. However, your microscope should be controlled by ClearControl (http://github.com/ClearControl). To play with the library, there are simulated microscopes available.

```
lScope = SimulatedXWingScope.getInstance();

# Turn on a laser
lLaser = lScope.getDevice("Laser", "488");
lLaser.setTargetPowerInPercent(20);
lLaser.setLaserOn(True);
lLaser.setLaserPowerOn(True);

# Take an image
lImage = lScope.getDirectImage();
lImage.setIlluminationZ(25);
lImage.setDetectionZ(25);

# start acquisition
lTakenImage = lImage.acquire()

# convert and show
lImglib2Image = EasyScopyUtilities.stackToImg(lTakenImage);
ImageJFunctions.show(lImglib2Image);

# That's always a godd idea by the end!
lScope.shutDownAllLasers();

# bye bye
XWingScope.cleanup();
```

To get a list of supported microscopes run this jython scipt from Fijis script editor:
```
from  net.clearcontrol.easyscopy import EasyScopy;
from ij import IJ;

IJ.log("Supported microscopes:");
for lMicroscopeClass in EasyScopy.listEasyScopes():
    IJ.log(" * " + str(lMicroscopeClass));
```

## installation

Clone this repo
```
git clone https://github.com/ClearControl/EasyScopy
```

Open pom.xml and enter the path of your Fiji installation in the line containing

```
<imagej.app.directory>C:/path/to/Fiji.app
```

Go to the source dir and deploy to your Fiji.app

```
cd EasyScopy
deploy.bat
```

Take care: EasyScopy is in early developmental stage. Installing it to your Fiji may harm your Fiji installation as it brings many dependencies which may be incompatible with other plugins. It is recommended not to work in a production environment.
