package pdl.imageprocessing;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

  public static ResponseEntity<?> egalisationV(Planar<GrayU8> input) {
    input = grayToPlanarRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux != 3)
      return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
    ColorProcessing.equalizationColorV(input);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> egalisationS(Planar<GrayU8> input) {
    input = grayToPlanarRGB(input);
    int nbCanaux = input.getNumBands();
    if (nbCanaux != 3)
      return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
    ColorProcessing.equalizationColorS(input);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> sobelImage(Planar<GrayU8> image, boolean color) {
    int nbCanaux = image.getNumBands();
    if (nbCanaux != 1 && !color)
      ColorProcessing.RGBtoGray(image);
    Planar<GrayU8> input = image.clone();
    for (int i = 0; i < nbCanaux; ++i) {
      Convolution.gradientImageSobel(input.getBand(i), image.getBand(i));
    }
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> luminosityImage(Planar<GrayU8> input, int delta) {
    if (delta >= -255 && delta <= 255) {
      int nbCanaux = input.getNumBands();
      for (int i = 0; i < nbCanaux; ++i) {
        GrayLevelProcessing.luminosity(input.getBand(i), delta);
      }
      return new ResponseEntity<>("ok", HttpStatus.OK);
    } else
      return new ResponseEntity<>("the parameter must be between -255 and 255", HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> meanFilterWithBorders(Planar<GrayU8> image, int size, BorderType borderType) {
    if (size % 2 == 1) {
      if (size < image.height && size < image.width) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.meanFilterWithBorders(input.getBand(i), image.getBand(i), size, borderType);
        }
        return new ResponseEntity<>("ok", HttpStatus.OK);
      } else
        return new ResponseEntity<>("the size of the kernel is too large", HttpStatus.BAD_REQUEST);
    } else
      return new ResponseEntity<>("the kernel size must be odd", HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> gaussianBlur(Planar<GrayU8> image, int size, float sigma, BorderType borderType) {
    if (size % 2 == 1) {
      if (size < image.height && size < image.width) {
        int nbCanaux = image.getNumBands();
        Planar<GrayU8> input = image.clone();
        double[][] kernel = Convolution.gaussianKernel(size, sigma);
        for (int i = 0; i < nbCanaux; ++i) {
          Convolution.gaussianBlurGrayU8(input.getBand(i), image.getBand(i), size, sigma, kernel, borderType);
        }
        return new ResponseEntity<>("ok", HttpStatus.OK);
      } else
        return new ResponseEntity<>("the size of the kernel is too large", HttpStatus.BAD_REQUEST);
    } else
      return new ResponseEntity<>("the kernel size must be odd", HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> filter(Planar<GrayU8> input, float h, float smin, float smax) {
    if (h >= 0 && h <= 360) {
      if (smin <= smax && smin >= 0 && smin <= 1 && smax >= 0 && smax <= 1) {
        input = grayToPlanarRGB(input);
        int nbCanaux = input.getNumBands();
        if (nbCanaux == 3) {
          for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
              ColorProcessing.filter(input, h, smin, smax, x, y);
            }
          }
          return new ResponseEntity<>("ok", HttpStatus.OK);
        } else
          return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
      } else
        return new ResponseEntity<>("smin must be inferior or equal to smax and they must be between 0 and 1",
            HttpStatus.BAD_REQUEST);
    } else
      return new ResponseEntity<>("hue must be between 0 and 360", HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> rainbow(Planar<GrayU8> input, float smin, float smax) {
      if (smin <= smax && smin >= 0 && smin <= 1 && smax >= 0 && smax <= 1) {
        input = grayToPlanarRGB(input);
        int nbCanaux = input.getNumBands();
        if (nbCanaux == 3) {
          for (int y = 0; y < input.height; ++y) {
            float h = y*360/input.height;
            for (int x = 0; x < input.width; ++x) {
              ColorProcessing.filter(input, h, smin, smax, x, y);
            }
          }
          return new ResponseEntity<>("ok", HttpStatus.OK);
        } else
          return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
      } else
        return new ResponseEntity<>("smin must be inferior or equal to smax and they must be between 0 and 1",
            HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> dynamicContast(Planar<GrayU8> image, int min, int max){
    if(min>max) return new ResponseEntity<>("min must be less than max", HttpStatus.BAD_REQUEST);
    int nbCanaux = image.getNumBands();
    if(nbCanaux!=1 && nbCanaux!=3) return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
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
    if(minHisto==maxHisto) return new ResponseEntity<>("dynamic contrast cannot be applied to a solid image", HttpStatus.BAD_REQUEST);
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.contrastLUT(image.getBand(i), min, max, minHisto, maxHisto);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> egalisationRGB(Planar<GrayU8> image) {
    int nbCanaux = image.getNumBands();
    if(nbCanaux!=1 && nbCanaux!=3) return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
    int[] histoCum;
    if (nbCanaux == 3){
      Planar<GrayU8> input = image.clone();
      ColorProcessing.RGBtoGray(input);
      histoCum = GrayLevelProcessing.histogramCumul(input.getBand(0));
    } else histoCum = GrayLevelProcessing.histogramCumul(image.getBand(0));
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.egalisation(image.getBand(i), histoCum);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> threshold(Planar<GrayU8> image, int t) {
      if(t<0 || t>255) return new ResponseEntity<>("The threshold parameter must be between 0 and 255", HttpStatus.BAD_REQUEST);
      int nbCanaux = image.getNumBands();
      for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.threshold(image.getBand(i), t);
      return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> RGBtoGray(Planar<GrayU8> image) {
    int nbCanaux = image.getNumBands();
    if(nbCanaux == 1) return new ResponseEntity<>("ok", HttpStatus.OK);
    if(nbCanaux != 3) return new ResponseEntity<>("unsupported type", HttpStatus.BAD_REQUEST);
    ColorProcessing.RGBtoGray(image);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> negativeImage(Planar<GrayU8> image){
    int nbCanaux = image.getNumBands();
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.reverse(image.getBand(i));
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  private static void gradient(Planar<GrayU8> input, int x, int y, int step){
    int nbCanaux = input.getNumBands();
    int gl = input.getBand(0).get(x, y);
    int val;
    for(int i = 1; i<nbCanaux; ++i) {
      val = input.getBand(i).get(x, y);
      if (val>gl) gl = val;
    }
    val = gl;
    val += val%step;
    val = gl-val;
    for(int i = 0; i<nbCanaux; ++i) {
      int v = input.getBand(i).get(x, y)+val;
      if(v>255) v = 255; 
      else if(v<0) v = 0;
      input.getBand(i).set(x, y, v);
    }
  }

  public static ResponseEntity<?> draw(Planar<GrayU8> input, int step){
    if(step<1 || step>255) return new ResponseEntity<>("The step must be between 1 and 255", HttpStatus.BAD_REQUEST);
    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        gradient(input, x, y, step);
      }
    }
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> waterColor(Planar<GrayU8> input){
    int nbCanaux = input.getNumBands();
    Planar<GrayU8> contours = input.clone();
    sobelImage(contours, false);
    negativeImage(contours);
    for(int i = 0; i<nbCanaux; ++i){
      GrayLevelProcessing.threshold4step(input.getBand(i));
    }
    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        for(int i = 0; i<nbCanaux; ++i){
          int c = contours.getBand(i).get(x, y);
          int color = input.getBand(i).get(x, y);
          if(c<color) input.getBand(i).set(x, y,c);
        }
      }
    }
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> rotation(Planar<GrayU8> image, double theta){
    theta = theta*Math.PI/180;
    if(theta<-360 || theta>360) return new ResponseEntity<>("The angle of rotation must be between -360 and 360", HttpStatus.BAD_REQUEST);
    int nbCanaux = image.getNumBands();
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.rotate(image.getBand(i), theta);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> fisheyes(Planar<GrayU8> image, double delta, Perspective perspective){
    int nbCanaux = image.getNumBands();
    if(delta<0) return new ResponseEntity<>("delta must be positive", HttpStatus.BAD_REQUEST);
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.fisheyes(image.getBand(i), delta, perspective);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

  public static ResponseEntity<?> tourbillon(Planar<GrayU8> image, float tourbillonFactor){
    int nbCanaux = image.getNumBands();
    if(tourbillonFactor<0) return new ResponseEntity<>("the parameter must be positive", HttpStatus.BAD_REQUEST);
    for (int i = 0; i < nbCanaux; ++i)
        GrayLevelProcessing.tourbillon(image.getBand(i), tourbillonFactor);
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

}
