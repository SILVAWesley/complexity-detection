package complexity_detection;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
	public static final String FILE_HEADER = "nome, num, rotulo, contornos, entropia, qtd_cores";
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat color = loadImage(ImgComplexity.MEDIUM, "11");
		
		loadForExperiment();
		
		System.out.println(color);
	}
	
	public static void loadForExperiment() {
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(new File("data.csv"));
			
			pw.append(FILE_HEADER + System.lineSeparator());
			
			for (int i = 0; i < 45; i++) {
				int num = i + 1;
				String numStr = "";
				if (num < 10) {
					numStr = "0" + (num);
				} else {
					numStr = num + "";
				}
				
				Mat m = loadImage(ImgComplexity.SIMPLE, numStr );
				
				int countours = getCountours(m);
				int colors = countColors(m);
				double entropy = getEntropy(mat2Img(m), 256);
				
				pw.append("s_img_" + numStr + ",");
				pw.append(String.valueOf(num) + ",");
				pw.append("simples,");
				pw.append(String.valueOf(countours) + ",");
				pw.append(String.valueOf(entropy) + ",");
				pw.append(String.valueOf(colors));
				pw.append(System.lineSeparator());
			}
			
			for (int i = 0; i < 45; i++) {
				int num = i + 1;
				String numStr = "";
				if (num < 10) {
					numStr = "0" + (num);
				} else {
					numStr = num + "";
				}
				
				Mat m = loadImage(ImgComplexity.COMPLEX, numStr);
				int countours = getCountours(m);
				int colors = countColors(m);
				double entropy = getEntropy(mat2Img(m), 256);
				
				pw.append("c_img_" + numStr + ",");
				pw.append(String.valueOf(num) + ",");
				pw.append("complexa,");
				pw.append(String.valueOf(countours) + ",");
				pw.append(String.valueOf(entropy) + ",");
				pw.append(String.valueOf(colors));
				pw.append(System.lineSeparator());
			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} finally {
				pw.flush();
				pw.close();
			
		}
	}
			
	public static int getCountours(Mat mat) {
		Mat gray = new Mat();
		
		// Transforma imagem em cinza.
		Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
		
		Mat cannied = new Mat();
		
		// Calcula bordas
		Imgproc.Canny(gray,  cannied,  50, 150, 3, false);
		
		Mat draw = new Mat();
		
		// Final image
		cannied.convertTo(draw, CvType.CV_8U);
		
		List<MatOfPoint> countours = new ArrayList<>();
		Imgproc.findContours(draw, countours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		return countours.size();
	}
	
	public static void loadAllImages(Mat[] simple, Mat[] medium, Mat[] complex) {
		for (int i = 0; i < simple.length; i++) {
			int num = i + 1;
			String numStr = "";
			if (num < 10) {
				numStr = "0" + (num);
			} else {
				numStr = num + "";
			}
				
			
			simple[i] = loadImage(ImgComplexity.SIMPLE, numStr);
		}
		
		for (int i = 0; i < simple.length; i++) {
			int num = i + 1;
			String numStr = "";
			if (num < 10) {
				numStr = "0" + (num);
			} else {
				numStr = num + "";
			}
				
			
			simple[i] = loadImage(ImgComplexity.MEDIUM, numStr);
		}
		
		for (int i = 0; i < simple.length; i++) {
			int num = i + 1;
			String numStr = "";
			if (num < 10) {
				numStr = "0" + (num);
			} else {
				numStr = num + "";
			}
				
			
			simple[i] = loadImage(ImgComplexity.COMPLEX, numStr);
		}
		
	}
	
	public static Mat loadImage(ImgComplexity complexity, String num) {
		if (complexity.equals(ImgComplexity.SIMPLE)) {
			Mat m = Imgcodecs.imread("./images/simples/" + "s_img_" + num + "." + "jpg");
			
			if (m.empty()) {
				return Imgcodecs.imread("./images/simples/" + "s_img_" + num + "." + "png");
			} 
			
			return m;
		} else if (complexity.equals(ImgComplexity.MEDIUM)) {
			Mat m = Imgcodecs.imread("./images/medias/" + "m_img_" + num + "." + "jpg");
			if (m.empty()) {
				return Imgcodecs.imread("./images/medias/" + "m_img_" + num + "." + "png");
			}
			
			return m;
		} else {
			Mat m = Imgcodecs.imread("./images/complexas/" + "c_img_" + num + "." + "jpg");
			
			if (m.empty()) {
				return Imgcodecs.imread("./images/complexas/" + "c_img_" + num + "." + "png");
			}
			
			return m;
		}
	}
	
	public static int countColors(Mat mat) {
		int size = (int) mat.total();
		int nbChannels = mat.channels();
		byte[] temp = new byte[size * nbChannels];
		mat.get(0, 0, temp);
		Set<Integer> set =  new HashSet<Integer>();//will contain all different values
		for (int i = 0; i < size; i++) {
			int pixelVal = 0;
			for(int j=0; j<nbChannels; j++) {
				pixelVal+= (temp[i*nbChannels+j] << (8*j));
			}
			
			set.add(pixelVal);
		}
				 
		return set.size();
	}
	
	private static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[320 * 240 * (int)in.elemSize()];
        int type;
        in.get(0, 0, data);

        if(in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(320, 240, type);

        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    } 
	
	public static int[] buildHistogram(BufferedImage image, int numberOfBins){
        int bins[]=new int[numberOfBins];
        int intensity ;
        image=getGrayScale8bits(image);
                for (int i = 0 ; i <= image.getWidth()-1 ; i++){
                    for (int j=0 ;j <= image.getHeight()-1 ; j++){
                        intensity = image.getRGB(i,j) & 0xFF;
                        bins[intensity]++;
                    }            
                }
        return bins;
    }
	
	public static BufferedImage getGrayScale8bits(BufferedImage inputImage){
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();
        return img;
	}
	
	  public static double getEntropy(BufferedImage image, int maxValue){
			int bins[] = buildHistogram(image,maxValue);
			  double entropyValue = 0,temp=0;
			  double totalSize = image.getHeight() * image.getWidth(); //total size of all symbols in an image
		 
			  for(int i=0;i<maxValue;i++){ 
			    if(bins[i]>0){
			        temp=(bins[i]/totalSize)*(Math.log(bins[i]/totalSize));
			        entropyValue += temp;
			      }
			  }
			return entropyValue*(-1);
	  }
}
