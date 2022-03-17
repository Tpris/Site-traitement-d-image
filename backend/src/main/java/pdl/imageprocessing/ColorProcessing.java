package pdl.imageprocessing;

import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import boofcv.alg.color.ColorHsv;

public class ColorProcessing {

  public static void luminositeColor(Planar<GrayU8> input, int delta) {
    for (int i = 0; i < 3; ++i) {
      GrayLevelProcessing.luminosite(input.getBand(i), delta);
    }
  }

  public static void convolutionColor(Planar<GrayU8> input, Planar<GrayU8> output, int[][] kernel) {
    for (int i = 0; i < 3; ++i) {
      Convolution.convolution(input.getBand(i), output.getBand(i), kernel);
    }
  }

  public static void meanFilterColorWithBorders(Planar<GrayU8> input, Planar<GrayU8> output, int size, BorderType borderType) {
    for (int i = 0; i < 3; ++i) {
      Convolution.meanFilterWithBorders(input.getBand(i), output.getBand(i), size, borderType);
    }
  }

  public static void RGBtoGray(Planar<GrayU8> input) {
    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        int val = (int) (input.getBand(0).get(x, y) * 0.3 +
            input.getBand(1).get(x, y) * 0.59 +
            input.getBand(2).get(x, y) * 0.11);
        for (int i = 0; i < 3; ++i) {
          input.getBand(i).set(x, y, val);
        }
      }
    }
  }


  public static void rgbToHsv(int r, int g, int b, double[] hsv) {
    hsv[2] = Math.max(r, Math.max(g, b));
    float min = Math.min(r, Math.min(g, b));
    hsv[1] = (hsv[2] == 0) ? 0 : 1 - (min / hsv[2]);
    if (hsv[2] == min)
      hsv[0] = 0;
    else if (hsv[2] == r)
      hsv[0] = (60 * (g - b) / (hsv[2] - min) + 360) % 360;
    else if (hsv[2] == g)
      hsv[0] = 60 * (b - r) / (hsv[2] - min) + 120;
    else
      hsv[0] = 60 * (r - g) / (hsv[2] - min) + 240;
  }

  public static void hsvToRgb(double h, double s, double v, int[] rgb) {
    int ti = (int) (Math.floor(h / 60)) % 6;
    double f = h / 60 - ti;
    int l = (int) (v * (1 - s));
    int m = (int) (v * (1 - f * s));
    int n = (int) (v * (1 - (1 - f) * s));
    switch (ti) {
      case 0:
        rgb[0] = (int) v;
        rgb[1] = n;
        rgb[2] = l;
        break;
      case 1:
        rgb[0] = m;
        rgb[1] = (int) v;
        rgb[2] = l;
        break;
      case 2:
        rgb[0] = l;
        rgb[1] = (int) v;
        rgb[2] = n;
        break;
      case 3:
        rgb[0] = l;
        rgb[1] = m;
        rgb[2] = (int) v;
        break;
      case 4:
        rgb[0] = n;
        rgb[1] = l;
        rgb[2] = (int) v;
        break;
      case 5:
        rgb[0] = (int) v;
        rgb[1] = l;
        rgb[2] = m;
        break;

    }
  }

  public static void filter(Planar<GrayU8> input, double h) {
    int[] rgb = new int[3];
    double[] hsv = new double[3];
    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        rgbToHsv(input.getBand(0).get(x, y),
            input.getBand(1).get(x, y),
            input.getBand(2).get(x, y), hsv);
        hsvToRgb(h, hsv[1], hsv[2], rgb);
        for (int i = 0; i < 3; ++i) {
          input.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  public static void egalisationRGB(Planar<GrayU8> image) {
    Planar<GrayU8> input = image.clone();
    RGBtoGray(input);
    int[] egal = new int[256];
    int[] histoCumul = GrayLevelProcessing.histogramCumul(input.getBand(0));
    for (int i = 0; i < 256; i++) {
      egal[i] = histoCumul[i] * 255 / (input.height * input.width);
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        for (int i = 0; i < 3; ++i) {
          image.getBand(i).set(x, y, egal[image.getBand(i).get(x, y)]);
        }
      }
    }
  }

  public static void egalisationColorV(Planar<GrayU8> image) {
    int[] egal = new int[256];
    int[] histoCumul = histogramCumulV(image);
    for (int i = 0; i < 256; i++) {
      egal[i] = histoCumul[i] * 255 / (image.height * image.width);
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
        double[] hsv = new double[3];
        rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
        hsvToRgb(hsv[0], hsv[1], egal[(int) hsv[2]], rgb);
        for (int i = 0; i < 3; i++) {
          image.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  public static void egalisationColorS(Planar<GrayU8> image) {
    // egalisationColorHSV(image, histogramCumulS(image));
    double[] egal = new double[101];
    int[] histoCumul = histogramCumulS(image);
    for (int i = 0; i < 101; i++) {
      egal[i] = histoCumul[i] * 100 / (image.height * image.width);
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
        double[] hsv = new double[3];
        rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
        // System.out.println(hsv[1]+" "+egal[(int) (hsv[1]*100)]/100);
        hsvToRgb(hsv[0], egal[(int) (hsv[1]*100)]/100, hsv[2], rgb);
        for (int i = 0; i < 3; i++) {
          image.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  public static void contrastColor(Planar<GrayU8> image, int min, int max) {
    for (int i = 0; i < 3; ++i) {
      GrayLevelProcessing.contrast(image.getBand(i), min, max);
    }
  }

  private static double[] getHSVfromRgb(int x, int y, Planar<GrayU8> image) {
    int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
    double[] hsv = new double[3];
    rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
    return hsv;
  }

  private static double getVfromRgb(int x, int y, Planar<GrayU8> image) {
    double[] hsv = getHSVfromRgb(x, y, image);
    return hsv[2];
  }

  private static double getSfromRgb(int x, int y, Planar<GrayU8> image) {
    double[] hsv = getHSVfromRgb(x, y, image);
    return hsv[1];
  }

  public static double minV(Planar<GrayU8> image) {
    double min = 255;
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getVfromRgb(x, y, image);
        if (min > v)
          min = v;
      }
    }
    return min;
  }

  public static double maxV(Planar<GrayU8> image) {
    double max = 0;
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getVfromRgb(x, y, image);
        if (max < v)
          max = v;
      }
    }
    return max;
  }

  public static double minS(Planar<GrayU8> image) {
    double min = 255;
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getSfromRgb(x, y, image);
        if (min > v)
          min = v;
      }
    }
    return min;
  }

  public static double maxS(Planar<GrayU8> image) {
    double max = 0;
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getSfromRgb(x, y, image);
        if (max < v)
          max = v;
      }
    }
    return max;
  }

  public static int[] histogramV(Planar<GrayU8> image) {
    int values[] = new int[256];
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getVfromRgb(x, y, image);
        values[(int) v]++;
      }
    }
    return values;
  }

  public static int[] histogramS(Planar<GrayU8> image) {
    int values[] = new int[101];
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        double v = getSfromRgb(x, y, image);
        int test = (int) (v*100);
        values[test]++;
      }
    }
    return values;
  }

  public static int[] histogramCumulHSV(Planar<GrayU8> input, int[] hist, int tailleTab) {
    int[] histo = hist;
    int histoCum[] = new int[tailleTab];
    histoCum[0] = histo[0];
    for (int i = 1; i < tailleTab; i++) {
      histoCum[i] = histo[i] + histoCum[i - 1];
    }
    return histoCum;
  }

  public static int[] histogramCumulV(Planar<GrayU8> input) {
    return histogramCumulHSV(input, histogramV(input), 256);
  }

  public static int[] histogramCumulS(Planar<GrayU8> input) {
    return histogramCumulHSV(input, histogramS(input), 101);
  }

  public static void contrastHSV(Planar<GrayU8> image, int min, int max) {
    double minHisto = minV(image);
    double maxHisto = maxV(image);
    double LUT[] = new double[256];
    for (int i = 0; i < 256; i++) {
      LUT[i] = ((max - min) * (i - minHisto) / (maxHisto - minHisto)) + min;
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
        double[] hsv = new double[3];
        rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
        hsvToRgb(hsv[0], hsv[1], LUT[(int) hsv[2]], rgb);
        for (int i = 0; i < 3; i++) {
          image.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  public static void contrastLUTColor(Planar<GrayU8> input, int min, int max) {
    Planar<GrayU8> in = input.clone();
    RGBtoGray(in);
		int minHisto = GrayLevelProcessing.min(in.getBand(0));
		int maxHisto = GrayLevelProcessing.max(in.getBand(0));
    System.out.println("min = "+minHisto+" max = "+maxHisto);
		if (min > max) {
			System.err.println("min est superieur à max");
		} else if (minHisto == maxHisto) {
			System.err.println("un contraste ne peut pas être appliqué à une image à couleur unie");
		} else {
			int LUT[] = new int[256];
			for (int i = 0; i < 256; i++) {
				LUT[i] = ((max - min) * (i - minHisto) / (maxHisto - minHisto)) + min;
        if(LUT[i]>max) LUT[i] = max;
        else if(LUT[i]<min) LUT[i] = min;
			}
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
          for(int i = 0; i<3;++i)
					  input.getBand(i).set(x, y, LUT[input.getBand(i).get(x, y)]);
				}
			}
		}
	}

  private static void testConversionHSV(int r, int g, int b) {
    int[] rgb = { r, g, b };
    double[] hsv = new double[3];
    System.out.println("*** fonction à tester ***");
    System.out.println("- rgb to hsv");
    // RGBtoHSV(rgb[0], rgb[1], rgb[2], hsv);
    rgbToHsv(r, g, b, hsv);
    for (double f : hsv)
      System.out.println(f);
    System.out.println("- hsv to rgb");
    hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
    for (int i : rgb)
      System.out.println(i);
    System.out.println("*** ColorHsv ***");
    rgb[0] = r; rgb[1]= g; rgb[2]=b;
    System.out.println("- rgb to hsv");
    ColorHsv.rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
    for (double f : hsv)
      System.out.println(f);
    System.out.println("- hsv to rgb");
    ColorHsv.hsvToRgb(hsv[0], hsv[1], hsv[2]);
    for (float f : rgb)
      System.out.println(f);
  }

}