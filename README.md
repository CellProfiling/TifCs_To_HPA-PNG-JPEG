#  TifCs_To_HPA-PNG-JPEG ImageJ plugin
FIJI plugin that reads folders with single-channel tif files and outputs an identical file system with PNGs and JPEGs in HPA-Style. 

So this plugin ...
- ... reads a directory containing directories with single-channel .tif files (one directory per multi-channel image)
- ... outputs jpeg (or png) files of single channels or merged channels as downloadable from the HPA, such as:
   - https://images.proteinatlas.org/115/672_E2_1_blue_red_green.jpg
   - https://images.proteinatlas.org/115/672_E2_1_green.jpg
- ... allows to use an un-build intensity-scaling function to provide best conversion from 16-bit depth to 8-bit range independently for each channel. This function right now works as follows:
   - The 99.999 % percentile of all pixel intensities is determined
   - Intensities are rescaled so that the 99.999% percentile represents the highest displayed intensity value (An intensity of 0 is set to be the lowest intensity value)
   - If the 99.999 % percentile is smaller than 20,000.0: Intensities are rescaled so that the highest displayed intensity value is 20,000.0 (instead of using the 99.999 % percentile)


## Copyright
(c) 2023, Jan N. Hansen

Contact: jan.hansen (at) scilifelab.se

## Licenses
The plugin and the source code are published under the GNU General Public License v3.0 contained in this repository.

## How to use this plugin?
This software is a plugin for ImageJ or FIJI (an extended version of the open-source image-analysis software ImageJ, including the BioFormats library from OME). Thus to use the plugin, you need to first have ImageJ or FIJI downloaded to your computer.
Imagej/FIJI is freely available for download [here](https://imagej.net/downloads). 

### Installation
The plugin can be installed as follows: 
- Download the .jar file from the latest release at the [release section](https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/releases).
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/dfaa6c69-852c-444e-b579-c3d05683605a" width=500>
</p>

- Launch FIJI and install the plugin by drag and drop into the FIJI status bar (red marked region in the screenshot below) 
<p align="center">
   <img src="https://user-images.githubusercontent.com/27991883/201358020-c3685947-b5d8-4127-88ec-ce9b4ddf0e56.png" width=500>
</p>

- Confirm the installations by pressing save in the upcoming dialog(s).
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/69326270-5ee2-474c-80d8-550387ede036" width=500>
</p>

- Next, ImageJ requires to be restarted (close it and start it again)

- You can now verify that the plugin is installed by launching it through the menu entry: Plugins > CellProfiling > 

<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/b7632c82-05a6-4cd2-a533-5a1b680f8a75" width=500>
</p>

### Using the plugin
Launch the plugin via ```Plugins>ELL-Plugins>TifChannels to HPA-PNG-JPEG (v0.0.3)```. A dialog will pop up allowing you to select options for processing.

<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/dded2f45-070b-4aa0-940a-8f1f6690a337" width=500>
</p>

Now set the settings as explained here:

1. Insert the path to the input folder system with subfolders containing single-channel .tiff images. File systems may, e.g., come from (the plugin creating .ome.tif files out of OPERA files)[https://github.com/CellProfiling/HPA_Convert_OPERA_To_LIMS-OMETIF] or from (the plugin creating memento-ready .ome.tif files out of .lif files)[https://github.com/CellProfiling/Sp8-Lif_To_Memento].
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/7a977e2f-cd1c-48f9-9187-b55f706a9c85">
</p>

2. Insert the path to the output folder, where the same subfolder system shall be created but with .jpeg or .png files instead of .tiff files.
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/7e1d9cae-64ed-44f5-8d91-b95a4d099ce0">
</p>

3. Decide what output format you would prefer (.jpeg images load faster than .png images in memento).
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/e3810d6d-ee57-47b5-ad49-5061f97c7000">
</p>

4. Decide whether you simply want to create all kinds of image display (any channel alone plus any channel with other channels combined). If you do not want that, select the following option:
![image](https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/2d2fedfd-d607-4054-b77f-1fa964c70c99). In turn, a dialog will appear after this dialog and allow you to customize overlays that you want to have created.
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/6b148d36-e360-46c9-a78e-f9d3bb0a02ab">
</p>


5. Decide whether you want to auto-adjust channels (this should always be used with 16-bit input images since otherwise these may appear black or dark after 8-bit conversion). Note that at this stage the plugin will auto-adjust each field of view individually. So if you recorded multiple images from the same well, it will still adjust each image individually based on the intensities in the image.
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/5eea0c03-29f4-4921-97da-349e7eabd5ce">
</p>

6. Denote what the end of the filename is for each channel color.
<p align="center">
   <img src="https://github.com/CellProfiling/TifCs_To_HPA-PNG-JPEG/assets/27991883/92e13947-505a-413a-b359-cf39f2de3b0f">
</p>

7. Press OK!

Now the plugin processes and it tells you when it has processed and created all files.

### Updating the plugin version
Download the new version's .jar file from the [release page](https://github.com/CellProfiling/HPA_Convert_OPERA_To_LIMS-OMETIF/releases). Make sure FIJI is closed - if still open, close it. Next, locate the FIJI software file / folder on your computer and go on below depending on your OS.

#### Windows
In Windows or Linux, FIJI is a directory called FIJI.app. Enter this directory and navigate to the "plugins" folder and enter it. Find the old version of the TifChannels_to_HPA_PNG_JPEG_Main_JNH-X.X.X-SNAPSHOT.jar file and delete it. Then place the new plugin version in the "plugins" folder. Exit the FIJI.app folder. Start FIJI.

#### Mac
In Mac OS, FIJI is just a software file (FIJI.app). Right click on the FIJI icon (or hold option and do normal click on it), then select "Show Package Content". A folder will open, which contains the contents of the FIJI.app. Navigate to the "plugins" folder folder and enter it. Find the old version of the TifChannels_to_HPA_PNG_JPEG_Main_JNH-X.X.X-SNAPSHOT.jar file and delete it. Then place the new plugin version in the "plugins" folder . Exit the FIJI.app folder. Start FIJI.

---

(c) 2023 J.N. Hansen, Cell Profiling group
