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
* Date: August 07, 2023 (This Version: June 15, 2024)
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
import java.util.Arrays;
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

//	String greenFileEnd = "C03.ome.tif", blueFileEnd = "C00.ome.tif", redFileEnd = "C04.ome.tif",
//			yellowFileEnd = "C01.ome.tif", whiteFileEnd = "C02.ome.tif";
	
	String greenFileEnd = "C3.tif", blueFileEnd = "C0.tif", redFileEnd = "C4.tif",
			yellowFileEnd = "C1.tif", whiteFileEnd = "C2.tif";

	boolean diagnosisLogging = false;

	boolean blueOut = true, greenOut = true, redOut = true, yellowOut = true, blue_greenOut = true, blue_redOut = true,
			blue_yellowOut = true, green_redOut = true, green_yellowOut = true, red_yellowOut = true,
			blue_green_redOut = true, blue_red_yellowOut = true, blue_green_yellowOut = true,
			blue_green_red_yellowOut = true, whiteOut = true, blue_green_red_yellow_whiteOut = true;

	double percentage = 0.001;
	double mergePercentage = 20.0;
	double minMaxAllowedValue = 10000;
	
	// -----------------define params for Dialog-----------------

	@Override
	public void run(String arg) {
		
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
		// show Dialog-----------------------------------------------------------------
		gd.setInsets(0, 0, 0);
		gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2023 JN Hansen", SuperHeadingFont);
		gd.addHelp("https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/");

		gd.setInsets(0, 0, 0);
		gd.addMessage("Notes", SubHeadingFont);

		gd.setInsets(0, 0, 0);
		gd.addMessage(
				"The plugin inputs a folder structure with a folder for each individual image containing single-channel tifs",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage("and outputs a transformed folder structure with a folder for each individual image containing",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage("classical single- or multi-channel HPA-style JPEGs and PNGs.", InstructionsFont);

		gd.setInsets(5, 0, 0);
		gd.addMessage("I/O Settings", SubHeadingFont);

		gd.setInsets(0, 0, 0);
		gd.addStringField("Input folder: specify filepath here", inPath, 40);
		gd.setInsets(0, 0, 0);
		gd.addStringField("Output folder: specify filepath here", outPath, 40);

		gd.setInsets(0, 0, 0);
		gd.addCheckbox("Output pngs", outputPNGs);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("Output jpegs", outputJPGs);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox(
				"Customize channel overlays to be created and saved (extra settings dialog is displayed after this)",
				selectChannelOutputs);

		gd.setInsets(5, 0, 0);
		gd.addMessage("Enhancement settings", SubHeadingFont);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("Autoadjust channel intensities", autoAdjustIntensities);
		gd.setInsets(0, 0, 0);
		gd.addNumericField("-> Intensity percentile (see info below)", percentage, 3);
		gd.setInsets(0, 0, 0);
		gd.addNumericField("-> Minimum allowed maximum display value (see info below)", minMaxAllowedValue, 0);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("-> Use adjustment file to auto-adjust images together", joinedAdjustment);
		gd.setInsets(0, 0, 0);
		gd.addNumericField("--> Combining percentile (see info below)", mergePercentage, 3);
		
		gd.setInsets(0, 0, 0);
		gd.addMessage(
				"If autoadjust function is used, for each individual image and channel, the upper and lower X% percentiles (X is determined",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"by user parameter 'Intensity percentile' (e.g., if 0.01% is set as 'Intensity percentile', the min display value will be",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"determined based on the 0.01% percentile and the maximum display value will be determined based on the 99.99% percentile.",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"The 'Combining percentile' parameter determines how the minimum / maximum display value is computed from the percentiles",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"of all images in one group (if adjustment 'together' is activated). E.g., when set to 50% the median of all min and max values.",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"will be applied. If 20% is selected, the 20% and 80% percentiles will be used to merge the minimum and maximum display values,",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"respectively. Use the 'Minimum allowed maximum...' parameter to limit how low the maximum display value is allowed to drop,",
				InstructionsFont);
		gd.setInsets(-5, 0, 0);
		gd.addMessage(
				"to make sure that negative-staining images are not overenhanced!",
				InstructionsFont);
		
		gd.setInsets(5, 0, 0);
		gd.addMessage("Channel assignments", SubHeadingFont);
		gd.setInsets(0, 0, 0);
		gd.addMessage("Specify here the file-endings for the individual channel .tif files", InstructionsFont);

		gd.setInsets(0, 0, 0);
		gd.addStringField("Green channel - file ending (including .tif(f)):", greenFileEnd, 30);
		gd.setInsets(0, 0, 0);
		gd.addStringField("Blue channel - file ending (including .tif(f)):", blueFileEnd, 30);
		gd.setInsets(0, 0, 0);
		gd.addStringField("Red channel - file ending (including .tif(f)):", redFileEnd, 30);
		gd.setInsets(0, 0, 0);
		gd.addStringField("Yellow channel - file ending (including .tif(f)):", yellowFileEnd, 30);
		gd.setInsets(0, 0, 0);
		gd.addStringField("White channel (Brightfield or TI, only if applicable) - file ending (including .tif(f)):",
				whiteFileEnd, 30);

		gd.setInsets(5, 0, 0);
		gd.addMessage("Extended modes", SubHeadingFont);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("Extended logging for diagnosis of errors", diagnosisLogging);

		gd.showDialog();
		// show Dialog-----------------------------------------------------------------

		// read and process variables--------------------------------------------------
		inPath = gd.getNextString();
		outPath = gd.getNextString();
		outputPNGs = gd.getNextBoolean();
		outputJPGs = gd.getNextBoolean();
		selectChannelOutputs = gd.getNextBoolean();
		autoAdjustIntensities = gd.getNextBoolean();
		percentage = gd.getNextNumber();
		minMaxAllowedValue = gd.getNextNumber();
		joinedAdjustment = gd.getNextBoolean();
		mergePercentage = gd.getNextNumber();
		greenFileEnd = gd.getNextString();
		blueFileEnd = gd.getNextString();
		redFileEnd = gd.getNextString();
		yellowFileEnd = gd.getNextString();
		whiteFileEnd = gd.getNextString();

		diagnosisLogging = gd.getNextBoolean();
		// read and process variables--------------------------------------------------
		if (gd.wasCanceled())
			return;

		if (selectChannelOutputs) {
			GenericDialog gd2 = new GenericDialog(PLUGINNAME + " - set parameters");

			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue only", blueOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Green only", greenOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Red only", redOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Yellow only", yellowOut);

			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("Blue & Green", blue_greenOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue & Red", blue_redOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue & Yellow", blue_yellowOut);

			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("Green & Red", green_redOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Green & Yellow", green_yellowOut);

			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("Red & Yellow", red_yellowOut);

			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue & Green & Red", blue_green_redOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue & Red & Yellow", blue_red_yellowOut);
			gd2.setInsets(0, 0, 0);
			gd2.addCheckbox("Blue & Green & Yellow", blue_green_yellowOut);

			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("Blue & Green & Red & Yellow", blue_green_red_yellowOut);

			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("White (Brightfield, TI, phase contrast)", whiteOut);
			gd2.setInsets(5, 0, 0);
			gd2.addCheckbox("Blue & Green & Red & Yellow & White", blue_green_red_yellow_whiteOut);

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

			if (gd2.wasCanceled())
				return;
		}

		String tableFileDir = "";
		String tableFileName = "";
		String[][] adjustmentLookUpTable = null;
		if (joinedAdjustment) {
			// Open a file open dialog to load a csv file containing information on the
			// wells that should be adjusted together
			new WaitForUserDialog(
					"The plugin assumes a file structure as follows:\n"
					+ "<InputPath>|<WellID, e.g., 'C5' or 'HPA..._Genename'|<imagefolder>|<z plane folder>|<single channel tif file>\n\n"
					+ "Please open a table .csv file with columns WellID,AdjustmentGroup in the following dialog!\n"
					+ "Make sure there is no duplicate entries in the WellID column!\n"
					+ "The WellID needs to refer to the folder name as explained in the file structure above (e.g., 'A1' if coming from OPERA,\n"
					+ "or 'Antibody_Gene' if coming from lif files).\n"
					+ "In adjustment group, use integer numbers ('1','2'). All wells/topfolders assigned the same number, will be adjusted together.\n"
					+ "If you want to adjust only by well, just give each well a different adjustment number.")
					.show();

			OpenDialog odTable;
			odTable = new OpenDialog("Open table file with columns Well,AdjustmentGroup", null);
			tableFileDir = odTable.getDirectory();
			tableFileName = odTable.getFileName();
			adjustmentLookUpTable = getTableFromCSV(tableFileDir + System.getProperty("file.separator") + tableFileName,
					diagnosisLogging);
			if (adjustmentLookUpTable.length == 0) {
				return;
			}
			if (diagnosisLogging) {
				IJ.log("Log of read in csv file:");
				String out = "";
				for (int i = 0; i < adjustmentLookUpTable[0].length; i++) {
					out = "";
					for (int n = 0; n < adjustmentLookUpTable.length; n++) {
						out += adjustmentLookUpTable[n][i] + "	";
					}
					out = out.substring(0, out.length() - 1);
					IJ.log(out);
				}
			}
		}

		// add progressDialog
		progress = new ProgressDialog(new String[] { "" + new File(inPath).getName() });
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

		LinkedList<LinkedList<String>> filesByGroup = new LinkedList<LinkedList<String>>();
		{
			LinkedList<String> allFiles = validateFoldersAndCreateFileList(inPath);
			if (allFiles.size() == 0) {
				IJ.error("Cannot process input folder. Problem with input folder! No folders detected!\n"
						+ "Are you sure the folder structure you loaded under input path is as follows?\n"
						+ "<InputPath>|<well id, e.g., C5>|<imagefolder>|<tif files for this image>\n" + "AND\n"
						+ "Are you sure you set the filename ending parameters correctly for all channels?");
			}

			if (joinedAdjustment) {
				filesByGroup = getFolderListsByAdjustmentGroups(allFiles, adjustmentLookUpTable, progress, diagnosisLogging);
				if (filesByGroup.size() == 0) {
					return;
				}

				String fileList[] = new String[filesByGroup.size()];
				String seriesList[] = new String[filesByGroup.size()];
				String tempWellID;
				for (int aG = 0; aG < filesByGroup.size(); aG++) {
					fileList[aG] = filesByGroup.get(aG).get(0);
					
					tempWellID = fileList[aG];

					while (tempWellID.substring(tempWellID.length() - 1).equals(System.getProperty("file.separator"))) {
						tempWellID = tempWellID.substring(0, tempWellID.length() - 1);
					}

					// Extracting the well id in the filepath (only works if file path is:
					// <InputPath>|<well id, e.g., C5>|<imagefolder>|<zplanefolders>|<tif files for this image>
					tempWellID = tempWellID.substring(0, tempWellID.lastIndexOf(System.getProperty("file.separator")));
					tempWellID = tempWellID.substring(0, tempWellID.lastIndexOf(System.getProperty("file.separator")));
					tempWellID = tempWellID.substring(tempWellID.lastIndexOf(System.getProperty("file.separator")) + 1);

					seriesList[aG] = tempWellID;						
				}

				progress.updateTaskList(fileList, seriesList, "Adjustment Group", true);
				
				allFiles.clear();
				allFiles = null;
				System.gc();				
			}else {
				for(int i = 0; i < allFiles.size(); i++) {
					filesByGroup.add(new LinkedList<String>());
					filesByGroup.get(i).add(allFiles.get(i));
				}
			}
		}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// ---------------------------------RUN TASKS----------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

		String greenFile = "";
		ImagePlus imp, impGreen, impBlue, impRed, impYellow, impWhite = null;
		CompositeImage cImp;
		String newFilePath;
		File outDir;

		String[] subTasksPath, subTasksPathBlue, subTasksPathGreen, subTasksPathRed, subTasksPathYellow, subTasksPathWhite;
		
		running: while (continueProcessing) {
			for (int aG = 0; aG < filesByGroup.size(); aG++) {
				/**
				 * Create a list of the tasks ("subTasks") that we need to process and decide on
				 * together
				 */
							
				logAndUpdateProgrBar("listing " + filesByGroup.get(aG).size() + " subtasks... (adjustment group " + (aG + 1) + " / " + filesByGroup.size() + ")");
				
				subTasksPathGreen = new String[filesByGroup.get(aG).size()];
				subTasksPathBlue = new String[filesByGroup.get(aG).size()];
				subTasksPathRed = new String[filesByGroup.get(aG).size()];
				subTasksPathYellow = new String[filesByGroup.get(aG).size()];
				subTasksPathWhite = new String[filesByGroup.get(aG).size()];

				for(int p = 0; p < filesByGroup.get(aG).size(); p++){
					String[] fileList = new File(filesByGroup.get(aG).get(p)).list();					
					// Find file path
					for (int f = 0; f < fileList.length; f++) {
						if (fileList[f].endsWith(greenFileEnd)) {
							subTasksPathGreen [p] = filesByGroup.get(aG).get(p) + System.getProperty("file.separator") + fileList[f];
						} else if (fileList[f].endsWith(blueFileEnd)) {
							subTasksPathBlue [p] = filesByGroup.get(aG).get(p) + System.getProperty("file.separator") + fileList[f];
						} else if (fileList[f].endsWith(redFileEnd)) {
							subTasksPathRed [p] = filesByGroup.get(aG).get(p) + System.getProperty("file.separator") + fileList[f];
						} else if (fileList[f].endsWith(yellowFileEnd)) {
							subTasksPathYellow [p] = filesByGroup.get(aG).get(p) + System.getProperty("file.separator") + fileList[f];
						} else if (blue_green_red_yellow_whiteOut || whiteOut) {
							if (fileList[f].endsWith(whiteFileEnd)) {
								subTasksPathWhite [p] = filesByGroup.get(aG).get(p) + System.getProperty("file.separator") + fileList[f];
							}
						}
					}
				}
				
				/**
				 * Iterate over the subTasks to retrieve upper and lower percentile values
				 * Sort the percentile values
				 * Retrieve adjustment values
				 */
				
				logAndUpdateProgrBar("determining adjustment values ... (adjustment group " + (aG + 1) + " / " + filesByGroup.size() + ")");
									
				double adjValuesGreen [] = getAdjustmentValuesFromGroupedImages(subTasksPathGreen, percentage, mergePercentage);
				logAndUpdateProgrBar("Group " + (aG + 1) + " / " + filesByGroup.size() + ": Determined adjustment values Green: " + adjValuesGreen [0] + " | " + adjValuesGreen [1]);
				double adjValuesBlue [] = getAdjustmentValuesFromGroupedImages(subTasksPathBlue, percentage, mergePercentage);
				logAndUpdateProgrBar("Group " + (aG + 1) + " / " + filesByGroup.size() + ": Determined adjustment values Blue: " + adjValuesBlue [0] + " | " + adjValuesBlue [1]);
				double adjValuesRed [] = getAdjustmentValuesFromGroupedImages(subTasksPathRed, percentage, mergePercentage);
				logAndUpdateProgrBar("Group " + (aG + 1) + " / " + filesByGroup.size() + ": Determined adjustment values Red: " + adjValuesRed [0] + " | " + adjValuesRed [1]);
				double adjValuesYellow [] = getAdjustmentValuesFromGroupedImages(subTasksPathYellow, percentage, mergePercentage);
				logAndUpdateProgrBar("Group " + (aG + 1) + " / " + filesByGroup.size() + ": Determined adjustment values Yellow: " + adjValuesYellow [0] + " | " + adjValuesYellow [1]);
				double adjValuesWhite [] = new double [0];
				if (blue_green_red_yellow_whiteOut || whiteOut) {
					adjValuesWhite = getAdjustmentValuesFromGroupedImages(subTasksPathWhite, percentage, mergePercentage);
					logAndUpdateProgrBar("Group " + (aG + 1) + " / " + filesByGroup.size() + ": Determined adjustment values White: " + adjValuesWhite [0] + " | " + adjValuesWhite [1]);
				}
				
				// TODO limit upper value!
				
				/**
				 * Apply values and output
				 */
				for(int p = 0; p < filesByGroup.get(aG).size(); p++){
					try{
						// Open images
						impGreen = IJ.openImage(subTasksPathGreen[p]);
						impBlue = IJ.openImage(subTasksPathBlue[p]);
						impRed = IJ.openImage(subTasksPathRed[p]);
						impYellow = IJ.openImage(subTasksPathYellow[p]);
						if (blue_green_red_yellow_whiteOut || whiteOut) {
							impWhite = IJ.openImage(subTasksPathWhite[p]);
						}

						// Create image
						if (blue_green_red_yellow_whiteOut || whiteOut) {
							imp = IJ.createHyperStack("Merged File", impGreen.getWidth(), impGreen.getHeight(), 5, 1, 1,
									impGreen.getBitDepth());
						} else {
							imp = IJ.createHyperStack("Merged File", impGreen.getWidth(), impGreen.getHeight(), 4, 1, 1,
									impGreen.getBitDepth());
						}
						imp.setDisplayMode(IJ.COMPOSITE);

						// Transfer pixels
						for (int x = 0; x < imp.getWidth(); x++) {
							for (int y = 0; y < imp.getHeight(); y++) {
								imp.getStack().setVoxel(x, y, imp.getStackIndex(1, 1, 1) - 1,
										impGreen.getStack().getVoxel(x, y, 0));
								imp.getStack().setVoxel(x, y, imp.getStackIndex(2, 1, 1) - 1,
										impBlue.getStack().getVoxel(x, y, 0));
								imp.getStack().setVoxel(x, y, imp.getStackIndex(3, 1, 1) - 1,
										impRed.getStack().getVoxel(x, y, 0));
								imp.getStack().setVoxel(x, y, imp.getStackIndex(4, 1, 1) - 1,
										impYellow.getStack().getVoxel(x, y, 0));
								if (blue_green_red_yellow_whiteOut || whiteOut) {
									imp.getStack().setVoxel(x, y, imp.getStackIndex(5, 1, 1) - 1,
											impWhite.getStack().getVoxel(x, y, 0));
								}
							}
						}

						// Transfer metadata from Green image
						imp.setCalibration(impGreen.getCalibration());

						// Switch to composite image
						cImp = (CompositeImage) imp.duplicate();
						cImp.setDisplayMode(IJ.COMPOSITE);
						cImp.setActiveChannels("11111");

						// Setup channels
						cImp.setC(1);
						IJ.run(cImp, "Green", "");
						if (autoAdjustIntensities) {
							cImp.setDisplayRange(adjValuesGreen[0], adjValuesGreen[1]);							
						}else {
							cImp.setDisplayRange(impGreen.getDisplayRangeMin(), impGreen.getDisplayRangeMax());
						}

						cImp.setC(2);
						IJ.run(cImp, "Blue", "");
						if (autoAdjustIntensities) {
							cImp.setDisplayRange(adjValuesBlue[0], adjValuesBlue[1]);							
						}else {
							cImp.setDisplayRange(impBlue.getDisplayRangeMin(), impBlue.getDisplayRangeMax());
						}

						cImp.setC(3);
						IJ.run(cImp, "Red", "");
						if (autoAdjustIntensities) {
							cImp.setDisplayRange(adjValuesRed[0], adjValuesRed[1]);							
						}else {
							cImp.setDisplayRange(impRed.getDisplayRangeMin(), impRed.getDisplayRangeMax());
						}

						cImp.setC(4);
						IJ.run(cImp, "Yellow", "");
						if (autoAdjustIntensities) {
							cImp.setDisplayRange(adjValuesYellow[0], adjValuesYellow[1]);							
						}else {
							cImp.setDisplayRange(impYellow.getDisplayRangeMin(), impYellow.getDisplayRangeMax());
						}

						if (blue_green_red_yellow_whiteOut || whiteOut) {
							cImp.setC(5);
							IJ.run(cImp, "Grays", "");
							if (autoAdjustIntensities) {
								cImp.setDisplayRange(adjValuesWhite[0], adjValuesWhite[1]);								
							}else {
								cImp.setDisplayRange(impWhite.getDisplayRangeMin(), impWhite.getDisplayRangeMax());
							}
						}

						newFilePath = filesByGroup.get(aG).get(p);
						newFilePath = newFilePath.substring(newFilePath.indexOf(inPath) + inPath.length());
						newFilePath = outPath + System.getProperty("file.separator") + newFilePath;
						if (diagnosisLogging) {
							progress.notifyMessage(
									"Output path for <" + filesByGroup.get(aG).get(p) + ">:\n" + newFilePath + "",
									ProgressDialog.LOG);
						}
						outDir = new File(newFilePath);
						if (!outDir.exists()) {
							outDir.mkdirs();
						}
						
						greenFile = new File(subTasksPathGreen[p]).getName();
						String newFileName = greenFile.substring(0, greenFile.lastIndexOf(greenFileEnd));
						if (diagnosisLogging) {
							progress.notifyMessage("Filename post end removal: " + newFileName + "", ProgressDialog.LOG);
						}
						if (newFileName.length() == 0) {
							newFileName = new File(filesByGroup.get(aG).get(p)).getName();
							if (newFileName.length() < 5) {
								newFileName = new File(filesByGroup.get(aG).get(p)).getParentFile().getName() + "_" + newFileName;
							}

							if (diagnosisLogging) {
								progress.notifyMessage("Filename by director(ies): " + newFileName + "",
										ProgressDialog.LOG);
							}
						}

						if (outputPNGs) {
							outputImage(cImp, "PNG", ".png", newFilePath, newFileName);
						}
						if (outputJPGs) {
							outputImage(cImp, "JPG", ".jpg", newFilePath, newFileName);
						}

						imp.changes = false;
						imp.close();

						cImp.changes = false;
						cImp.close();

					} catch(Exception e) {
						String out = "" + e.getMessage();
						out += "\n" + e.getCause();
						for(int err = 0; err < e.getStackTrace().length; err++){
							out += " \n " + e.getStackTrace()[err].toString();
						}			
						
						progress.notifyMessage(
								"Could not process file " + (aG+1) + " (file path: " + filesByGroup.get(aG).get(p) + ")\nDetailed Error Message:\n" + out,
								ProgressDialog.NOTIFICATION);
					}
				}
								
				System.gc();
				
				progress.updateBarText("finished!");
				progress.setBar(1.0);
				progress.moveTask(0);
			}

			filesByGroup.clear();
			filesByGroup = null;
			processingDone = true;			
			break running;
		}
		System.gc();
	}

	/**
	 * This function explores the folder system in the @param directoryPath and
	 * finds all folders containing a set of .tif files suitable for processing.
	 * 
	 * @return a LinkedList with Strings representing filepaths to folders
	 *         containing all tif files neeeded for conversion
	 */
	private LinkedList<String> validateFoldersAndCreateFileList(String directoryPath) {
		LinkedList<String> allFiles = new LinkedList<String>();
		{
			LinkedList<String> FoldersToCheck = new LinkedList<String>();
			FoldersToCheck.add(directoryPath);

			boolean red, green, blue, yellow, white, skip;

			progress.updateBarText("Scanning file system for image folders ...");
			while (FoldersToCheck.size() > 0) {
				// Initialize folder variables
				File currFolder = new File(FoldersToCheck.getFirst());
				String[] fileList = currFolder.list();
				green = false;
				blue = false;
				red = false;
				yellow = false;
				white = false;
				skip = false;

				// Scan files and dirs in folder
				for (int f = 0; f < fileList.length; f++) {
					/**
					 * Now, the script scans through all file names in the folder and verifies if
					 * they are tif files and if so it checks whether they are named correctly.
					 */

					if (fileList[f] == greenFileEnd || fileList[f].endsWith(greenFileEnd)) {
						if (green) {
							if (diagnosisLogging) {
								progress.notifyMessage(
										"Ambigous files for green channel in\n" + currFolder.getPath() + "",
										ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						green = true;

					} else if (fileList[f] == blueFileEnd || fileList[f].endsWith(blueFileEnd)) {
						if (blue) {
							if (diagnosisLogging) {
								progress.notifyMessage(
										"Ambigous files for blue channel in\n" + currFolder.getPath() + "",
										ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						blue = true;
					} else if (fileList[f] == redFileEnd || fileList[f].endsWith(redFileEnd)) {
						if (red) {
							if (diagnosisLogging) {
								progress.notifyMessage(
										"Ambigous files for red channel in\n" + currFolder.getPath() + "",
										ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						red = true;
					} else if (fileList[f] == yellowFileEnd || fileList[f].endsWith(yellowFileEnd)) {
						if (yellow) {
							if (diagnosisLogging) {
								progress.notifyMessage(
										"Ambigous files for yellow channel in\n" + currFolder.getPath() + "",
										ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						yellow = true;
					} else if ((blue_green_red_yellow_whiteOut || whiteOut)
							&& (fileList[f] == whiteFileEnd || fileList[f].endsWith(whiteFileEnd))) {
						if (white) {
							if (diagnosisLogging) {
								progress.notifyMessage(
										"Ambigous files for white channel in\n" + currFolder.getPath() + "",
										ProgressDialog.NOTIFICATION);
								skip = true;
							}
						}
						white = true;
					} else if (new File(
							currFolder.getAbsolutePath() + System.getProperty("file.separator") + fileList[f])
							.isDirectory()) {
						FoldersToCheck
								.add(currFolder.getAbsolutePath() + System.getProperty("file.separator") + fileList[f]);
						if (diagnosisLogging) {
							progress.notifyMessage("Folder added to to-screen list: " + currFolder.getPath()
									+ System.getProperty("file.separator") + fileList[f], ProgressDialog.LOG);
						}
					} else {
						if (diagnosisLogging) {
							progress.notifyMessage("Could not assign file " + fileList[f] + "", ProgressDialog.LOG);
						}
					}
				}

				// Add if contains all files needed
				if ((green && blue && red && yellow) && skip == false) {
					if ((blue_green_red_yellow_whiteOut || whiteOut) && !white) {
						progress.notifyMessage("Skipped a folder since missing brightfield image file ending with "
								+ whiteFileEnd + " (Folder: " + currFolder.getPath() + ").",
								ProgressDialog.NOTIFICATION);
					} else {
						allFiles.add(currFolder.getAbsolutePath());
						if (diagnosisLogging) {
							progress.notifyMessage("Added folder to process list: " + currFolder.getPath() + "",
									ProgressDialog.LOG);
						}
					}
				} else if (diagnosisLogging) {
					progress.notifyMessage("Did not add folder to process list since missing file(s) (code:" + "R-"
							+ red + "_G-" + green + "_B-" + blue + "_Y-" + yellow + "_SKIP-" + skip + "):"
							+ currFolder.getPath() + "", ProgressDialog.LOG);
				}

				// Remove last element to move on to next folder in list
				FoldersToCheck.removeFirst();
				if (diagnosisLogging) {
					progress.notifyMessage(
							"Removed first element to move on - remaining nr of elements " + FoldersToCheck.size(),
							ProgressDialog.LOG);
				}
				progress.updateBarText("Scanning file system for image folders ... found " + FoldersToCheck.size()
						+ " image folders so far.");
			}
			FoldersToCheck.clear();
			FoldersToCheck = null;
			System.gc();
		}
		return allFiles;
	}

	/**
	 * This method checks the file list for where the file belongs If a file is in
	 * the list already belonging to the same adjustment group, it will skip it.
	 * 
	 * @param allFiles:              the list of filepaths
	 * @param adjustmentLookUpTable: the look up table loaded to determine
	 *                               adjustment group.
	 * @param diagnosisLogging:      If true, progress information is logged.
	 * @return LinkedList<LinkedList<String>> that contains linked lists of strings containing filepaths to images to be adjusted together
	 */
	private static LinkedList<LinkedList<String>> getFolderListsByAdjustmentGroups(LinkedList<String> allFiles,
			String[][] adjustmentLookUpTable, ProgressDialog progress, boolean diagnosisLogging) {
		String tempWellID, adjustmentGroup;

		ArrayList<String> adjustmentGroups = new ArrayList<String>(allFiles.size());

		LinkedList<LinkedList<String>> allFoldersByGroup = new LinkedList<LinkedList<String>> (); 
		
		for (int f = 0; f < allFiles.size(); f++) {
			tempWellID = allFiles.get(f);

			while (tempWellID.substring(tempWellID.length() - 1).equals(System.getProperty("file.separator"))) {
				tempWellID = tempWellID.substring(0, tempWellID.length() - 1);
//				if(diagnosisLogging) {
//					progress.notifyMessage("LOG: Shortening file name to " + tempWellID + " (last char: " + tempWellID.substring(tempWellID.length()-1) 
//					+ ") for file " + allFiles.get(f) + "",ProgressDialog.LOG);
//				}
				if (tempWellID.length() == 0) {
					new WaitForUserDialog("Failed to process the list of input folders.\n"
							+ "Are you sure the folder structure you loaded under input path is as follows?\n"
							+ "<InputPath>|<well id, e.g., C5>|<imagefolder>|<tif files for this image>").show();
					allFiles.clear();
					return allFoldersByGroup;
				}
			}

			// Extracting the well id in the filepath (only works if file path is:
			// <InputPath>|<well id, e.g., C5>|<imagefolder>|<zplanefolders>|<channel tif files for this image>
			tempWellID = tempWellID.substring(0, tempWellID.lastIndexOf(System.getProperty("file.separator")));
			tempWellID = tempWellID.substring(0, tempWellID.lastIndexOf(System.getProperty("file.separator")));
			tempWellID = tempWellID.substring(tempWellID.lastIndexOf(System.getProperty("file.separator")) + 1);

//			if(diagnosisLogging) {
//				progress.notifyMessage("LOG: Checking for well " + tempWellID + " in look-up-table (" + allFiles.get(f) + ")",ProgressDialog.LOG);
//			}

			adjustmentGroup = "";
			for (int lut = 0; lut < adjustmentLookUpTable[0].length; lut++) {
				if (adjustmentLookUpTable[0][lut].equals(tempWellID)) {
					adjustmentGroup = adjustmentLookUpTable[1][lut];
					if (diagnosisLogging) {
						progress.notifyMessage(
								"LOG: Found well " + tempWellID + " in look-up-table and retrieved adjustment group "
										+ adjustmentGroup + " for it (" + allFiles.get(f) + ")",
								ProgressDialog.LOG);
					}
					break;
				}
			}

//			if (adjustmentGroup.equals("")) {
				// TODO eventually implement to try in this case to determine tempWell ID again by going up only one folder and not two as before
				
//			}
			
			if (adjustmentGroup.equals("")) {
				progress.notifyMessage(
						"WARNING: Need to skip file since could not identify well / adjustment group for file with well ID "
								+ tempWellID + " (file path: " + allFiles.get(f) + ")!",
						ProgressDialog.NOTIFICATION);
			}

			if (adjustmentGroups.size() == 0) {
				// Create first element
				adjustmentGroups.add(adjustmentGroup);
				allFoldersByGroup.add(new LinkedList<String>());
				allFoldersByGroup.get(0).add(allFiles.get(f));
				
				if (diagnosisLogging) {
					progress.notifyMessage("LOG: Creating adjustment group "
							+ adjustmentGroup + 
							" and adding file number" + (f + 1) + 
							" (" + allFiles.get(f) + ")", ProgressDialog.LOG);
				}
			} else {
				// Search whether adjustment group has been already added to list - if yes,
				// add the file to adjustment group list, if no, create a new group
				for (int aG = 0; aG < adjustmentGroups.size(); aG++) {
					if (adjustmentGroup.equals(adjustmentGroups.get(aG))) {
						if (diagnosisLogging) {
							progress.notifyMessage(
									"LOG: Will add file number " + (f + 1) + " to adjustment group "
											+ adjustmentGroup + " in list (" + allFiles.get(f) + ")",
									ProgressDialog.LOG);
						}
						
						//Add file to corresponding list
						allFoldersByGroup.get(aG).add(allFiles.get(f));
												
						break;
					} else if ((aG + 1) == adjustmentGroups.size()) {
						//Create new group and add file
						adjustmentGroups.add(adjustmentGroup);
						allFoldersByGroup.add(new LinkedList<String>());
						allFoldersByGroup.get(aG + 1).add(allFiles.get(f));
												
						if (diagnosisLogging) {
							progress.notifyMessage("Creating adjustment group "
									+ adjustmentGroup + 
									" and adding file number" + (f + 1) + 
									" (" + allFiles.get(f) + ")",
									ProgressDialog.LOG);
						}
						break;
					}
				}
			}

		}

		if (diagnosisLogging) {
			IJ.log("###################################");
			IJ.log("#       LISTS AFTER REMOVAL       #");
			IJ.log("###################################");
			IJ.log("AdjustmentGroup	FilePath");

			for (int aG = 0; aG < adjustmentGroups.size(); aG++) {
				for (int e = 0; e < allFoldersByGroup.get(aG).size(); e++) {
					IJ.log(adjustmentGroups.get(aG) + "	" + allFoldersByGroup.get(aG).get(e));					
				}
			}

			IJ.log("###################################");
		}
		
		return allFoldersByGroup;
	}

	/**
	 * @param: 0 < channel <= nr of channels
	 */
	private void autoAdjustDisplayRange(CompositeImage imp, int channel, double minimumMaxValue) {
		imp.setC(channel);
		double max = getMinMaxPercentInImage(imp, channel, 0.001)[1];
		if (max < minimumMaxValue) {
			max = minimumMaxValue;
		}
		imp.setDisplayRange(0.0, max);

		if (diagnosisLogging) {
			progress.notifyMessage("Adjusted channel " + channel + " to [" + 0.0 + "," + max
					+ "] by determining percentile 0.0001 - in image processor: [" + imp.getProcessor().getMin() + ","
					+ imp.getProcessor().getMax() + "]", ProgressDialog.LOG);
		}
	}
		
	/**
	 * @param: 0 < channel <= nr of channels
	 */
	private void autoAdjustBrightfieldRange(CompositeImage imp, int channel) {
		imp.setC(channel);
		double[] minMax = getMinMaxPercentInImage(imp, channel, 0.001);

		imp.setDisplayRange(minMax[0], minMax[1]);

		if (diagnosisLogging) {
			progress.notifyMessage("Adjusted channel " + channel + " to [" + minMax[0] + "," + minMax[1]
					+ "] by determining percentile 0.0001 - in image processor: [" + imp.getProcessor().getMin() + ","
					+ imp.getProcessor().getMax() + "]", ProgressDialog.LOG);
		}
	}

	/**
	 * @param: 0 < channel <= nr of channels
	 */
	private double[] getMinMaxPercentInImage(CompositeImage imp, int channel, double percent) {
		ArrayList<Double> pixels = new ArrayList<Double>(imp.getWidth() * imp.getHeight());
		for (int x = 0; x < imp.getWidth(); x++) {
			for (int y = 0; y < imp.getHeight(); y++) {
				pixels.add(imp.getStack().getVoxel(x, y, imp.getStackIndex(channel, 1, 1) - 1));
			}
		}
		Collections.sort(pixels);

		int indexMin = (int) Math.round((double) pixels.size() * percent / 100.0);
		int indexMax = (int) Math.round((double) pixels.size() * (100.0 - percent) / 100.0);
		double out[] = new double[] { pixels.get(indexMin - 1), pixels.get(indexMax - 1) };
		pixels.clear();
		pixels = null;
		return out;
	}
	
	/**
	 * @param: 0 < channel <= nr of channels
	 */
	private double[] getMinMaxPercentInImage(ImagePlus imp, int channel, double percent) {
		ArrayList<Double> pixels = new ArrayList<Double>(imp.getWidth() * imp.getHeight());
		for (int x = 0; x < imp.getWidth(); x++) {
			for (int y = 0; y < imp.getHeight(); y++) {
				pixels.add(imp.getStack().getVoxel(x, y, imp.getStackIndex(channel, 1, 1) - 1));
			}
		}
		Collections.sort(pixels);

		int indexMin = (int) Math.round((double) pixels.size() * percent / 100.0);
		int indexMax = (int) Math.round((double) pixels.size() * (100.0 - percent) / 100.0);
		double out[] = new double[] { pixels.get(indexMin - 1), pixels.get(indexMax - 1) };
		pixels.clear();
		pixels = null;
		return out;
	}
	
	/**
	 * getPercentageRangesForGroupedImages
	 * @param filePaths: contains paths as strings to all images for which the upper and lower intensity percentile shall be determined
	 * @param percentage: determines the percentile to be determined in %
	 * @return a double array with first dimension of size 2 (index 0 = lower percentile, 1 = upper percentile), and the second dimensions of size of the lengths of input path.
	 */
	private double [][] getPercentageRangesForGroupedImages(String[] filePaths, double percentage){		
		double adjValues [][] = new double [2][filePaths.length];
		double values [];
		ImagePlus imp;
		for(int p = 0; p < filePaths.length; p++){
			// Open images
			imp = IJ.openImage(filePaths [p]);
			values = getMinMaxPercentInImage(imp, 1, percentage);
			adjValues [0][p] = values [0];
			adjValues [1][p] = values [1];
			imp.changes = false;
			imp.close();
		}
		return adjValues;
	}
	
	/**
	 * getSortedPercentageRangesForGroupedImages will first determine the upper and lower percentiles for all images, and then sort the thereby created lists increasing.
	 * @param filePaths: contains paths as strings to all images for which the upper and lower intensity percentile shall be determined
	 * @param percentage: determines the percentile to be determined in %
	 * @return a double array with first dimension of size 2 (index 0 = lower percentile, 1 = upper percentile), and the second dimensions of size of the lengths of input path.
	 */
	private double [][] getSortedPercentageRangesForGroupedImages(String[] filePaths, double percentage){
		double adjValues [][] = getPercentageRangesForGroupedImages(filePaths, percentage);
		Arrays.sort(adjValues[0]);
		Arrays.sort(adjValues[1]);
		return adjValues;
	}
	
	/**
	 * This function will first determine the upper and lower percentiles for all images.
	 * Then it will then sort the thereby created lists (increasing pattern).
	 * Then it will determine the lower and upper percentiles from the lists of values and return them:
	 * @param filePaths: contains paths as strings to all images for which the upper and lower intensity percentile shall be determined
	 * @param percentage: determines the percentiles in % to be determined from the image intensities 
	 * @param mergingPercentage: determines the percentiles in % to be used to merge all percentile values from all images
	 * @return a double array with first dimension of size 2 (index 0 = lower percentile, 1 = upper percentile).
	 */
	private double [] getAdjustmentValuesFromGroupedImages(String[] filePaths, double percentage, double mergingPercentage) {
		double adjValues [][] = getSortedPercentageRangesForGroupedImages(filePaths, percentage);
		
		return new double [] {adjValues[0][(int)Math.round((double) adjValues[0].length*mergingPercentage/100.0)-1],
				adjValues[1][(int)Math.round((double) adjValues[1].length*(100.0-mergingPercentage)/100.0)-1]};
		
	}

	private void outputImage(CompositeImage imp, String fileType, String fileEnding, String dir, String namePrefix) {
		if (greenOut) {
			imp.setActiveChannels("10000");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_green" + fileEnding);
		}

		if (blueOut) {
			imp.setActiveChannels("01000");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_blue" + fileEnding);
		}

		if (redOut) {
			imp.setActiveChannels("00100");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_red" + fileEnding);
		}

		if (yellowOut) {
			imp.setActiveChannels("00010");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_yellow" + fileEnding);
		}

		if (blue_greenOut) {
			imp.setActiveChannels("11000");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_green" + fileEnding);
		}

		if (blue_redOut) {
			imp.setActiveChannels("01100");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_red" + fileEnding);
		}

		if (blue_yellowOut) {
			imp.setActiveChannels("01010");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_yellow" + fileEnding);
		}

		if (green_redOut) {
			imp.setActiveChannels("10100");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_red_green" + fileEnding);
		}

		if (green_yellowOut) {
			imp.setActiveChannels("10010");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_green_yellow" + fileEnding);
		}

		if (red_yellowOut) {
			imp.setActiveChannels("00110");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_red_yellow" + fileEnding);
		}

		if (blue_green_redOut) {
			imp.setActiveChannels("11100");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_red_green" + fileEnding);
		}

		if (blue_red_yellowOut) {
			imp.setActiveChannels("01110");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_red_yellow" + fileEnding);
		}

		if (blue_green_yellowOut) {
			imp.setActiveChannels("11010");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_green_yellow" + fileEnding);
		}

		if (blue_green_red_yellowOut) {
			imp.setActiveChannels("11110");
			IJ.saveAs(imp, fileType,
					dir + System.getProperty("file.separator") + namePrefix + "_blue_red_green_yellow" + fileEnding);
		}

		if (whiteOut) {
			imp.setActiveChannels("00001");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix + "_white" + fileEnding);
		}

		if (blue_green_red_yellow_whiteOut) {
			imp.setActiveChannels("11111");
			IJ.saveAs(imp, fileType, dir + System.getProperty("file.separator") + namePrefix
					+ "_blue_red_green_yellow_white" + fileEnding);
		}

	}

	private static String[][] getTableFromCSV(String filePath, boolean diagnosisLogging) {
		try {
			FileReader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			LinkedList<String> lines = new LinkedList<String>();
			reading: while (true) {
				try {
					line = br.readLine();
					if (line.equals(null)) {
						break reading;
					}
					lines.add(line);
				} catch (Exception e) {
					break reading;
				}
			}
			br.close();
			fr.close();

			if (lines.size() == 0) {
				IJ.error(
						"Processing failed - loaded table file contained 0 lines!\nMake sure to load a comma-delimited, intact csv file!");
				return new String[0][0];
			}

			int nrOfCols = getNumberOfPatternsInString(lines.get(0), ",") + 1;
			if (diagnosisLogging) {
				IJ.log("Found " + nrOfCols + " in adjustment file based on line content '" + lines.get(0) + "'");
			}
			String[][] out = new String[nrOfCols][lines.size()];

			for (int i = 0; i < out[0].length; i++) {
				line = lines.get(i);
				for (int n = nrOfCols - 1; n > 0; n--) {
					out[n][i] = line.substring(line.lastIndexOf(",") + 1);
					line = line.substring(0, line.lastIndexOf(","));
				}
				out[0][i] = line;

				if (line.contains(",")) {
					IJ.error(
							"Processing failed - loaded table file contained variying numbers of columns in each row.\nMake sure to load a comma-delimited, intact csv file!");
					return new String[0][0];
				}
			}
			lines.clear();
			lines = null;

			return out;
		} catch (IOException e) {
			IJ.error("Loading table file failed!");
			e.printStackTrace();
			return new String[0][0];
		}
	}
	
	private static int getNumberOfPatternsInString(String text, String pattern) {
		int n = 0;
		while (text.contains(pattern)) {
			if (text.lastIndexOf(pattern) == -1) {
				break;
			}
			n += 1;
			text = text.substring(0, text.lastIndexOf(pattern));
		}
		return n;
	}
	
	private void logAndUpdateProgrBar(String msg) {
		progress.updateBarText(msg);
		if(diagnosisLogging) {
			progress.notifyMessage(msg, ProgressDialog.LOG); 
		}
	}
	
}// end main class