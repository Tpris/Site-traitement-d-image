package imageprocessing;

import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ImageProcessing {

    public static void contoursImage(Planar<GrayU8> image) {
        int nbCanaux = image.getNumBands();
        if (nbCanaux != 1)
          ColorProcessing.RGBtoGray(image);
        Planar<GrayU8> input = image.clone();
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.gradientImageSobel(input.getBand(i), image.getBand(i));
        }
      }
    
      public static void luminositeImage(Planar<GrayU8> input, int delta) {
        int nbCanaux = input.getNumBands();
        for (int i = 0; i < nbCanaux; ++i) {
          GrayLevelProcessing.luminosite(input.getBand(i), delta);
        }
      }
    
      public static void filter(Planar<GrayU8> input, float h, float smin, float smax) {
        int nbCanaux = input.getNumBands();
        int[] rgb = new int[3];
        float[] hsv = new float[3];
    
        if(nbCanaux==1){
          GrayU8[] bands = new GrayU8[3];
          for(int i = 0; i<3;++i) bands[i] = input.getBand(0).clone();
          input.setBands(bands);
        }
        nbCanaux = input.getNumBands();
    
        for (int y = 0; y < input.height; ++y) {
          for (int x = 0; x < input.width; ++x) {
            if (nbCanaux == 3) {
                ColorProcessing.rgbToHsv(input.getBand(0).get(x, y),
                       input.getBand(1).get(x, y),
                       input.getBand(2).get(x, y), hsv);
    
              if(hsv[1]<smin) hsv[1]=smin;
              else if (hsv[1]>smax) hsv[1]=smax;
    
              ColorProcessing.hsvToRgb(h, hsv[1], hsv[2], rgb);
    
              for (int i = 0; i < 3; ++i) {
                input.getBand(i).set(x, y, rgb[i]);
              }
            } else
              System.err.println("error : unsupported type");
    
          }
        }
      }

      public static void meanFilterWithBorders(Planar<GrayU8> image, int size, BorderType borderType) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.meanFilterWithBorders(input.getBand(i), image.getBand(i), size, borderType);
        }
      }
    
      public static void convolutionColor(Planar<GrayU8> image, int size, double sigma) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.flouGaussien(input.getBand(i), image.getBand(i), size, sigma);
        }
      }

      public static void egalisationV(Planar<GrayU8> input) {
        int nbCanaux = input.getNumBands();
        if(nbCanaux==1){
          GrayU8[] bands = new GrayU8[3];
          for(int i = 0; i<3;++i) bands[i] = input.getBand(0).clone();
          input.setBands(bands);
        }
        nbCanaux = input.getNumBands();
        if(nbCanaux==3) ColorProcessing.egalisationColorV(input);
        else System.err.println("error : unsupported type");
      }

      public static void egalisationS(Planar<GrayU8> input) {
        int nbCanaux = input.getNumBands();
        if(nbCanaux==1){
          GrayU8[] bands = new GrayU8[3];
          for(int i = 0; i<3;++i) bands[i] = input.getBand(0).clone();
          input.setBands(bands);
        }
        nbCanaux = input.getNumBands();
        if(nbCanaux==3) ColorProcessing.egalisationColorS(input);
        else System.err.println("error : unsupported type");
      }
}
