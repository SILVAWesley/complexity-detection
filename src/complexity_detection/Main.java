package complexity_detection;

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
	
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat color = Imgcodecs.imread("./images/complexas/06NC3J6.jpg");
		System.out.println(color);
		
		Mat gray = new Mat();
		Mat draw = new Mat();
		Mat wide = new Mat();
		Mat newImg = new Mat(); 
		
		Imgproc.cvtColor(color, newImg, Imgproc.CV_CONTOURS_MATCH_I3);
		
		Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(gray,  wide,  50, 150, 3, false);
		
		wide.convertTo(draw, CvType.CV_8U);
		
		if (Imgcodecs.imwrite("C:\\Users\\Wesley Silva\\git\\complexity-detection\\images\\complexas\\06NC3J622222.jpg", draw)) {
			
			System.out.println("edge is detected...");
		}
		
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(draw, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		countColors(newImg);
		
		System.out.println(contours.size());
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
}
