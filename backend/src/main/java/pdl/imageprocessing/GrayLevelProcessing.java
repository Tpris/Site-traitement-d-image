package pdl.imageprocessing;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.GrayU8;

class GrayLevelProcessing {

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

	
	private static void testEgalisation(GrayU8 input){
		int[] hist = new int[256];
		int[] t = new int[256];
		GrayU8 output = new GrayU8();
		ImageStatistics.histogram(input, ImageStatistics.min(input), hist);
		EnhanceImageOps.equalize(hist, t);
		EnhanceImageOps.applyTransform(input, t, output);
	}
}