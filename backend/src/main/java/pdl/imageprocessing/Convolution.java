package pdl.imageprocessing;

import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;

class Convolution {

  public static void flouGaussienGrayU8(GrayU8 input, GrayU8 output, int size, double sigma, double[][] kernel,
      BorderType borderType) {
    if (size % 2 == 1) {
      int bord = (size - 1) / 2;
      BoofConcurrency.loopFor(0, input.height, y -> {
        for (int x = 0; x < input.width; ++x) {
          if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
            gaussBorderTreatement(x, y, bord, size, kernel, sigma, input, output, borderType);
          } else {
            int val = 0;
            for (int ky = -bord; ky <= bord; ky++) {
              for (int kx = -bord; kx <= bord; kx++) {
                val += input.get(x + kx, y + ky) * kernel[ky + bord][kx + bord];
              }
            }
            output.set(x, y, val);
          }
        }
      });
    } else {
      System.err.println("la taille du noyau doit être impair");
    }
  }

  public static double[][] gaussianKernel(int size, double sigma) {
    double[][] kernel = new double[size][size];
    int bord = (size - 1) / 2;
    double denominateur1 = 2 * sigma * sigma;
    double sumCoef = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      for (int kx = -bord; kx <= bord; kx++) {
        kernel[ky + bord][kx + bord] = Math.exp(-(((kx * kx) + (ky * ky)) / denominateur1));
        sumCoef += kernel[ky + bord][kx + bord];
      }
    }
    for (int ky = 0; ky < size; ky++) {
      for (int kx = 0; kx < size; kx++) {
        kernel[ky][kx] /= sumCoef;
      }
    }
    return kernel;
  }

  static void gradientImageSobel(GrayU8 input, GrayU8 output) {
    int h1[][] = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
    int h2[][] = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
    int bord = 1;
    for (int y = 0; y < input.height; ++y) {
      for (int x = 0; x < input.width; ++x) {
        if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
          output.set(x, y, input.get(x, y));
        } else {
          int valx = 0;
          int valy = 0;
          for (int ky = -bord; ky <= bord; ky++) {
            for (int kx = -bord; kx <= bord; kx++) {
              valx += input.get(x + kx, y + ky) * h1[ky + bord][kx + bord];
              valy += input.get(x + kx, y + ky) * h2[ky + bord][kx + bord];
            }
          }
          int v = (int) Math.sqrt(valx * valx + valy * valy);
          if (v < 0)
            v = 0;
          else if (v > 255)
            v = 255;
          output.set(x, y, v);
        }
      }
    }
  }

  static void meanFilterWithBorders(GrayU8 input, GrayU8 output, int size, BorderType borderType) {
    if (size % 2 == 1) {
      int bord = (size - 1) / 2;
      for (int y = 0; y < input.height; ++y) {
        for (int x = 0; x < input.width; ++x) {
          if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
            borderTreatement(x, y, bord, size, input, output, borderType);
          } else {
            meanFilterTreatement(x, y, bord, size, input, output);
          }
        }
      }
    } else {
      System.err.println("size doit être impair");
    }
  }

  private static void meanFilterTreatement(int x, int y, int bord, int size, GrayU8 input, GrayU8 output) {
    int val = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      for (int kx = -bord; kx <= bord; kx++) {
        val += input.get(x + kx, y + ky);
      }
    }
    output.set(x, y, val / (size * size));
  }

  private static void skip(int x, int y, GrayU8 input, GrayU8 output) {
    output.set(x, y, input.get(x, y));
  }

  private static void normalized(int x, int y, int bord, GrayU8 input, GrayU8 output) {
    int val = 0;
    int nbPix = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      if (y + ky >= 0 && y + ky < input.height) {
        for (int kx = -bord; kx <= bord; kx++) {
          if (x + kx >= 0 && x + kx < input.width) {
            val += input.get(x + kx, y + ky);
            nbPix++;
          }
        }
      }
    }
    output.set(x, y, val / nbPix);
  }

  private static void extended(int x, int y, int bord, int size, GrayU8 input, GrayU8 output) {
    int valE = 0;
    int xE, yE = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      yE = y + ky;
      if (yE < 0)
        yE = 0;
      else if (yE >= input.height)
        yE = input.height - 1;
      for (int kx = -bord; kx <= bord; kx++) {
        xE = x + kx;
        if (xE < 0)
          xE = 0;
        else if (xE >= input.width)
          xE = input.width - 1;
        valE += input.get(xE, yE);

      }
    }
    output.set(x, y, valE / (size * size));
  }

  private static void reflect(int x, int y, int bord, int size, GrayU8 input, GrayU8 output) {
    int valE = 0;
    int xE, yE = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      yE = y + ky;
      if (yE < 0)
        yE = -yE;
      else if (yE >= input.height) {
        yE = input.height - 1 - (yE - input.height);
      }
      for (int kx = -bord; kx <= bord; kx++) {
        xE = x + kx;
        if (xE < 0)
          xE = -xE;
        else if (xE >= input.width)
          xE = input.width - 1 - (xE - input.width);
        valE += input.get(xE, yE);

      }
    }
    output.set(x, y, valE / (size * size));
  }

  private static void normalizedGauss(int x, int y, int bord, GrayU8 input, GrayU8 output, double[][] kernel,
      double sigma) {
    double coefEntierKernel = 1 / kernel[bord][bord];
    int size = kernel.length;
    double[][] kernel2 = new double[size][size];
    for (int ky = 0; ky < size; ky++) {
      for (int kx = 0; kx < size; kx++) {
        kernel2[ky][kx] = kernel[ky][kx] * coefEntierKernel;
      }
    }

    int val = 0;
    double coef = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      if (y + ky >= 0 && y + ky < input.height) {
        for (int kx = -bord; kx <= bord; kx++) {
          if (x + kx >= 0 && x + kx < input.width) {
            val += input.get(x + kx, y + ky) * kernel2[ky + bord][kx + bord];
            coef += kernel2[ky + bord][kx + bord];
          }
        }
      }
    }
    output.set(x, y, (int) (val / coef));
  }

  private static void extendedGaussian(int x, int y, int bord, int size, GrayU8 input, GrayU8 output,
      double[][] kernel) {
    int valE = 0;
    int xE, yE = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      yE = y + ky;
      if (yE < 0)
        yE = 0;
      else if (yE >= input.height)
        yE = input.height - 1;
      for (int kx = -bord; kx <= bord; kx++) {
        xE = x + kx;
        if (xE < 0)
          xE = 0;
        else if (xE >= input.width)
          xE = input.width - 1;
        valE += input.get(xE, yE) * kernel[ky + bord][kx + bord];

      }
    }
    output.set(x, y, valE);
  }

  private static void reflectGaussian(int x, int y, int bord, int size, GrayU8 input, GrayU8 output,
      double[][] kernel) {
    int valE = 0;
    int xE, yE = 0;
    for (int ky = -bord; ky <= bord; ky++) {
      yE = y + ky;
      if (yE < 0)
        yE = -yE;
      else if (yE >= input.height) {
        yE = input.height - 1 - (yE - input.height);
      }
      for (int kx = -bord; kx <= bord; kx++) {
        xE = x + kx;
        if (xE < 0)
          xE = -xE;
        else if (xE >= input.width)
          xE = input.width - 1 - (xE - input.width);
        valE += input.get(xE, yE) * kernel[ky + bord][kx + bord];

      }
    }
    output.set(x, y, valE);
  }

  private static void borderTreatement(int x, int y, int bord, int size, GrayU8 input, GrayU8 output,
      BorderType borderType) {
    switch (borderType) {
      case SKIP:
      default:
        skip(x, y, input, output);
        break;
      case NORMALIZED:
        normalized(x, y, bord, input, output);
        break;
      case EXTENDED:
        extended(x, y, bord, size, input, output);
        break;
      case REFLECT:
        reflect(x, y, bord, size, input, output);
        break;
    }
  }

  private static void gaussBorderTreatement(int x, int y, int bord, int size, double[][] kernel, double sigma,
      GrayU8 input, GrayU8 output, BorderType borderType) {
    switch (borderType) {
      case SKIP:
      default:
        skip(x, y, input, output);
        break;
      case NORMALIZED:
        normalizedGauss(x, y, bord, input, output, kernel, sigma);
        break;
      case EXTENDED:
        extendedGaussian(x, y, bord, size, input, output, kernel);
        break;
      case REFLECT:
        reflectGaussian(x, y, bord, size, input, output, kernel);
        break;
    }
  }
}
