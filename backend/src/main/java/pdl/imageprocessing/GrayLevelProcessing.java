package pdl.imageprocessing;

import boofcv.struct.image.GrayU8;

class GrayLevelProcessing {

	static void luminosity(GrayU8 input, int delta) {
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

	static void contrastLUT(GrayU8 input, int min, int max, int minHisto, int maxHisto) {
		if (min > max) {
			System.err.println("min est superieur à max");
		} else if (minHisto == maxHisto) {
			System.err.println("un contraste ne peut pas être appliqué à une image à couleur unie");
		} else {
			int LUT[] = new int[256];
			for (int i = 0; i < 256; i++) {
				LUT[i] = ((max - min) * (i - minHisto) / (maxHisto - minHisto)) + min;
				if(LUT[i]>255) LUT[i] = 255;
				else if (LUT[i]<0) LUT[i] = 0;
			}
			for (int y = 0; y < input.height; ++y) {
				for (int x = 0; x < input.width; ++x) {
					input.set(x, y, LUT[input.get(x, y)]);
				}
			}
		}
	}

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

	static void reverse(GrayU8 input) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = 255 - input.get(x, y);
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

	static void egalisation(GrayU8 input, int[] histoCumul) {
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

	public static void threshold4step(GrayU8 input) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				 if (gl < 61) {
					gl = 255;
				} else if(gl<122){
					gl = 122;
				} else if(gl<183){
					gl = 183;
				} else gl = 255;
				if(gl>255) gl = 255; 
				else if(gl<0) gl = 0;
				input.set(x, y, gl);
			}
		}
	}
	
	static void rotate(GrayU8 input, double theta){
		int x0 = input.width/2;
		int y0 = input.height/2;
		GrayU8 tmp = input.clone();
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int p = (int) ((x-x0)*Math.cos(theta)+(y-y0)*Math.sin(theta)+x0);
				int q = (int) (-(x-x0)*Math.sin(theta)+(y-y0)*Math.cos(theta)+y0);
				if(p<input.width && q <input.height && p>=0 && q>=0)
					input.set(x, y, tmp.get(p, q));
				else input.set(x, y, 0);
			}
		}
	}

	static void fisheyes(GrayU8 input, double d){
		int x0 = input.width/2;
		int y0 = input.height/2;
		GrayU8 tmp = input.clone();
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int p = (int) (d*radious(x, y, x0, y0)*(x-x0)+x);
				int q = (int) (d*radious(x, y, x0, y0)*(y-y0)+y);
				if(p<input.width && q <input.height && p>=0 && q>=0)
					input.set(x, y, tmp.get(p, q));
				else input.set(x, y, 0);
			}
		}
	}

	static void tourbillon(GrayU8 input, float tourbillonFactor){
		int x0 = input.width/2;
		int y0 = input.height/2;
		GrayU8 tmp = input.clone();
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				double theta=10*Math.exp(-tourbillonFactor*radious(x, y, x0, y0));
				int p = (int) ((x-x0)*Math.cos(theta)+(y-y0)*Math.sin(theta)+x0);
				int q = (int) (-(x-x0)*Math.sin(theta)+(y-y0)*Math.cos(theta)+y0);
				if(p<input.width && q <input.height && p>=0 && q>=0)
					input.set(x, y, tmp.get(p, q));
				else input.set(x, y, 0);
			}
		}
	}

	private static double radious(int x, int y, int x0, int y0){
		return Math.sqrt(Math.pow(x-x0, 2)+Math.pow(y-y0, 2));
	}
}