# EasyScopy
Fiji plugin for microscope control scripting. 

After installation of the easyscopy.jar and its dependencies, you can communicate to your microscope and take images from Fiji scripts. However, your microscope should be controlled by [ClearControl](http://github.com/ClearControl). To play with the library, there are simulated microscopes available.

Example code for taking a single image:
```
# initialize the microscope "hardware"
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

# properly close all device drivers and say bye bye
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

## Installation

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

Run your Fiji and update it. "jython.jar" may be marked as "Locally modified". Mark it as "Uninstall" and perform the update.

Take care: EasyScopy is in early developmental stage. Installing it to your Fiji may harm your Fiji installation as it brings many dependencies which may be incompatible with other plugins. It is recommended not to work in a production environment.

# Trouble shooting
## Various Python error messages after installation to Fiji
If you get error messages, like `Package __site__ not found`, update your Fiji installation. If the Fiji Updater lists libraries as `locally modified` update them by selecting them as `Uninstall`.

## OpenCL error messages on MacOS
When installing EasyScopy on a recent MacOS, you might get OpenCL error messages after the first run. In that case, the default OpenCL device is not supported. After the first run, you will find a `.clearcontrol` directory within your home directory. Navigate to that folder and open the `configuration.txt` and enter these two lines:
```
clearcl.device.fusion = GPU
clearcl.device.simulation = GPU
```

Replace `GPU` with the name (or a part of the name) of your favorite installed OpenCL device (for example `CPU` or `HD` or `AMD`). You can get a list of all GPU devices by running this jython script:

```
from clearcl.imagej import ClearCLIJ;
from ij import IJ;
IJ.log(ClearCLIJ.clinfo());
```

See more information on GPU compatibility on [ClearCL](https://github.com/ClearVolume/ClearCL) and [ClearCLIJ](https://github.com/ClearControl/ClearCLIJ) github pages.


