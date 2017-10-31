#@UiService ui

from xwing.main import XWingMain

xwingmain = XWingMain.getInstance()

microscope = xwingmain.getLightSheetMicroscope

from clearcontrol.microscope.lightsheet.extendeddepthfield import FocusableImager

imager = FocusableImager(microscope, 0, 0, 1)
imager.setFieldOfView(256, 256)
imager.addImageRequest(100, 100)
stack = imager.execute()

from clearcontrol.stack.imglib2 import StackToImgConverte
img = StackToImgConverter(stack).getRandomAccessibleInterval()

ui.show(img)
