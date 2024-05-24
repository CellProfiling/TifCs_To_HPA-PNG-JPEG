package tifCs2hpaPngJpeg_jnh;

/** ===============================================================================
* TifChannels_to_HPA_PNG_JPEG_Main_JNH ImageJ/FIJI Plugin v0.0.4
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation (http://www.gnu.org/licenses/gpl.txt )
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*  
* See the GNU General Public License for more details.
*  
* Copyright (C) Jan Niklas Hansen
* Date: August 07, 2023 (This Version: May 24, 2024)
*   
* For any questions please feel free to contact me (jan.hansen@scilifelab.se).
* =============================================================================== */

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.WaitForUserDialog;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

public class TifCs_to_HPA_PNG_JPEG_Main implements PlugIn {
	// Name variables
	static final String PLUGINNAME = "TifChannels_to_HPA_PNG_JPEG_Main_JNH";
	static final String PLUGINVERSION = "0.0.4";

	// Fix fonts
	static final Font SuperHeadingFont = new Font("Sansserif", Font.BOLD, 16);
	static final Font HeadingFont = new Font("Sansserif", Font.BOLD, 14);
	static final Font SubHeadingFont = new Font("Sansserif", Font.BOLD, 12);
	static final Font TextFont = new Font("Sansserif", Font.PLAIN, 12);
	static final Font InstructionsFont = new Font("Sansserif", 2, 12);
	static final Font RoiFont = new Font("Sansserif", Font.PLAIN, 20);

	// Fix formats
	DecimalFormat dformat6 = new DecimalFormat("#0.000000");
	DecimalFormat dformat3 = new DecimalFormat("#0.000");
	DecimalFormat dformat0 = new DecimalFormat("#0");
	DecimalFormat dformatDialog = new DecimalFormat("#0.000000");

	static final String[] nrFormats = { "US (0.00...)", "Germany (0,00...)" };

	static SimpleDateFormat NameDateFormatter = new SimpleDateFormat("yyMMdd_HHmmss");
	static SimpleDateFormat FullDateFormatter = new SimpleDateFormat("yyyy-MM-dd	HH:mm:ss");
	static SimpleDateFormat FullDateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Progress Dialog
	ProgressDialog progress;
	boolean processingDone = false;
	boolean continueProcessing = true;
	
	// -----------------define params for Dialog-----------------
	int tasks = 1;

	String inPath = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "InputFolders"
			+ System.getProperty("file.separator");
	String outPath = "E:" + System.getProperty("file.separator") + System.getProperty("file.separator") + "OuputFolders"
			+ System.getProperty("file.separator");
	
	boolean outputPNGs = false, outputJPGs = true, autoAdjustIntensities = true, joinedAdjustment = true;
	boolean selectChannelOutputs = false;
	
	String greenFileEnd = "C3.tif", blueFileEnd = "C0.tif", redFileEnd = "C4.tif", yellowFileEnd = "C1.tif", whiteFileEnd = "C2.tif";
	
	boolean diagnosisLogging = false;	

	boolean blueOut = true, greenOut = true, redOut = true, yellowOut = true,
			blue_greenOut = true, blue_redOut = true, blue_yellowOut = true,
			green_redOut = true, green_yellowOut = true, 
			red_yellowOut = true, 
			blue_green_redOut = true, blue_red_yellowOut = true, blue_green_yellowOut = true,
			blue_green_red_yellowOut = true,
			whiteOut = true, blue_green_red_yellow_whiteOut = true;
	
	// -----------------define params for Dialog-----------------
	
