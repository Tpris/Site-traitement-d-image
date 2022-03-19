package pdl.imageprocessing;

import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.concurrency.BoofConcurrency;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel2D_S32;
import boofcv.struct.image.GrayU8;

class Convolution {

  static void meanFilterSimple(GrayU8 input, GrayU8 output, int size) {
    if (size % 2 == 1) {
      int bord = (size - 1) / 2;
      for (int y = 0; y < input.height; ++y) {
        for (int x = 0; x < input.width; ++x) {
          if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
            skip(x, y, input, output);
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

  static void meanFilterWithBorders(GrayU8 input, GrayU8 output, int size, BorderType borderType) {
    if (size % 2 == 1) {
      int bord = (size - 1) / 2;
      for (int y = 0; y < input.height; ++y) {
        for (int x = 0; x < input.width; ++x) {
          if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
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

          } else {
            meanFilterTreatement(x, y, bord, size, input, output);
          }
        }
      }
    } else {
      System.err.println("size doit être impair");
    }
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
      else if (yE >= input.height){
        yE = input.height-1 - (yE-input.height);
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

  static void convolution(GrayU8 input, GrayU8 output, int[][] kernel) {
    if ((kernel.length) % 2 == 1 && (kernel[0].length) % 2 == 1) {
      int bordh = (kernel.length - 1) / 2;
      int bordw = (kernel[0].length - 1) / 2;
      for (int y = 0; y < input.height; ++y) {
        for (int x = 0; x < input.width; ++x) {
          if (x < bordw || x >= input.width - bordw || y < bordh || y >= input.height - bordh) {
            skip(x, y, input, output);
          } else {
            int val = 0;
            int nbPix = 0;
            for (int ky = -bordh; ky <= bordh; ky++) {
              for (int kx = -bordw; kx <= bordw; kx++) {
                val += input.get(x + kx, y + ky) * kernel[ky + bordh][kx + bordw];
                nbPix += kernel[ky + bordh][kx + bordw];
              }
            }
            output.set(x, y, val / nbPix);
          }
        }
      }
    } else {
      System.err.println("la taille du noyau doit être impair");
    }
  }

  static void flouGaussienGrayU8(GrayU8 input, GrayU8 output, int size, double[][] kernel) {
    if (size % 2 == 1) {
      int bord = (size - 1) / 2;
      for (int y = 0; y < input.height; ++y) {
        for (int x = 0; x < input.width; ++x) {
          if (x < bord || x >= input.width - bord || y < bord || y >= input.height - bord) {
            skip(x, y, input, output);
          } else {
            int val = 0;
            for (int ky = -bord; ky <= bord; ky++) {
              for (int kx = -bord; kx <= bord; kx++) {
                val += input.get(x + kx, y + ky) * kernel[ky+bord][kx+bord];
              }
            }
            output.set(x, y, val);
          }
        }
      }
    } else {
      System.err.println("la taille du noyau doit être impair");
    }
  }

  static double[][] gaussianKernel(int size, int sigma){
      double[][] kernel = new double [size][size];
      int bord = (size - 1) / 2;
      double denominateur1 = 2*sigma*sigma;
      double denominateur2 = denominateur1*Math.PI;
      for (int ky = -bord; ky <= bord; ky++) {
        for (int kx = -bord; kx <= bord; kx++) {
          kernel[ky+bord][kx+bord] = Math.exp(-(((kx*kx)+(ky*ky))/denominateur1)) /denominateur2;
        }
      }
      return kernel;
  }

  static void convolutionParallele(GrayU8 input, GrayU8 output, int[][] kernel) {
    if ((kernel.length) % 2 == 1 && (kernel[0].length) % 2 == 1) {
      int bordh = (kernel.length - 1) / 2;
      int bordw = (kernel[0].length - 1) / 2;
      BoofConcurrency.loopFor(0, input.height, y -> {
        for (int x = 0; x < input.width; ++x) {
          if (x < bordw || x >= input.width - bordw || y < bordh || y >= input.height - bordh) {
            skip(x, y, input, output);
          } else {
            int val = 0;
            int nbPix = 0;
            for (int ky = -bordh; ky <= bordh; ky++) {
              for (int kx = -bordw; kx <= bordw; kx++) {
                val += input.get(x + kx, y + ky) * kernel[ky + bordh][kx + bordw];
                nbPix += kernel[ky + bordh][kx + bordw];
              }
            }
            output.set(x, y, val / nbPix);
          }
        }
      });
    } else {
      System.err.println("la taille du noyau doit être impair");
    }
  }

  static void testConvolution(GrayU8 input, GrayU8 output){
    int[] data = { 1, 2, 3, 2, 1 ,
                    2, 6, 8, 6, 2 ,
                    3, 8, 10, 8, 3,
                    2, 6, 8, 6, 2 ,
                    1, 2, 3, 2, 1 };
    Kernel2D_S32 kernel =  new Kernel2D_S32(5, data);
    GConvolveImageOps.convolveNormalized(kernel, input, output);
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
          // v = valy;
          if(v < 0) v=0;
          else if(v > 255) v = 255;
          output.set(x, y, v);
        }
      }
    }
  }

}