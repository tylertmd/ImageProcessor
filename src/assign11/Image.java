package assign11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class represents an image as a two-dimensional array of pixels and provides a number
 * of image filters (via instance methods) for changing the appearance of the image.
 * Application of multiple filters is cumulative; e.g., obj.redBlueSwapFilter() followed by
 * obj.rotateClockwiseFilter() results in an image altered both in color and orientation.
 *
 * Note:
 *   - The pixel in the northwest corner of the image is stored in the first row, first column.
 *   - The pixel in the northeast corner of the image is stored in the first row, last column.
 *   - The pixel in the southeast corner of the image is stored in the last row, last column.
 *   - The pixel in the southwest corner of the image is stored in the last row, first column.
 *
 * @author Prof. Martin and Tyler Davidson
 * @version Assignment 10
 */
public class Image {

	private Pixel[][] imageArray;

	/**
	 * Creates a new Image object by reading the image file with the given filename.
	 *
	 * DO NOT MODIFY THIS METHOD
	 *
	 * @param filename - name of the given image file to read
	 * @throws IOException if file does not exist or cannot be read
	 */
	public Image(String filename) {
		BufferedImage imageInput = null;
		try {
			imageInput = ImageIO.read(new File(filename));
		}
		catch(IOException e) {
			System.out.println("Image file " + filename + " does not exist or cannot be read.");
		}

		imageArray = new Pixel[imageInput.getHeight()][imageInput.getWidth()];
		for(int i = 0; i < imageArray.length; i++)
			for(int j = 0; j < imageArray[0].length; j++) {
				int rgb = imageInput.getRGB(j, i);
				imageArray[i][j] = new Pixel((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
			}
	}

	/**
	 * Create an Image object directly from a pre-made Pixel array.
   * This is primarily to be used in testing.
	 *
	 * DO NOT MODIFY THIS METHOD
	 */
	public Image(Pixel[][] imageArray) {
		this.imageArray = imageArray;
	}

	/**
	 * Create a new "default" Image object, whose purpose is to be used in testing.
	 *
	 * The orientation of this image:
	 * 		cyan 	 red
	 *		green	 magenta
	 *		yellow	 blue
	 *
	 * DO NOT MODIFY THIS METHOD
	 */
	public Image() {
		imageArray = new Pixel[3][2];
		imageArray[0][0] = new Pixel(0, 255, 255);  // cyan
		imageArray[0][1] = new Pixel(255, 0, 0);  // red
		imageArray[1][0] = new Pixel(0, 255, 0);  // green
		imageArray[1][1] = new Pixel(255, 0, 255);  // magenta
		imageArray[2][0] = new Pixel(255, 255, 0);  // yellow
		imageArray[2][1] = new Pixel(0, 0, 255);  // blue
	}

	/**
	 * Gets the pixel at the specified row and column indexes.
	 *
	 * DO NOT MODIFY THIS METHOD
	 *
	 * @param rowIndex - given row index
	 * @param columnIndex - given column index
	 * @return the pixel at the given row index and column index
	 * @throws IndexOutOfBoundsException if row or column index is out of bounds
	 */
	public Pixel getPixel(int rowIndex, int columnIndex) {
		if(rowIndex < 0 || rowIndex >= imageArray.length)
			throw new IndexOutOfBoundsException("rowIndex must be in range 0-" + (imageArray.length - 1));

		if(columnIndex < 0 || columnIndex >= imageArray[0].length)
			throw new IndexOutOfBoundsException("columnIndex must be in range 0-" + (imageArray[0].length - 1));

		return imageArray[rowIndex][columnIndex];
	}

	/**
	 * Writes the image represented by this object to file.
	 * Does nothing if the image length is 0.
	 *
	 * DO NOT MODIFY THIS METHOD
	 *
	 * @param filename - name of image file to write
	 * @throws IOException if file does cannot be written
	 */
	public void writeImage(String filename) {
		if(imageArray.length > 0) {
			BufferedImage imageOutput = new BufferedImage(imageArray[0].length,
					imageArray.length, BufferedImage.TYPE_INT_RGB);

			for(int i = 0; i < imageArray.length; i++)
				for(int j = 0; j < imageArray[0].length; j++)
					imageOutput.setRGB(j, i, imageArray[i][j].getPackedRGB());

			try {
				ImageIO.write(imageOutput, "png", new File(filename));
			}
			catch(IOException e) {
				System.out.println("The image cannot be written to file " + filename);
			}
		}
	}

	/**
	 * Applies a filter to the image represented by this object such that for each
	 * pixel the red amount and blue amount are swapped.
	 *
	 * HINT: Since the Pixel class does not include setter methods for its private
	 *       instance variables, create new Pixel objects with the altered colors.
	 */
	public void redBlueSwapFilter() {
		for (int row = 0; row < imageArray.length; row++) {
			for (int col = 0; col < imageArray[0].length; col++) {
				Pixel originalImage = imageArray[row][col];
				
				int red = originalImage.getRedAmount();
				int green = originalImage.getGreenAmount();
				int blue = originalImage.getBlueAmount();
				
				Pixel newImage = new Pixel(blue, green, red);
				imageArray[row][col] = newImage;
			}
		}
		
	}


	/**
	 * Applies a filter to the image represented by this object such that the color
	 * of each pixel is converted to its corresponding grayscale shade, producing the
	 * effect of a black and white photo. The filter sets the amount of red, green,
	 * and blue all to the value of this average:
	 *           (originalRed + originalGreen + originalBlue) / 3
	 *
	 * HINT: Since the Pixel class does not include setter methods for its private
	 *       instance variables, create new Pixel objects with the altered colors.
	 */
	public void blackAndWhiteFilter() {
		for (int row = 0; row < imageArray.length; row++) {
			for (int col = 0; col < imageArray[0].length; col++) {
				Pixel originalImage = imageArray[row][col];
				
				int red = originalImage.getRedAmount();
				int green = originalImage.getGreenAmount();
				int blue = originalImage.getBlueAmount();
				
				int grayScale = (red + green + blue) / 3;
				
				Pixel grayImage = new Pixel(grayScale, grayScale, grayScale);
				imageArray[row][col] = grayImage;
			}
		}
	}

	/**
	 * Applies a filter to the image represented by this object such that it is rotated
	 * clockwise (by 90 degrees). This filter rotates directly clockwise, it should
	 * not do this by rotating counterclockwise 3 times.
	 *
	 * HINT: If the image is not square, this filter requires creating a new array with
	 *       different lengths. Use the technique of creating and reassigning a new backing array
	 *       from BetterDynamicArray (assign06) as a guide for how to make a second array and
	 *       eventually reset the imageArray reference to this new array.
	 *       Note that we learned how to rotate a square 2D array *left* in Class Meeting 11.
	 */
	public void rotateClockwiseFilter() {
		Pixel[][] rotatedImage = new Pixel[imageArray[0].length][imageArray.length];
		
		for (int row = 0; row < imageArray.length; row++) {
			for (int col = 0; col < imageArray[0].length; col++) {
				rotatedImage[col][imageArray.length - 1 - row] = imageArray[row][col];
			}
		}
		
		imageArray = rotatedImage;
	}

	/**
	 * Applies a filter to the image represented by this object such that it is a gradient from left to right
	 * of blue to green.
	 */
	public void blueToGreenGradientFilter() {
	    for (int row = 0; row < imageArray.length; row++) {
	        for (int col = 0; col < imageArray[0].length; col++) {
	            Pixel originalImage = imageArray[row][col];

	            double gradientFactor = (double) col / imageArray[0].length;

	            int blue = (int) (255 * (1 - gradientFactor));
	            int green = (int) (255 * gradientFactor);

	            int red = originalImage.getRedAmount();

	            Pixel newImage = new Pixel(red, green, blue);

	            imageArray[row][col] = newImage;
	        }
	    }	
	}
	
	
	public void sepiaFilter() {
	    for (int row = 0; row < imageArray.length; row++) {
	        for (int col = 0; col < imageArray[0].length; col++) {
	            Pixel originalPixel = imageArray[row][col];
	            int originalRed = originalPixel.getRedAmount();
	            int originalGreen = originalPixel.getGreenAmount();
	            int originalBlue = originalPixel.getBlueAmount();

	            int sepiaRed = (int)(0.393 * originalRed + 0.769 * originalGreen + 0.189 * originalBlue);
	            int sepiaGreen = (int)(0.349 * originalRed + 0.686 * originalGreen + 0.168 * originalBlue);
	            int sepiaBlue = (int)(0.272 * originalRed + 0.534 * originalGreen + 0.131 * originalBlue);

	            sepiaRed = Math.min(255, sepiaRed);
	            sepiaGreen = Math.min(255, sepiaGreen);
	            sepiaBlue = Math.min(255, sepiaBlue);

	            Pixel newPixel = new Pixel(sepiaRed, sepiaGreen, sepiaBlue);
	            imageArray[row][col] = newPixel;
	        }
	    }
	}
	
	
	public void brightnessFilter(int number) {
		for (int row = 0; row < imageArray.length; row++) {
			for (int col = 0; col < imageArray[0].length; col++) {
				Pixel originalPixel = imageArray[row][col];
				int originalRed = originalPixel.getRedAmount();
				int originalGreen = originalPixel.getGreenAmount();
				int originalBlue = originalPixel.getBlueAmount();
				
				int brightRed = originalRed + number;
				int brightGreen = originalGreen + number;
				int brightBlue = originalBlue + number;
				
				brightRed = Math.max(0, Math.min(255, brightRed));
				brightGreen = Math.max(0, Math.min(255, brightGreen));
				brightBlue = Math.max(0, Math.min(255, brightBlue));
				
				Pixel newPixel = new Pixel(brightRed, brightGreen, brightBlue);
				imageArray[row][col] = newPixel;
			}
		}
	}
	
	
	public void cropFilter(int x1, int y1, int x2, int y2) {
		
		x1 = Math.max(0, Math.min(x1, imageArray[0].length - 1));
		x2 = Math.max(0, Math.min(x2, imageArray[0].length - 1));
		y1 = Math.max(0, Math.min(y1, imageArray.length - 1));
		y2 = Math.max(0, Math.min(y2, imageArray.length - 1));
		
		if (x1 > x2) {
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2) {
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		
		int newHeight = y2 - y1 + 1;
		int newWidth = x2 - x1 + 1;
		
		Pixel[][] croppedArray = new Pixel[newHeight][newWidth];
		
		for (int row = 0; row < newHeight; row ++) {
			for (int col = 0; col < newWidth; col++) {
				croppedArray[row][col] = imageArray[y1 + row][x1 + col];
			}
		}
		
		imageArray = croppedArray; 
		
	}
	
	
	public Image copy() {
		int width = this.imageArray.length;
		int height = this.imageArray[0].length;
		Pixel[][] newArray = new Pixel[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				newArray[x][y] = new Pixel(this.imageArray[x][y]);
			}
		}
		return new Image(newArray);
	}
	
		
	public void mirrorFilter() {
	    int height = imageArray.length;
	    int width = imageArray[0].length;

	    for (int col = width / 2; col < width; col++) {
	        int mirroredCol = width - col - 1;
	        for (int row = 0; row < height; row++) {
	            Pixel originalPixel = imageArray[row][mirroredCol];
	            int red = originalPixel.getRedAmount();
	            int green = originalPixel.getGreenAmount();
	            int blue = originalPixel.getBlueAmount();

	            Pixel newPixel = new Pixel(red, green, blue);
	            imageArray[row][col] = newPixel;
	        }
	    }
	}
	
	
	public int getNumberOfRows() {
		return this.imageArray.length;
		}

	public int getNumberOfColumns() {
		if(this.imageArray.length == 0)
			return 0;
		return this.imageArray[0].length;
		}
	
	
	public BufferedImage getBufferedImage() {
	    BufferedImage bufferedImg = new BufferedImage(imageArray[0].length, imageArray.length, BufferedImage.TYPE_INT_RGB);

	    for (int i = 0; i < imageArray.length; i++) {
	        for (int j = 0; j < imageArray[0].length; j++) {
	            bufferedImg.setRGB(j, i, imageArray[i][j].getPackedRGB());
	        }
	    }

	    return bufferedImg;
	}
}