	@Override
	public void run(String arg) {
		//TODO Remove
		inPath = "C:"+ System.getProperty("file.separator") +"Users"+ System.getProperty("file.separator") +"jan.hansen"+ System.getProperty("file.separator") 
			+"Desktop"+ System.getProperty("file.separator") +"ExampleTifs"+ System.getProperty("file.separator");
		outPath = "C:"+ System.getProperty("file.separator") +"Users"+ System.getProperty("file.separator") +"jan.hansen"+ System.getProperty("file.separator") 
			+ "Desktop"+ System.getProperty("file.separator") +"ExampleJpegs"+ System.getProperty("file.separator");

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ------------------------------INITIALIZATIONS-------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		dformat6.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformat3.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformat0.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dformatDialog.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// --------------------------REQUEST USER-SETTINGS-----------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		GenericDialog gd = new GenericDialog(PLUGINNAME + " - set parameters");	
		//show Dialog-----------------------------------------------------------------
		gd.setInsets(0,0,0);	gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2023 JN Hansen", SuperHeadingFont);	
		gd.addHelp("https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/");

		gd.setInsets(0,0,0);	gd.addMessage("Notes", SubHeadingFont);
		
		gd.setInsets(0,0,0);		gd.addMessage("The plugin inputs a folder structure with a folder for each individual image containing single-channel tifs", InstructionsFont);
		gd.setInsets(0,0,0);		gd.addMessage("and outputs a transformed folder structure with a folder for each individual image containing", InstructionsFont);
		gd.setInsets(0,0,0);		gd.addMessage("classical single- or multi-channel HPA-style JPEGs and PNGs.", InstructionsFont);
					
		gd.setInsets(10,0,0);	gd.addMessage("I/O Settings", SubHeadingFont);

		gd.setInsets(0,0,0);		gd.addStringField("Input folder: specify filepath here", inPath, 40);
		gd.setInsets(0,0,0);		gd.addStringField("Output folder: specify filepath here", outPath, 40);
		
		
		gd.setInsets(0,0,0);		gd.addCheckbox("Output pngs", outputPNGs);
		gd.setInsets(0,0,0);		gd.addCheckbox("Output jpegs", outputJPGs);
		gd.setInsets(0,0,0);		gd.addCheckbox("Customize channel overlays to be created and saved (extra settings dialog is displayed after this)",
				selectChannelOutputs);
				

		gd.setInsets(10,0,0);	gd.addMessage("Enhancement settings", SubHeadingFont);
		gd.setInsets(0,0,0);		gd.addCheckbox("Autoadjust channel intensities", autoAdjustIntensities);
		gd.setInsets(0,0,0);		gd.addMessage("If autoadjust function is used, for each individual image and channel, the 99.999% percentile is determined and", InstructionsFont);
		gd.setInsets(-5,0,0);		gd.addMessage("set as the maximum display value to rescale the look up tables to a reasonable range. If the 99.999% percentile is", InstructionsFont);
		gd.setInsets(-5,0,0);		gd.addMessage("smaller than 20000.0, the maximum display value is set to be 20000.0 to avoid overamplification of signals in empty", InstructionsFont);
		gd.setInsets(-5,0,0);		gd.addMessage("images. Also, when the autoadjust function is activated, the minimum display value is automatically set to 0.0.", InstructionsFont);

		gd.setInsets(0,0,0);		gd.addCheckbox("Use adjustment file to auto-adjust images together", joinedAdjustment);
		
		
		gd.setInsets(10,0,0);	gd.addMessage("Channel assignments", SubHeadingFont);
		gd.setInsets(0,0,0);		gd.addMessage("Specify here the file-endings for the individual channel .tif files", InstructionsFont);
		
		gd.setInsets(0,0,0);		gd.addStringField("Green channel - file ending (including .tif(f)):", greenFileEnd, 30);
		gd.setInsets(0,0,0);		gd.addStringField("Blue channel - file ending (including .tif(f)):", blueFileEnd, 30);
		gd.setInsets(0,0,0);		gd.addStringField("Red channel - file ending (including .tif(f)):", redFileEnd, 30);
		gd.setInsets(0,0,0);		gd.addStringField("Yellow channel - file ending (including .tif(f)):", yellowFileEnd, 30);
		gd.setInsets(0,0,0);		gd.addStringField("White channel (Brightfield or TI, only if applicable) - file ending (including .tif(f)):", whiteFileEnd, 30);
				
		gd.setInsets(10,0,0);	gd.addMessage("Extended modes", SubHeadingFont);
		gd.setInsets(0,0,0);		gd.addCheckbox("Extended logging for diagnosis of errors", diagnosisLogging);		
				
		gd.showDialog();
		//show Dialog-----------------------------------------------------------------

		//read and process variables--------------------------------------------------	
		inPath = gd.getNextString();
		outPath = gd.getNextString();
		outputPNGs = gd.getNextBoolean();
		outputJPGs = gd.getNextBoolean();
		selectChannelOutputs = gd.getNextBoolean();
		autoAdjustIntensities = gd.getNextBoolean();
		joinedAdjustment = gd.getNextBoolean();		
		greenFileEnd = gd.getNextString();
		blueFileEnd = gd.getNextString();
		redFileEnd = gd.getNextString();
		yellowFileEnd = gd.getNextString();
		whiteFileEnd = gd.getNextString();
		
		diagnosisLogging = gd.getNextBoolean();
		//read and process variables--------------------------------------------------
		if (gd.wasCanceled()) return;
				
		if(selectChannelOutputs){
			GenericDialog gd2 = new GenericDialog(PLUGINNAME + " - set parameters");	

			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue only", blueOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Green only", greenOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Red only", redOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Yellow only", yellowOut);
			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("Blue & Green", blue_greenOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue & Red", blue_redOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue & Yellow", blue_yellowOut);
			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("Green & Red", green_redOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Green & Yellow", green_yellowOut);
			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("Red & Yellow", red_yellowOut);
			
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue & Green & Red", blue_green_redOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue & Red & Yellow", blue_red_yellowOut);
			gd2.setInsets(0,0,0);		gd2.addCheckbox("Blue & Green & Yellow", blue_green_yellowOut);
			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("Blue & Green & Red & Yellow", blue_green_red_yellowOut);
			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("White (Brightfield, TI, phase contrast)", whiteOut);			
			gd2.setInsets(5,0,0);		gd2.addCheckbox("Blue & Green & Red & Yellow & White", blue_green_red_yellow_whiteOut);
						
			gd2.showDialog();
			
			blueOut = gd2.getNextBoolean();
			greenOut = gd2.getNextBoolean();
			redOut = gd2.getNextBoolean();
			yellowOut = gd2.getNextBoolean();
			
			blue_greenOut = gd2.getNextBoolean();
			blue_redOut = gd2.getNextBoolean();
			blue_yellowOut = gd2.getNextBoolean();
			
			green_redOut = gd2.getNextBoolean();
			green_yellowOut = gd2.getNextBoolean();
			
			red_yellowOut = gd2.getNextBoolean();
			
			blue_green_redOut = gd2.getNextBoolean();
			blue_red_yellowOut = gd2.getNextBoolean();
			blue_green_yellowOut = gd2.getNextBoolean();
			
			blue_green_red_yellowOut = gd2.getNextBoolean();

			whiteOut = gd2.getNextBoolean();
			blue_green_red_yellow_whiteOut = gd2.getNextBoolean();			
						
			if (gd2.wasCanceled()) return;
		}
		
