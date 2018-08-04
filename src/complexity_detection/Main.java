package complexity_detection;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat[] simple = new Mat[30];
		Mat[] medium = new Mat[30];
		Mat[] complex = new Mat[30];
		
		loadAllImages(simple, medium, complex);
		
		Mat color = loadImage(ImgComplexity.SIMPLE, "11");
		
		Mat gray = new Mat();
		Mat draw = new Mat();
		Mat wide = new Mat();
		Mat newImg = new Mat(); 
		
		Imgproc.cvtColor(color, newImg, Imgproc.CV_CONTOURS_MATCH_I3);
		
		Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(gray,  wide,  50, 150, 3, false);
		
		wide.convertTo(draw, CvType.CV_8U);
		
		//if (Imgcodecs.imwrite("C:\\Users\\Wesley Silva\\git\\complexity-detection\\images\\complexas\\06NC3J622222.jpg", draw)) {
			
		//	System.out.println("edge is detected...");
		//}
		
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(draw, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		countColors(newImg);
		System.out.println("Origin entropy = " + getEntropy(mat2Img(newImg), 256));
		//System.out.println("Red entropy = " + getEntropy(getRed(mat2Img(newImg)), 256));
		//System.out.println("Green entropy = " + getEntropy(getGreen(mat2Img(newImg)), 256));
		//System.out.println("Blue entropy = " + getEntropy(getBlue(mat2Img(newImg)), 256));
		System.out.println(contours.size());
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
	}
	
	public static Mat loadImage(ImgComplexity complexity, String num) {
		if (complexity.equals(ImgComplexity.SIMPLE)) {
			Mat m = Imgcodecs.imread("./images/simples/" + "s_img_" + num + "." + "jpg");
			
			if (m.empty()) {
				return Imgcodecs.imread("./images/simples/" + "s_img_" + num + "." + "png");
			} 
			
			return m;
		} else if (complexity.equals(ImgComplexity.MEDIUM)) {
			Mat m = Imgcodecs.imread("./images/medias/" + "m_img_" + num + "." + "jpg	");
			
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
	
	public static void countColors(Mat draw) {

				Mat m = draw;
				  int size = (int)m.total();
				  int nbChannels = m.channels();
				  byte[] temp = new byte[size * nbChannels];
				  m.get(0, 0, temp);
				  Set<Integer> set =  new HashSet<Integer>();//will contain all different values
				  for (int i = 0; i < size; i++)
				  {
				     int pixelVal = 0;
				     for(int j=0; j<nbChannels; j++) {
				        pixelVal+= (temp[i*nbChannels+j] << (8*j));
				     }
				     set.add(pixelVal);
				 }
				 
				  
				  
				 System.out.println("Color number : " + set.size());

	}
	
	public static BufferedImage mat2Img(Mat in)
    {
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
		 
			  for(int i=0;i<maxValue;i++){ //the number of times a sybmol has occured
			    if(bins[i]>0){ //log of zero goes to infinity
			    	//System.out.println("ENTROU");
			        temp=(bins[i]/totalSize)*(Math.log(bins[i]/totalSize));
			        entropyValue += temp;
			      }
			  }
			return entropyValue*(-1);
	  }
	  
	  public static BufferedImage getBlue(BufferedImage originalImage){
	        int RGBPixel;
	        int bluePixel;
	                for (int i = 0 ; i <= originalImage.getWidth()-1 ; i++)
	                    for (int j=0 ;j <= originalImage.getHeight()-1 ; j++){

	                            RGBPixel = originalImage.getRGB(i,j);
	                            bluePixel = RGBPixel & 0xff;

	                            originalImage.setRGB(i, j, (bluePixel));
	                    }
	        return originalImage;
	    }
	    
	//------------------------------------------------------------------------------------------   
	   
	    /* Process a image to obtain its red channel
	    * @param BufferedImage originalImage - The image to be processed
	    * @return BufferedImage originalImage - The red channel of the given image
	    */
	    public static BufferedImage getRed(BufferedImage originalImage){
	        int RGBPixel;
	        int redPixel;
	                for (int i = 0 ; i <= originalImage.getWidth()-1 ; i++)
	                    for (int j=0 ;j <= originalImage.getHeight()-1 ; j++){

	                            RGBPixel = originalImage.getRGB(i,j);
	                            redPixel = (RGBPixel>>16) & 0xff;

	                            originalImage.setRGB(i, j, (redPixel << 16));
	                    }
	        return originalImage;
	    }
	    
	 //------------------------------------------------------------------------------------------   
	   
	    /* Process a image to obtain its gren channel
	    * @param BufferedImage originalImage - The image to be processed
	    * @return BufferedImage originalImage - The green channel of the given image
	    */
	    public static BufferedImage getGreen(BufferedImage originalImage){
	        int RGBPixel;
	        int greenPixel;
	                for (int i = 0 ; i <= originalImage.getWidth()-1 ; i++)
	                    for (int j=0 ;j <= originalImage.getHeight()-1 ; j++){

	                            RGBPixel = originalImage.getRGB(i,j);
	                            greenPixel = (RGBPixel>>8) & 0xff;

	                            originalImage.setRGB(i, j, (greenPixel << 8));
	                    }
	        return originalImage;
	    }
}
