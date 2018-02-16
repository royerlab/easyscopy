from  net.clearcontrol.easyscopy import EasyScopy;
from ij import IJ;

IJ.log("Supported microscopes:");
for lMicroscopeClass in EasyScopy.listEasyScopes():
    IJ.log(" * " + str(lMicroscopeClass));
    