		String tableFileDir = "";
		String tableFileName = "";
		String [][] adjustmentLookUpTable = null;
		if(joinedAdjustment) {
			//Open a file open dialog to load a csv file containing information on the wells that should be adjusted together
			new WaitForUserDialog("Please open a table .csv file with columns Well,AdjustmentGroup in the following dialog!\n"
					+"Make sure there is no duplicate entries in the well column and that you provide only information for one plate (the specific plate analyzed)!\n"
					+"The Well needs to be referred to as in the filenames (e.g., 'A1').\n"
					+"In adjustment group, use integer numbers ('1','2'). All wells assigned the same number, will be adjusted together.\n"
					+ "If you want to adjust only by well, just give each well a different adjustment number.").show();
						
	    	OpenDialog odTable;
	    	odTable = new OpenDialog("Open table file with columns Antibody,Protein,Plate,Well", null);
	    	tableFileDir = odTable.getDirectory();
    		tableFileName = odTable.getFileName();
    		adjustmentLookUpTable = getTableFromCSV(tableFileDir + System.getProperty("file.separator") + tableFileName);
    		if(adjustmentLookUpTable.length==0) {
    			return;
    		}
    		if(diagnosisLogging) {
    			IJ.log("Log of read in csv file:");
    			String out = "";
    			for(int i = 0; i < adjustmentLookUpTable[0].length; i++) {
    				out = "";
    				for(int n = 0; n < adjustmentLookUpTable.length; n++) {
    					out += adjustmentLookUpTable[0][i] + "	";
    				}
    				out = out.substring(0,out.length()-1);
            		IJ.log(out);
    			}		
    		}
		}
		
