package imageprocessing;

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

  public static void sobelImage(Planar<GrayU8> image, boolean color) {
    int nbCanaux = image.getNumBands();
    if (nbCanaux != 1 && !color)
      ColorProcessing.RGBtoGray(image);
    Planar<GrayU8> input = image.clone();
    for (int i = 0; i < nbCanaux; ++i) {
      Convolution.gradientImageSobel(input.getBand(i), image.getBand(i));
    }
  }

  public static void dynamicContast(Planar<GrayU8> image, int min, int max){
    int nbCanaux = image.getNumBands();
    //if(nbCanaux!=1 && nbCanaux!=3) return error
    int minHisto, maxHisto;
    if (nbCanaux == 3){
      Planar<GrayU8> input = image.clone();
      ColorProcessing.RGBtoGray(input);
      minHisto = GrayLevelProcessing.min(input.getBand(0));
      maxHisto = GrayLevelProcessing.max(input.getBand(0));
    } else {
      minHisto = GrayLevelProcessing.min(image.getBand(0));
      maxHisto = GrayLevelProcessing.max(image.getBand(0));
    }
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.contrastLUT(image.getBand(i), min, max, minHisto, maxHisto);
    //return good
  }

  public static void egalisationRGB(Planar<GrayU8> image) {
    int nbCanaux = image.getNumBands();
    //if(nbCanaux!=1 && nbCanaux!=3) return error
    int[] histoCum;
    if (nbCanaux == 3){
      Planar<GrayU8> input = image.clone();
      ColorProcessing.RGBtoGray(input);
      histoCum = GrayLevelProcessing.histogramCumul(input.getBand(0));
    } else histoCum = GrayLevelProcessing.histogramCumul(image.getBand(0));
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.egalisation(image.getBand(i), histoCum);
    //return good
  }

  public static void threshold(Planar<GrayU8> image, int t) {
      //if(t<0 || t>255) return error
      int nbCanaux = image.getNumBands();
      for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.threshold(image.getBand(i), t);
      //return good
  }

  public static void RGBtoGray(Planar<GrayU8> image) {
    int nbCanaux = image.getNumBands();
    //if(nbCanaux != 3) return error
    ColorProcessing.RGBtoGray(image);
    //return good
  }

  public static void invImage(Planar<GrayU8> image){
    int nbCanaux = image.getNumBands();
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.reverse(image.getBand(i));
    //return good
  }

}
