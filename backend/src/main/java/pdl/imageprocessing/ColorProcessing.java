package pdl.imageprocessing;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

class ColorProcessing {

  static void RGBtoGray(Planar<GrayU8> input) {
    if (input.getNumBands() == 3) {
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
  }

  private static void rgbToHsv(int r, int g, int b, float[] hsv) {
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

  private static void setRgb(int[] rgb, int r, int g, int b){
    rgb[0] = r;
    rgb[1] = g;
    rgb[2] = b;
  }

  private static void hsvToRgb(float h, float s, float v, int[] rgb) {
    int ti = (int) (Math.floor(h / 60)) % 6;
    float f = h / 60 - ti;
    int l = (int) (v * (1 - s));
    int m = (int) (v * (1 - f * s));
    int n = (int) (v * (1 - (1 - f) * s));
    int vInt = (int) v;
    switch (ti) {
      case 0: setRgb(rgb, vInt, n, l);
          break;
      case 1: setRgb(rgb, m, vInt, l);
          break;
      case 2: setRgb(rgb, l, vInt, n);
          break;
      case 3: setRgb(rgb, l, m, vInt);
          break;
      case 4: setRgb(rgb, n, l, vInt);
          break;
      case 5: setRgb(rgb, vInt, l, m);
          break;
    }
  }

  static void filter(Planar<GrayU8> input, float h, float smin, float smax, int x, int y) {
    int[] rgb = new int[3];
    float[] hsv = new float[3];
    rgbToHsv(input.getBand(0).get(x, y),
              input.getBand(1).get(x, y),
              input.getBand(2).get(x, y), hsv);

    if (hsv[1] < smin)
      hsv[1] = smin;
    else if (hsv[1] > smax)
      hsv[1] = smax;
    
    hsvToRgb(h, hsv[1], hsv[2], rgb);
    for (int i = 0; i < 3; ++i) {
      input.getBand(i).set(x, y, rgb[i]);
    }
  }

  static void equalizationColorV(Planar<GrayU8> image) {
    int[] egal = new int[256];
    int[] histoCumul = histogramCumulV(image);
    for (int i = 0; i < 256; i++) {
      egal[i] = histoCumul[i] * 255 / (image.height * image.width);
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
        float[] hsv = new float[3];
        rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
        hsvToRgb(hsv[0], hsv[1], egal[Math.round(hsv[2])], rgb);
        for (int i = 0; i < 3; i++) {
          image.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  static void equalizationColorS(Planar<GrayU8> image) {
    float[] egal = new float[101];
    int[] histoCumul = histogramCumulS(image);
    egal[0]=0;
    for (int i = 1; i < 101; i++) {
      egal[i] = histoCumul[i] * 100 / (image.height * image.width);
    }
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
        float[] hsv = new float[3];
        rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
        hsvToRgb(hsv[0], egal[(int) (hsv[1] * 100)] / 100, hsv[2], rgb);
        for (int i = 0; i < 3; i++) {
          image.getBand(i).set(x, y, rgb[i]);
        }
      }
    }
  }

  private static int[] histogramCumulHSV(Planar<GrayU8> input, int[] hist, int tailleTab) {
    int[] histo = hist;
    int histoCum[] = new int[tailleTab];
    histoCum[0] = histo[0];
    for (int i = 1; i < tailleTab; i++) {
      histoCum[i] = histo[i] + histoCum[i - 1];
    }
    return histoCum;
  }

  private static int[] histogramCumulV(Planar<GrayU8> input) {
    return histogramCumulHSV(input, histogramV(input), 256);
  }

  private static int[] histogramCumulS(Planar<GrayU8> input) {
    return histogramCumulHSV(input, histogramS(input), 101);
  }

  private static int[] histogramV(Planar<GrayU8> image) {
    int values[] = new int[256];
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        float v = getVfromRgb(x, y, image);
        values[Math.round(v)]++;
      }
    }
    return values;
  }

  private static int[] histogramS(Planar<GrayU8> image) {
    int values[] = new int[101];
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        float s = getSfromRgb(x, y, image);
        values[Math.round(s*100)]++;
      }
    }
    return values;
  }

  private static float[] getHSVfromRgb(int x, int y, Planar<GrayU8> image) {
    int rgb[] = { image.getBand(0).get(x, y), image.getBand(1).get(x, y), image.getBand(2).get(x, y) };
    float[] hsv = new float[3];
    rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
    return hsv;
  }

  private static float getVfromRgb(int x, int y, Planar<GrayU8> image) {
    float[] hsv = getHSVfromRgb(x, y, image);
    return hsv[2];
  }

  private static float getSfromRgb(int x, int y, Planar<GrayU8> image) {
    float[] hsv = getHSVfromRgb(x, y, image);
    return hsv[1];
  }
}