		// add progressDialog
		progress = new ProgressDialog(new String[]{"" + new File(inPath).getName()});
		progress.setLocation(0, 0);
		progress.setVisible(true);
		progress.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (processingDone == false) {
					IJ.error("Script stopped...");
				}
				continueProcessing = false;
				return;
			}
		});

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// -------------------VALIDATE AND EXPLORE INPUT FOLDERS-----------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		LinkedList<String> allFiles = validateFoldersAndCreateFileList(inPath);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ---------------------------------RUN TASKS----------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		
		String greenFile = "", blueFile = "", redFile = "", yellowFile = "", whiteFile = "";
		ImagePlus imp, impGreen, impBlue, impRed, impYellow, impWhite = null;
		CompositeImage cImp;
		String newFilePath;
		File outDir;
		
		
		running: while (continueProcessing) {
			for(int d = 0; d < allFiles.size(); d++){
				progress.updateBarText("processing files in progress... (file " + (d+1) + " / " + allFiles.size() + ")");

				File currFolder = new File(allFiles.get(d));				
				String [] fileList = currFolder.list();
				
				//Find file path
				for (int f = 0; f < fileList.length; f++) {
					if (fileList[f].endsWith(greenFileEnd)) {
						greenFile = fileList [f];						
					} else if (fileList[f].endsWith(blueFileEnd)) {
						blueFile = fileList [f];
					} else if (fileList[f].endsWith(redFileEnd)) {
						redFile = fileList [f];
					} else if (fileList[f].endsWith(yellowFileEnd)) {
						yellowFile = fileList [f];
					} else if (blue_green_red_yellow_whiteOut || whiteOut) {
						if(fileList[f].endsWith(whiteFileEnd)) {
							whiteFile = fileList [f];
						}						
					}
				}
				
				if(greenFile.length() > 0 && blueFile.length() > 0 && redFile.length() > 0 && yellowFile.length() > 0) {
					//Open images
					impGreen = IJ.openImage(currFolder.getAbsolutePath() + System.getProperty("file.separator") + greenFile);
					impBlue = IJ.openImage(currFolder.getAbsolutePath() + System.getProperty("file.separator") + blueFile);
					impRed = IJ.openImage(currFolder.getAbsolutePath() + System.getProperty("file.separator") + redFile);
					impYellow = IJ.openImage(currFolder.getAbsolutePath() + System.getProperty("file.separator") + yellowFile);
					if(blue_green_red_yellow_whiteOut || whiteOut) {
						impWhite = IJ.openImage(currFolder.getAbsolutePath() + System.getProperty("file.separator") + whiteFile);
					}
					
					//Create image
					if(blue_green_red_yellow_whiteOut || whiteOut){
						imp = IJ.createHyperStack("Merged File", impGreen.getWidth(), impGreen.getHeight(), 5, 1, 1, impGreen.getBitDepth());
					}else {
						imp = IJ.createHyperStack("Merged File", impGreen.getWidth(), impGreen.getHeight(), 4, 1, 1, impGreen.getBitDepth());		   				
					}
					imp.setDisplayMode(IJ.COMPOSITE);
					
					//Transfer pixels
					for(int x = 0; x < imp.getWidth(); x++){
						for(int y = 0; y < imp.getHeight(); y++){
							imp.getStack().setVoxel(x, y, imp.getStackIndex(1, 1, 1)-1, 
									impGreen.getStack().getVoxel(x, y, 0));
							imp.getStack().setVoxel(x, y, imp.getStackIndex(2, 1, 1)-1, 
									impBlue.getStack().getVoxel(x, y, 0));
							imp.getStack().setVoxel(x, y, imp.getStackIndex(3, 1, 1)-1, 
									impRed.getStack().getVoxel(x, y, 0));
							imp.getStack().setVoxel(x, y, imp.getStackIndex(4, 1, 1)-1, 
									impYellow.getStack().getVoxel(x, y, 0));
							if(blue_green_red_yellow_whiteOut || whiteOut){
								imp.getStack().setVoxel(x, y, imp.getStackIndex(5, 1, 1)-1, 
										impWhite.getStack().getVoxel(x, y, 0));
							}
						}
					}
					
					//Transfer metadata from Green image
					imp.setCalibration(impGreen.getCalibration());
					
					//Switch to composite image
					cImp = (CompositeImage) imp.duplicate();
					cImp.setDisplayMode(IJ.COMPOSITE);
				    cImp.setActiveChannels("11111");
				    
				    //Setup channels
					cImp.setC(1);
					IJ.run(cImp, "Green", "");
					cImp.setDisplayRange(impGreen.getDisplayRangeMin(),impGreen.getDisplayRangeMax());

					cImp.setC(2);
					IJ.run(cImp, "Blue", "");
					cImp.setDisplayRange(impBlue.getDisplayRangeMin(),impBlue.getDisplayRangeMax());					

					cImp.setC(3);
					IJ.run(cImp, "Red", "");
					cImp.setDisplayRange(impRed.getDisplayRangeMin(),impRed.getDisplayRangeMax());
					
					cImp.setC(4);
					IJ.run(cImp, "Yellow", "");
					cImp.setDisplayRange(impYellow.getDisplayRangeMin(),impYellow.getDisplayRangeMax());
					
					if(blue_green_red_yellow_whiteOut || whiteOut){
						cImp.setC(5);
						IJ.run(cImp, "Grays", "");
						cImp.setDisplayRange(impWhite.getDisplayRangeMin(),impWhite.getDisplayRangeMax());						
					}
					
					if(autoAdjustIntensities) {
						autoAdjustDisplayRange(cImp,1, 20000.0);
						autoAdjustDisplayRange(cImp,2, 20000.0);
						autoAdjustDisplayRange(cImp,3, 20000.0);
						autoAdjustDisplayRange(cImp,4, 20000.0);	
						if(blue_green_red_yellow_whiteOut || whiteOut){
							//TODO create adjustment for white channel
						}			
					}
					
					newFilePath = currFolder.getAbsolutePath();
					newFilePath = newFilePath.substring(newFilePath.indexOf(inPath)+inPath.length());
					newFilePath = outPath + System.getProperty("file.separator") + newFilePath;
					if(diagnosisLogging) {
						progress.notifyMessage("Output path for <" + currFolder.getAbsolutePath() + ">:\n" + newFilePath + "", ProgressDialog.LOG);
					}
					outDir = new File(newFilePath);
					if(!outDir.exists()) {
						outDir.mkdirs();
					}
					
					String newFileName = greenFile.substring(0,greenFile.lastIndexOf(greenFileEnd));
					if(diagnosisLogging) {
						progress.notifyMessage("Filename post end removal: " + newFileName + "", ProgressDialog.LOG);
					}
					if(newFileName.length()==0) {
						newFileName = currFolder.getName();
						if(newFileName.length()<5) {
							newFileName = currFolder.getParentFile().getName() + "_" + newFileName;
						}
						
						if(diagnosisLogging) {
							progress.notifyMessage("Filename by director(ies): " + newFileName + "", ProgressDialog.LOG);
						}
					}
					
					if(outputPNGs) {
						outputImage(cImp, "PNG", ".png", newFilePath, newFileName);
					}
					if(outputJPGs) {
						outputImage(cImp, "JPG", ".jpg", newFilePath, newFileName);
					}
					

					imp.changes = false;
					imp.close();
					
					cImp.changes = false;
					cImp.close();
					
				}else {
					if(diagnosisLogging) {
						progress.notifyMessage("Incorrect directory discovered and skipped: " + currFolder.getAbsolutePath() + "", ProgressDialog.NOTIFICATION);
					}
				}
				System.gc();
			}
			
			allFiles.clear();
			allFiles = null;
			processingDone = true;
			progress.updateBarText("finished!");
			progress.setBar(1.0);
			break running;
		}
		progress.moveTask(0);
		System.gc();
	}	
	
	/**
	 * This function explores the folder system in the @param directoryPath and finds all folders containing a set of .tif files suitable for processing.
	 * @return a LinkedList with Strings representing filepaths to folders containing all tif files neeeded for conversion
	 */
	private LinkedList<String> validateFoldersAndCreateFileList(String directoryPath) {
		LinkedList<String> allFiles = new LinkedList<String>();
		{
			LinkedList<String> FoldersToCheck = new LinkedList<String>();
			FoldersToCheck.add(directoryPath);
			
			boolean red, green, blue, yellow, white, skip;
			
			progress.updateBarText("Scanning file system for image folders ...");
			while(FoldersToCheck.size()>0){
				//Initialize folder variables
				File currFolder = new File(FoldersToCheck.getFirst());				
				String [] fileList = currFolder.list();
				green = false;
				blue = false;
				red = false;
				yellow = false;
				white = false;
				skip = false;
				
				//Scan files and dirs in folder
				for (int f = 0; f < fileList.length; f++) {
					/**
					 * Now, the script scans through all file names in the folder and verifies if
					 * they are tif files and if so it checks whether they are named correctly.
					 */
					
					if (fileList[f] == greenFileEnd || fileList[f].endsWith(greenFileEnd)) {
						if(green) {
							if(diagnosisLogging) {
								progress.notifyMessage("Ambigous files for green channel in\n" + currFolder.getPath() + "", ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						green = true;
						
					} else if (fileList[f] == blueFileEnd || fileList[f].endsWith(blueFileEnd)) {
						if(blue) {
							if(diagnosisLogging) {
								progress.notifyMessage("Ambigous files for blue channel in\n" + currFolder.getPath() + "", ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						blue = true;
					} else if (fileList[f] == redFileEnd || fileList[f].endsWith(redFileEnd)) {
						if(red) {
							if(diagnosisLogging) {
								progress.notifyMessage("Ambigous files for red channel in\n" + currFolder.getPath() + "", ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						red = true;
					} else if (fileList[f] == yellowFileEnd || fileList[f].endsWith(yellowFileEnd)) {
						if(yellow) {
							if(diagnosisLogging) {
								progress.notifyMessage("Ambigous files for yellow channel in\n" + currFolder.getPath() + "", ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						yellow = true;
					} else if ((blue_green_red_yellow_whiteOut || whiteOut) && (fileList[f] == whiteFileEnd || fileList[f].endsWith(whiteFileEnd))) {
						if(white) {
							if(diagnosisLogging) {
								progress.notifyMessage("Ambigous files for white channel in\n" + currFolder.getPath() + "", ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						white = true;
					} else if (new File(currFolder.getAbsolutePath() + System.getProperty("file.separator") + fileList[f]).isDirectory()){
						FoldersToCheck.add(currFolder.getAbsolutePath() + System.getProperty("file.separator") + fileList[f]);
						if(diagnosisLogging) {
							progress.notifyMessage("Folder added to todo list: " + currFolder.getPath() + System.getProperty("file.separator") + fileList[f], ProgressDialog.LOG);
						}						
					} else {
						if(diagnosisLogging) {
							progress.notifyMessage("Could not assign file " + fileList [f] + "", ProgressDialog.LOG);
						}
					}
				}
				
				//Add if contains all files needed
				if(green && blue && red && yellow && skip == false) {
					if((blue_green_red_yellow_whiteOut || whiteOut) && !white) {
						progress.notifyMessage("Skipped a folder since missing brightfield image file ending with " + whiteFileEnd + " (Folder: " + currFolder.getPath() + ").", ProgressDialog.NOTIFICATION);
					}else {
						allFiles.add(currFolder.getAbsolutePath());
						if(diagnosisLogging) {
							progress.notifyMessage("Added folder to process list: " + currFolder.getPath() + "", ProgressDialog.LOG);
						}
					}
				}
				
				//Remove last element to move on to next folder in list
				FoldersToCheck.removeFirst();
				if(diagnosisLogging) {
					progress.notifyMessage("Removed first element to move on - remaining nr of elements " + FoldersToCheck.size(), ProgressDialog.LOG);
				}
				progress.updateBarText("Scanning file system for image folders ... found " + FoldersToCheck.size() + " image folders so far.");
			}
			FoldersToCheck.clear();
			FoldersToCheck = null;
			System.gc();
		}
		return allFiles;
	}
	
	
	/**
	 * @param: 0 < channel <= nr of channels
	 * */
	private void autoAdjustDisplayRange(CompositeImage imp, int channel, double minimumMaxValue) {
		imp.setC(channel);
		double max = getMinMaxPercentInImage(imp, channel, 0.001) [1];
		if(max < minimumMaxValue) {
			max = minimumMaxValue;
		}
		imp.setDisplayRange(0.0, max);
		
		if(diagnosisLogging) {
			progress.notifyMessage("Adjusted channel " + channel + " to [" + 0.0 + "," + max 
				+ "] by determining percentile 0.0001 - in image processor: [" + imp.getProcessor().getMin() + "," + imp.getProcessor().getMax() + "]", ProgressDialog.LOG);
		}
	}

	/**
	 * @param: 0 < channel <= nr of channels
	 * */
	private double [] getMinMaxPercentInImage(CompositeImage imp, int channel, double percent) {
		ArrayList<Double> pixels = new ArrayList<Double>(imp.getWidth()*imp.getHeight());
		for(int x = 0; x < imp.getWidth(); x++){
			for(int y = 0; y < imp.getHeight(); y++){
				pixels.add(imp.getStack().getVoxel(x, y, imp.getStackIndex(channel,1,1)-1));
			}
		}
		Collections.sort(pixels);
		
		int indexMin = (int) Math.round((double) pixels.size() * percent / 100.0);
		int indexMax = (int) Math.round((double) pixels.size() * (100.0-percent) / 100.0);
		double out [] = new double [] {pixels.get(indexMin-1),pixels.get(indexMax-1)};
		pixels.clear();
		pixels = null;		
		return out;
	}
	
	private void outputImage (CompositeImage imp, String fileType, String fileEnding, String dir, String namePrefix) {
		if(greenOut) {
			imp.setActiveChannels("10000");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_green" + fileEnding);			
		}
		
		if(blueOut) {
			imp.setActiveChannels("01000");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue" + fileEnding);
		}

		if(redOut) {
			imp.setActiveChannels("00100");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_red" + fileEnding);
		}
		
		if(yellowOut) {
			imp.setActiveChannels("00010");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_yellow" + fileEnding);			
		}
		
		if(blue_greenOut) {
			imp.setActiveChannels("11000");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_green" + fileEnding);			
		}
		
		if (blue_redOut) {
			imp.setActiveChannels("01100");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_red" + fileEnding);			
		}
		
		if(blue_yellowOut) {
			imp.setActiveChannels("01010");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_yellow" + fileEnding);
		}

		if(green_redOut) {
			imp.setActiveChannels("10100");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_red_green" + fileEnding);		
		}

		if(green_yellowOut) {
			imp.setActiveChannels("10010");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_green_yellow" + fileEnding);		
		}

		if(red_yellowOut) {
			imp.setActiveChannels("00110");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_red_yellow" + fileEnding);		
		}
				
		if(blue_green_redOut) {
			imp.setActiveChannels("11100");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_red_green" + fileEnding);		
		}
		
		if(blue_red_yellowOut) {
			imp.setActiveChannels("01110");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_red_yellow" + fileEnding);
		}
		
		if(blue_green_yellowOut) {
			imp.setActiveChannels("11010");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_green_yellow" + fileEnding);		
		}
		
		if(blue_green_red_yellowOut) {
			imp.setActiveChannels("11110");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_red_green_yellow" + fileEnding);
		}

		if(whiteOut) {
			imp.setActiveChannels("00001");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_white" + fileEnding);
		}
		
		if(blue_green_red_yellow_whiteOut) {
			imp.setActiveChannels("11111");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue_red_green_yellow_white" + fileEnding);
		}
		
	}
	

	private static String [][] getTableFromCSV(String filePath){
		try {
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			LinkedList<String> lines = new LinkedList<String>();
			reading: while(true){
				try{
					line = br.readLine();
					if(line.equals(null)){
						break reading;
					}
					lines.add(line);
				}catch(Exception e){
					break reading;
				}				
			}			
			br.close();
			fr.close();
			
			int nrOfCols = getNumberOfPatternsInString(lines.get(0),",")+1;			
			String [][] out = new String [nrOfCols][lines.size()];
			
			for(int i = 0; i < out[0].length; i++) {
				line = lines.get(i);
				for(int n = nrOfCols-1; n > 0; n++) {
					out [n][i] = line.substring(line.lastIndexOf(",")+1);
					line = line.substring(0, line.lastIndexOf(","));
				}
				out [0][i] = line;
				
				if(line.contains(",")) {
					IJ.error("Processing failed - loaded table file contained variying numbers of columns in each row.\nMake sure to load a comma-delimited, intact csv file!");
					return new String [0][0];
				}
			}
			lines.clear();
			lines = null;
			
			return out;			
		}catch (IOException e) {
			IJ.error("Loading table file failed!");
			e.printStackTrace();
			return new String [0][0];
		}
	}
	
	private static int getNumberOfPatternsInString(String text, String pattern) {
		int n = 0;
		while(text.contains(pattern)) {
			if(text.lastIndexOf(pattern) == -1) {
				break;
			}
			n +=1;			
			text = text.substring(0, text.lastIndexOf(pattern));
		}
		return n;
	}
}// end main class