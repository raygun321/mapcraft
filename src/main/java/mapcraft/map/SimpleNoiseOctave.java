/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcraft.map;

/**
 *
 * @author rmalot
 */
public class SimpleNoiseOctave extends SimplexNoise {
      public static float[][] generateSimplexNoise(int width, int height){
      float[][] simplexnoise = new float[width][height];
      float frequency = 5.0f / (float) width;
      
      for(int x = 0; x < width; x++){
         for(int y = 0; y < height; y++){
            simplexnoise[x][y] = (float) noise(x * frequency,y * frequency);
            simplexnoise[x][y] = (simplexnoise[x][y] + 1) / 2;   //generate values between 0 and 1
         }
      }
      
      return simplexnoise;
   }
      
    public double noiseWithFreq(double x, double y, double frequency) {
        return noise(x / frequency, y / frequency);
    }

    public double noiseWithFreq(double x, double y, double z, double frequency) {
        return noise(x / frequency, y / frequency, z / frequency);
    }

    public double noiseWithFreq(double x, double y, double z, double w, double frequency) {
        return noise(x / frequency, y / frequency, z / frequency, w / frequency);
    }
    
// Example
//    public double octavedNoise(double x, double y) {
//        return
//            (noiseWithFreq(x, y, 1f) * 1 + // Each of this lines is called an "octave".
//            noiseWithFreq(x, y, 2f) * 2 +  // The whole expression makes up a weighted average computation
//            noiseWithFreq(x, y, 4f) * 4 +  // where the noise with the lowest frequencies have the least effect.
//            noiseWithFreq(x, y, 8f) * 8 +
//            noiseWithFreq(x, y, 16f) * 16) / (1 + 2 + 4 + 8 + 16);
//    }

    public double octavedNoise(double x, double y, int octaves, double roughness, double scale) {
        double noiseSum = 0;
        double layerFrequency = scale;
        double layerWeight = 1;
        double weightSum = 0;

        for (int octave = 0; octave < octaves; octave++) {
            noiseSum += noise(x * layerFrequency, y * layerFrequency) * layerWeight;
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;
        }
        return noiseSum / weightSum;
    }
    
    public double octavedNoise(double x, double y, double z, int octaves, double roughness, double scale) {
        double noiseSum = 0;
        double layerFrequency = scale;
        double layerWeight = 1;
        double weightSum = 0;

        for (int octave = 0; octave < octaves; octave++) {
            noiseSum += noise(x * layerFrequency, y * layerFrequency, z * layerFrequency) * layerWeight; 
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;
        }
        return noiseSum / weightSum;
    }
        
    public double octavedNoise(double x, double y, double z, double w, int octaves, double roughness, double scale) {
        double noiseSum = 0;
        double layerFrequency = scale;
        double layerWeight = 1;
        double weightSum = 0;

        for (int octave = 0; octave < octaves; octave++) {
            noiseSum += noise(x * layerFrequency, y * layerFrequency, z * layerFrequency, w * layerFrequency) * layerWeight;
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;
        }
        return noiseSum / weightSum;
    }
}
