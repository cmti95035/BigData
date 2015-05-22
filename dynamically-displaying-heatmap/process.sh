#!/bin/bash -e 

# To run, this assumes that data are in the work directory, for example, /data/crawdad/crawdad, and the output file is output.csv.
# To run, it works to use
#
#   ./process.sh /data/crawdad/crawdad output.csv
#

python gather.py $1 $2

Rscript dynam.R $2

display animation.gif
