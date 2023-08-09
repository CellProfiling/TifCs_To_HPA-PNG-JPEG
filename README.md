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
To be written...

### Using the plugin
Launch the plugin via
To be written...

