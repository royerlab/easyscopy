package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import java.io.*;
import java.util.concurrent.TimeUnit;


public class DMControl {
    public static void main(String args[]){
        try {
            String line = null;
            ProcessBuilder pb = new ProcessBuilder("python","/Users/dsaha/Codes/EasyScopy/src/main/java/net/clearcontrol/easyscopy/lightsheet/implementations/blindbscope/demo/DMCalibrationModel.py");
            Process p = pb.start();
            boolean done = p.waitFor(100000, TimeUnit.MILLISECONDS);

            String fileName = "/Users/dsaha/Desktop/Actuator.txt";
            FileReader fileReader =
                    new FileReader(fileName);
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

        }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
