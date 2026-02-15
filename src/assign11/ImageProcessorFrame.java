package assign11;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;


public class ImageProcessorFrame extends JFrame {
	
	private ImagePanel imagePanel;
	private Image currentImage;
	private JMenuItem saveItem;
	private JMenuItem grayscaleItem, rotateItem, redBlueSwapItem, gradientItem, sepiaItem, brightnessItem, cropItem, mirrorItem;
	private JSlider brightnessSlider;
	private JPanel sliderPanel;
	private boolean isImageLoaded = false;
	private boolean isFilterApplied = false;
	private ArrayList<Image> undoHistory = new ArrayList<>();
	private JMenuItem undoItem;
	
	public ImageProcessorFrame() {
		
		setTitle("Image Processor");
		setSize(1000, 666);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem openItem = new JMenuItem("Open Image");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openImage();
			}
		});
		
		
		saveItem = new JMenuItem("Save Filtered Image");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImage();
			}
		});
		
		saveItem.setEnabled(false);
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		
		menuBar.add(fileMenu);
		
		
        JMenu filterMenu = new JMenu("Filters");

        grayscaleItem = new JMenuItem("Black and White");
        grayscaleItem.setToolTipText("Converts the image to black and white.");
        grayscaleItem.addActionListener(e -> applyFilter("Black and White"));
        grayscaleItem.setEnabled(false);
        filterMenu.add(grayscaleItem);

        rotateItem = new JMenuItem("Rotate Clockwise");
        rotateItem.setToolTipText("Rotates the image 90 degrees clockwise.");
        rotateItem.addActionListener(e -> applyFilter("Rotate Clockwise"));
        rotateItem.setEnabled(false);
        filterMenu.add(rotateItem);

        redBlueSwapItem = new JMenuItem("Red-Blue Swap");
        redBlueSwapItem.setToolTipText("Swaps the red and blue color channels.");
        redBlueSwapItem.addActionListener(e -> applyFilter("Red-Blue Swap"));
        redBlueSwapItem.setEnabled(false);
        filterMenu.add(redBlueSwapItem);

        gradientItem = new JMenuItem("Blue and Green Gradient");
        gradientItem.setToolTipText("Applies a blue and green gradient from left to right.");
        gradientItem.addActionListener(e -> applyFilter("Blue and Green Gradient"));
        gradientItem.setEnabled(false);
        filterMenu.add(gradientItem);

        sepiaItem = new JMenuItem("Sepia");
        sepiaItem.setToolTipText("Converts the image to a sepia filter.");
        sepiaItem.addActionListener(e -> applyFilter("Sepia"));
        sepiaItem.setEnabled(false);
        filterMenu.add(sepiaItem);
        
        brightnessItem = new JMenuItem("Brightness");
        brightnessItem.setToolTipText("Adjust brightness with the slider.");
        brightnessItem.addActionListener(e -> applyFilter("Brightness"));
        brightnessItem.setEnabled(false);
        filterMenu.add(brightnessItem);
        
        cropItem = new JMenuItem("Crop");
        cropItem.setToolTipText("Crop the image following your mouse.");
        cropItem.addActionListener(e -> applyFilter("Crop"));
        cropItem.setEnabled(false);
        filterMenu.add(cropItem);
        
        undoItem = new JMenuItem("Undo");
        undoItem.setToolTipText("Undo the previously applied filter");
        undoItem.addActionListener(e -> undo());
        undoItem.setEnabled(false);
        fileMenu.add(undoItem);
        
        mirrorItem = new JMenuItem("Mirror");
        mirrorItem.setToolTipText("Applies a vertical mirror filter from the center of the image.");
        mirrorItem.addActionListener(e -> applyFilter("Mirror"));
        mirrorItem.setEnabled(false);
        filterMenu.add(mirrorItem);
        
        menuBar.add(filterMenu);
        setJMenuBar(menuBar);
        
        
        brightnessSlider = new JSlider(-200, 200, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setMinorTickSpacing(10);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.setEnabled(false);
        
        brightnessSlider.addChangeListener(e -> {
        	if (!brightnessSlider.getValueIsAdjusting()) {
        		int brightnessValue = brightnessSlider.getValue();
        		if (currentImage != null) {
        			currentImage.brightnessFilter(brightnessValue);
        			imagePanel = new ImagePanel(currentImage, this);
        			setContentPane(imagePanel);
        			revalidate();
        			repaint();
        		}
        	}
        });
        
        
        sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(new JLabel("Adjust Brightness:"), BorderLayout.NORTH);
        sliderPanel.add(brightnessSlider, BorderLayout.CENTER);
        sliderPanel.setVisible(false);
        
        add(sliderPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	
	private void openImage() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open Image File");
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Image Files (.jpg, .jpeg, .png, .bmp, .gif)",
				"jpg", "jpeg", "png", "bmp", "gif"
	);
	fileChooser.setFileFilter(filter);
	
	int result = fileChooser.showOpenDialog(this);
	if (result == JFileChooser.APPROVE_OPTION) {
		File selected = fileChooser.getSelectedFile();
		
        try {
        	currentImage = new Image(selected.getAbsolutePath());
            
            imagePanel = new ImagePanel(currentImage, this);
            
            int imgWidth = currentImage.getBufferedImage().getWidth();
            int imgHeight = currentImage.getBufferedImage().getHeight();
            imagePanel.setPreferredSize(new Dimension(imgWidth, imgHeight));

            setContentPane(imagePanel);
            pack();
            revalidate();
            repaint();
            
            isImageLoaded = true;
            enableFilterOptions(true);
            saveItem.setEnabled(true);
            
            resetGUI();
            
            undoHistory.clear();
            undoHistory.add(currentImage.copy());
            undoItem.setEnabled(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "No image selected.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
    }
}
	
	
	private void saveImage() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save Filtered Image");

	    FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Image (.jpg)", "jpg");
	    fileChooser.setFileFilter(filter);

	    int result = fileChooser.showSaveDialog(this);

	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();

	        if (!selectedFile.getName().toLowerCase().endsWith(".jpg")) {
	            selectedFile = new File(selectedFile.getAbsolutePath() + ".jpg");
	        }

	        try {
	            BufferedImage bufferedImg = currentImage.getBufferedImage();
	            
	            ImageIO.write(bufferedImg, "jpg", selectedFile);
	            JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(this, "Error saving image.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "Save operation was cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	
	
	public void cropImage(int x1, int y1, int x2, int y2) {
	    if (currentImage != null) {
	    	
	        x1 = Math.max(0, Math.min(x1, currentImage.getBufferedImage().getWidth() - 1));
	        x2 = Math.max(0, Math.min(x2, currentImage.getBufferedImage().getWidth() - 1));
	        y1 = Math.max(0, Math.min(y1, currentImage.getBufferedImage().getHeight() - 1));
	        y2 = Math.max(0, Math.min(y2, currentImage.getBufferedImage().getHeight() - 1));

	        currentImage.cropFilter(x1, y1, x2, y2);

	        imagePanel = new ImagePanel(currentImage, this);

	        JPanel contentPanel = new JPanel(new BorderLayout());
	        contentPanel.add(imagePanel, BorderLayout.CENTER);
	        contentPanel.add(sliderPanel, BorderLayout.SOUTH);
	        
	        undoHistory.add(currentImage.copy());

	        setContentPane(contentPanel);
	        revalidate();
	        repaint();
	    } else {
	        JOptionPane.showMessageDialog(this, "No image loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	}
	
	
	private void undo() {
	    if (undoHistory.size() > 1) {
	    	undoHistory.remove(undoHistory.size() - 1);
	        currentImage = undoHistory.get(undoHistory.size() - 1).copy();
	        
	        imagePanel = new ImagePanel(currentImage, this); 
	        JPanel contentPanel = new JPanel(new BorderLayout());
	        contentPanel.add(imagePanel, BorderLayout.CENTER);
	        contentPanel.add(sliderPanel, BorderLayout.SOUTH);

	        setContentPane(contentPanel);
	        revalidate();
	        repaint();

	        undoItem.setEnabled(undoHistory.size() > 1);
	    }
	}
	
	
    private void applyFilter(String filterName) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        brightnessSlider.setVisible(false);
        sliderPanel.setVisible(false);
        
        boolean filterApplied = false;
        
        switch (filterName) {
            case "Black and White":
                currentImage.blackAndWhiteFilter();
                filterApplied = true;
                break;
            case "Rotate Clockwise":
                currentImage.rotateClockwiseFilter();
                filterApplied = true;
                break;
            case "Red-Blue Swap":
                currentImage.redBlueSwapFilter();
                filterApplied = true;
                break;
            case "Blue and Green Gradient":
                currentImage.blueToGreenGradientFilter();
                filterApplied = true;
                break;
            case "Sepia":
                currentImage.sepiaFilter();
                filterApplied = true;
                break;
            case "Brightness":
            	brightnessSlider.setValue(0);
            	brightnessSlider.setVisible(true);
            	sliderPanel.setVisible(true);
            	filterApplied = true;
            	break;
            case "Crop":
            	if (imagePanel.hasSelection()) {
            		int[] coords = imagePanel.getSelection();
            		cropImage(coords[0], coords[1], coords[2], coords[3]);
            		enableFilterOptions(true);
            		filterApplied = true;
            	}
            	break;
            case "Mirror":
                currentImage.mirrorFilter();
                filterApplied = true;
                break;
        }
        
        
        if (filterApplied) {
            undoHistory.add(currentImage.copy());
            undoItem.setEnabled(undoHistory.size() > 1);
        }
        

        imagePanel = new ImagePanel(currentImage, this);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        contentPanel.add(sliderPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
        revalidate();
        repaint();
        
        saveItem.setEnabled(true);
    }
    
    public void enableFilterOptions(boolean enable) {
        grayscaleItem.setEnabled(enable);
        rotateItem.setEnabled(enable);
        redBlueSwapItem.setEnabled(enable);
        gradientItem.setEnabled(enable);
        sepiaItem.setEnabled(enable);
        brightnessItem.setEnabled(enable);
        cropItem.setEnabled(enable);
        mirrorItem.setEnabled(enable);
    }
    
    
    private void resetGUI() {
        if (currentImage != null) {
            grayscaleItem.setEnabled(true);
            rotateItem.setEnabled(true);
            redBlueSwapItem.setEnabled(true);
            gradientItem.setEnabled(true);
            sepiaItem.setEnabled(true);
            brightnessItem.setEnabled(true);
            saveItem.setEnabled(true);
            brightnessSlider.setEnabled(true);
            cropItem.setEnabled(true);
            mirrorItem.setEnabled(true);
        } else {
            grayscaleItem.setEnabled(false);
            rotateItem.setEnabled(false);
            redBlueSwapItem.setEnabled(false);
            gradientItem.setEnabled(false);
            sepiaItem.setEnabled(false);
            brightnessItem.setEnabled(false);
            saveItem.setEnabled(false);
            brightnessSlider.setEnabled(false);
            cropItem.setEnabled(false);
            mirrorItem.setEnabled(false);
        }
    } 
    
    
    public void enableOnlyCrop() {
    	grayscaleItem.setEnabled(false);
    	rotateItem.setEnabled(false);
    	redBlueSwapItem.setEnabled(false);
    	gradientItem.setEnabled(false);
    	sepiaItem.setEnabled(false);
    	brightnessItem.setEnabled(false);
    	cropItem.setEnabled(true);
    	mirrorItem.setEnabled(false);
    }

    public void disableAllFilters() {
    	grayscaleItem.setEnabled(false);
    	rotateItem.setEnabled(false);
    	redBlueSwapItem.setEnabled(false);
    	gradientItem.setEnabled(false);
    	sepiaItem.setEnabled(false);
    	brightnessItem.setEnabled(false);
    	cropItem.setEnabled(false);
    	mirrorItem.setEnabled(false);
    }
}