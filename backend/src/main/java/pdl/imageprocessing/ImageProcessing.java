package pdl.imageprocessing;

import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ImageProcessing {

  private static Planar<GrayU8> grayToPlanarRGB(Planar<GrayU8> input) {
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
    input = grayToPlanarRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux == 3)
      ColorProcessing.equalizationColorV(input);
    else
      System.err.println("error : unsupported type");
  }

  public static void egalisationS(Planar<GrayU8> input) {
    input = grayToPlanarRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux == 3)
      ColorProcessing.equalizationColorS(input);
    else
      System.err.println("error : unsupported type");
  }

  public static void sobelImage(Planar<GrayU8> image, boolean color) {
    int nbCanaux = image.getNumBands();
    if (nbCanaux != 1 && !color)
      ColorProcessing.RGBtoGray(image);
    Planar<GrayU8> input = image.clone();
    for (int i = 0; i < nbCanaux; ++i) {
      Convolution.gradientImageSobel(input.getBand(i), image.getBand(i));
    }
  }

  public static void luminosityImage(Planar<GrayU8> input, int delta) {
    if (delta >= -255 && delta <= 255) {
      int nbCanaux = input.getNumBands();
      for (int i = 0; i < nbCanaux; ++i) {
        GrayLevelProcessing.luminosity(input.getBand(i), delta);
      }
    } else
      System.err.println("** error : the parameter must be between -255 and 255");
  }

  public static void meanFilterWithBorders(Planar<GrayU8> image, int size, BorderType borderType) {
    if (size % 2 == 1) {
      if (size < image.height && size < image.width) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.meanFilterWithBorders(input.getBand(i), image.getBand(i), size, borderType);
        }
      } else
        System.err.println("** error : the size of the kernel is too large");
    } else
      System.err.println("** error : the kernel size must be odd");
  }

  public static void gaussianBlur(Planar<GrayU8> image, int size, float sigma, BorderType borderType) {
    if (size % 2 == 1) {
      if (size < image.height && size < image.width) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        double[][] kernel = Convolution.gaussianKernel(size, sigma);
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.gaussianBlurGrayU8(input.getBand(i), image.getBand(i), size, sigma, kernel, borderType);
        }
      } else
        System.err.println("** error : the size of the kernel is too large");
    } else
      System.err.println("** error : the kernel size must be odd");
  }

  public static void filter(Planar<GrayU8> input, float h, float smin, float smax) {
    if (h >= 0 && h <= 360) {
      if (smin <= smax && smin >= 0 && smin <= 1 && smax >= 0 && smax <= 1) {
        input = grayToPlanarRGB(input);
        int nbCanaux = input.getNumBands();

        for (int y = 0; y < input.height; ++y) {
          for (int x = 0; x < input.width; ++x) {
            if (nbCanaux == 3) {
              ColorProcessing.filter(input, h, smin, smax, x, y);
            } else
              System.err.println("error : unsupported type");
          }
        }
      } else
        System.err.println("** error : smin must be inferior or equal to smax and they must be between 0 and 1");
    } else
      System.err.println("** error : hue must be between 0 and 360");
  }

}
