package pdl.imageprocessing;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

class GrayLevelProcessing {

	static void threshold(GrayU8 input, int t) {
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

	static void luminosite(GrayU8 input, int delta) {
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

	static int min(GrayU8 input) {
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

	static int max(GrayU8 input) {
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

	static void contrast(GrayU8 input, int min, int max) {
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

	static void contrastLUT(GrayU8 input, int min, int max) {
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
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
					input.set(x, y, LUT[input.get(x, y)]);
				}
			}
		}
	}

	static void contrastParallele(GrayU8 input, int min, int max) {
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

	static int[] histogram(GrayU8 input) {
		int values[] = new int[256];
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				values[input.get(x, y)]++;
			}
		}
		return values;
	}

	static int[] histogramCumul(GrayU8 input){
		int[] histo = histogram(input);
		int histoCum[] = new int[256];
		histoCum[0] = histo[0];
		for (int i = 1; i < 256; i++) {
			histoCum[i]=histo[i]+histoCum[i-1];
		}
		return histoCum;
	}

	static void egalisation(GrayU8 input) {
		int[] egal = new int[256];
		int[] histoCumul =  histogramCumul(input);
		for (int i = 0; i < 256; i++) {
			egal[i] = (histoCumul[i]*255)/(input.height*input.width);
		}
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				input.set(x, y, egal[input.get(x, y)]);
			}
		}
	}

	static void luminositeColor(Planar<GrayU8> input, int delta) {
		for(int i =0; i<3; ++i){
			GrayLevelProcessing.luminosite(input.getBand(i), delta);
		}
	}

	
	private static void testEgalisation(GrayU8 input){
		int[] hist = new int[256];
		int[] t = new int[256];
		GrayU8 output = new GrayU8();
		ImageStatistics.histogram(input, ImageStatistics.min(input), hist);
		EnhanceImageOps.equalize(hist, t);
		EnhanceImageOps.applyTransform(input, t, output);
	}
}