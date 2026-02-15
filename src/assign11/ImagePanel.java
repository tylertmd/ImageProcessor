package assign11;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;	

/**
 * This class represents a GUI component for displaying an image.
 *
 * @author Prof. Martin
 * @version Assignment 10
 */
public class ImagePanel extends JPanel {

	private int startX, startY, endX, endY;
	private boolean isDragging = false;

	private BufferedImage bufferedImg;
	private ImageProcessorFrame parentFrame;
	private Image image;

	/**
	 * Creates a new ImagePanel to display the given image.
	 *
	 * @param img - the given image
	 */
	public ImagePanel(Image img, ImageProcessorFrame frame) {
		int rowCount = img.getNumberOfRows();
		int colCount = img.getNumberOfColumns();

		this.bufferedImg = new BufferedImage(colCount, rowCount, BufferedImage.TYPE_INT_RGB);

		for(int i = 0; i < rowCount; i++)
			for(int j = 0; j < colCount; j++)
				this.bufferedImg.setRGB(j, i, img.getPixel(i, j).getPackedRGB());

		this.setPreferredSize(new Dimension(colCount, rowCount));
		
		this.image = img;
		this.parentFrame = frame;
	
	
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			startX = e.getX();
			startY = e.getY();
			isDragging = true;
			
			if (parentFrame != null) {
				parentFrame.disableAllFilters();
			}
			repaint();
		}
		
		public void mouseReleased(MouseEvent e) {
			endX = e.getX();
			endY = e.getY();
			isDragging = false;
	
			 // Ensure the rectangle is within bounds
			if (parentFrame != null) {
				parentFrame.enableOnlyCrop();
			}
			repaint();
        }
    });
	
	addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
		public void mouseDragged(java.awt.event.MouseEvent e) {
			endX = e.getX();
			endY = e.getY();
			repaint();
		}
	});
	
	
}
	
	public boolean hasSelection() {
		return startX != endX && startY != endY;
	}

	public int[] getSelection() {
		return new int[] { Math.min(startX, endX), Math.min(startY, endY),
		                   Math.max(startX, endX), Math.max(startY, endY) };
	}

	/**
	 * This method is called by the system when a component needs to be painted.
	 * Which can be at one of three times:
	 *    --when the component first appears
	 *    --when the size of the component changes (including resizing by the user)
	 *    --when repaint() is called
	 *
	 * Partially overrides the paintComponent method of JPanel.
	 *
	 * @param g -- graphics context onto which we can draw
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(this.bufferedImg, 0, 0, this);
		
		if (isDragging || startX != endX || startY != endY) {
			int x = Math.min(startX, endX);
			int y = Math.min(startY, endY);
			int width = Math.abs(endX - startX);
			int height = Math.abs(endY - startY);
			
			g.setColor(new Color(105, 105, 105, 125));
			g.fillRect(x, y, width, height);
			
			g.setColor(java.awt.Color.BLUE);
			g.drawRect(x, y, width, height);
		}
	}

	// Required by a serializable class (ignore for now)
	private static final long serialVersionUID = 1L;
}
