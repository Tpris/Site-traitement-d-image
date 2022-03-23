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

}
