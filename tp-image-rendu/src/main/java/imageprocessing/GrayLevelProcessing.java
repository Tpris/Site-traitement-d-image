package imageprocessing;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.concurrency.BoofConcurrency;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class GrayLevelProcessing {

	public static void threshold(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < t) {
					gl = 0;
				} else {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void luminosite(GrayU8 input, int delta) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y) + delta;
				if (gl > 255)
					gl = 255;
				else if (gl < 0)
					gl = 0;
				input.set(x, y, gl);
			}
		}
	}

	public static void reverse(GrayU8 input) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = 255 - input.get(x, y);
				input.set(x, y, gl);
			}
		}
	}

	public static int min(GrayU8 input) {
		int min = 255;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < min)
					min = gl;
			}
		}
		return min;
	}

	public static int max(GrayU8 input) {
		int max = 0;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl > max)
					max = gl;
			}
		}
		return max;
	}

	public static void contrast(GrayU8 input, int min, int max) {
		int minHisto = min(input);
		int maxHisto = max(input);
		if (min > max) {
			System.err.println("min est superieur à max");
		} else if (minHisto == maxHisto) {
			System.err.println("un contraste ne peut pas être appliqué à une image à couleur unie");
		} else {
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
					input.set(x, y, ((max - min) * (input.get(x, y) - minHisto) / (maxHisto - minHisto)) + min);
				}
			}
		}
	}

	public static void contrastLUTwithMinMaxHisto(GrayU8 input, int min, int max) {
		int minHisto = min(input);
		int maxHisto = max(input);
		contrastLUT(input, min, max, minHisto, maxHisto);
	}

	public static void contrastLUT(GrayU8 input, int min, int max, int minHisto, int maxHisto) {
		if (min > max) {
			System.err.println("min est superieur à max");
		} else if (minHisto == maxHisto) {
			System.err.println("un contraste ne peut pas être appliqué à une image à couleur unie");
		} else {
			int LUT[] = new int[256];
			for (int i = 0; i < 256; i++) {
				LUT[i] = ((max - min) * (i - minHisto) / (maxHisto - minHisto)) + min;
			}
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
					input.set(x, y, LUT[input.get(x, y)]);
				}
			}
		}
	}

	public static void contrastParallele(GrayU8 input, int min, int max) {
		int minHisto = min(input);
		int maxHisto = max(input);
		if (min > max) {
			System.err.println("min est superieur à max");
		} else if (minHisto == maxHisto) {
			System.err.println("un contraste ne peut pas être appliqué à une image à couleur unie");
		} else {
			int LUT[] = new int[256];
			for (int i = 0; i < 256; i++) {
				LUT[i] = ((max - min) * (i - minHisto) / (maxHisto - minHisto)) + min;
			}
			BoofConcurrency.loopFor(0, input.height, y -> {
				for (int x = 0; x < input.width; ++x) {
					input.set(x, y, LUT[input.get(x, y)]);
				}
			});
		}
	}

	public static int[] histogram(GrayU8 input) {
		int values[] = new int[256];
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				values[input.get(x, y)]++;
			}
		}
		return values;
	}

	public static int[] histogramCumul(GrayU8 input){
		int[] histo = histogram(input);
		int histoCum[] = new int[256];
		histoCum[0] = histo[0];
		for (int i = 1; i < 256; i++) {
			histoCum[i]=histo[i]+histoCum[i-1];
		}
		return histoCum;
	}

	public static void egalisationWithHistoCum(GrayU8 input) {
		int[] histoCumul =  histogramCumul(input);
		egalisation(input, histoCumul);
	}

	public static void egalisation(GrayU8 input, int[] histoCumul) {
		int[] egal = new int[256];
		for (int i = 0; i < 256; i++) {
			egal[i] = (histoCumul[i]*255)/(input.height*input.width);
		}
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				input.set(x, y, egal[input.get(x, y)]);
			}
		}
	}

	public static void luminositeColor(Planar<GrayU8> input, int delta) {
		for(int i =0; i<3; ++i){
			GrayLevelProcessing.luminosite(input.getBand(i), delta);
		}
	}

	public static void main(String[] args) {

		// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}
		final String inputPath = args[0];
		GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		if (input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}

		// processing

		// Partie 1
		// threshold(input, 128);

		// Partie 2
		// luminosite(input, -40);

		// Partie 3
		// long begin = System.nanoTime();
    	// for(int i = 0; i<1000; ++i){
		// 	// contrast(input, 90, 165);
		// 	// contrastLUT(input, 0, 255);
		// 	contrastParallele(input, 90, 165);
    	// }
    	// long end = System.nanoTime();
    	// System.out.println("Durée = " + (end - begin)/1000000 + " millisecondes");
		
		// System.out.println("min =" + min(input) + " max = " + max(input));

		// Partie 4
		egalisationWithHistoCum(input);
		// testEgalisation(input);

		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		// UtilImageIO.saveImage(output, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

	private static void testEgalisation(GrayU8 input){
		int[] hist = new int[256];
		int[] t = new int[256];
		GrayU8 output = new GrayU8();
		ImageStatistics.histogram(input, ImageStatistics.min(input), hist);
		EnhanceImageOps.equalize(hist, t);
		EnhanceImageOps.applyTransform(input, t, output);
	}

	/**
	 * y1 = ax1+b
	 * y2 = ax2+b
	 * a= (y2-y1)/(x2-x1)
	 * = 255-0/max-min
	 * 
	 * b=y-ax = y1-ax1 = 0 - amin
	 * 
	 * y = ax - a min
	 * = a(x-min)
	 * y = 255(x-min)/(max-min)
	 */

}