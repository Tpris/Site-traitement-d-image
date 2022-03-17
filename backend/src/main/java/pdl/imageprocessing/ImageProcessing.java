package pdl.imageprocessing;

import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ImageProcessing {

  private static Planar<GrayU8> grayToRGB(Planar<GrayU8> input) {
    int nbCanaux = input.getNumBands();
    if (nbCanaux == 1) {
      GrayU8[] bands = new GrayU8[3];
      for (int i = 0; i < 3; ++i)
        bands[i] = input.getBand(0).clone();
      input.setBands(bands);
    }
    return input;
  }
  
  public static void egalisationV(Planar<GrayU8> input) {
    input = grayToRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux == 3)
      ColorProcessing.egalisationColorV(input);
    else
      System.err.println("error : unsupported type");
  }

  public static void egalisationS(Planar<GrayU8> input) {
    input = grayToRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux == 3)
      ColorProcessing.egalisationColorS(input);
    else
      System.err.println("error : unsupported type");
  }

  public static void contoursImage(Planar<GrayU8> image) {
    int nbCanaux = image.getNumBands();
    if (nbCanaux != 1)
      ColorProcessing.RGBtoGray(image);
    Planar<GrayU8> input = image.clone();
    for (int i = 0; i < nbCanaux; ++i) {
      Convolution.gradientImageSobel(input.getBand(i), image.getBand(i));
    }
  }

  public static void luminositeImage(Planar<GrayU8> input, double delta) {
    int deltaN = (int) delta;
    int nbCanaux = input.getNumBands();
    for (int i = 0; i < nbCanaux; ++i) {
      GrayLevelProcessing.luminosite(input.getBand(i), deltaN);
    }
  }

  public static void meanFilterWithBorders(Planar<GrayU8> image, double size, BorderType borderType) {
    int sizeN = (int)size;
    int nbCanaux = image.getNumBands();
    Planar<GrayU8> input = image.clone();
    for (int i = 0; i < nbCanaux; ++i) {
      Convolution.meanFilterWithBorders(input.getBand(i), image.getBand(i), sizeN, borderType);
    }
  }

  public static void flouGaussien(Planar<GrayU8> image, double size, double sigma, BorderType borderType) {
    int sizeN = (int) size;
    int sigmaN = (int) sigma;
    if (sizeN % 2 == 1) {
      int nbCanaux = image.getNumBands();
      Planar<GrayU8> input = image.clone();
      double [][]kernel = Convolution.gaussianKernel(sizeN, sigmaN);
      for (int i = 0; i < nbCanaux; ++i) {
        Convolution.flouGaussienGrayU8(input.getBand(i), image.getBand(i), sizeN, kernel);
      }
    }
  }

  public static void filter(Planar<GrayU8> input, double h, double smin, double smax) {
    int[] rgb = new int[3];
    double[] hsv = new double[3];

    input = grayToRGB(input);
    int nbCanaux = input.getNumBands();

    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        if (nbCanaux == 3) {
          ColorProcessing.rgbToHsv(input.getBand(0).get(x, y),
              input.getBand(1).get(x, y),
              input.getBand(2).get(x, y), hsv);

          if (hsv[1] < smin)
            hsv[1] = smin;
          else if (hsv[1] > smax)
            hsv[1] = smax;

          ColorProcessing.hsvToRgb(h, hsv[1], hsv[2], rgb);

          for (int i = 0; i < 3; ++i) {
            input.getBand(i).set(x, y, rgb[i]);
          }
        } else
          System.err.println("error : unsupported type");

      }
    }
  }

}
