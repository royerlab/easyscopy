from keras.models import load_model
import numpy as np
import os

class CalibbrationModel:

    def __init__(self):
        self.field_generate = np.loadtxt("/Users/dsaha/Desktop/Zernike.txt")
        self.field_generate = self.field_generate.reshape((1,63))
        #print(self.field_generate)

    def model_predict(self):
        m = load_model("/Users/dsaha/Desktop/20180409")
        a = m.predict(self.field_generate)
        if os.path.exists("/Users/dsaha/Desktop/Actuator.txt"):
            os.remove("/Users/dsaha/Desktop/Actuator.txt")
        np.savetxt("/Users/dsaha/Desktop/Actuator.txt",a)


#field_generate = np.zeros((1,63))
#field_generate[0,4] = +0.2


CalibbrationModel().model_predict()

