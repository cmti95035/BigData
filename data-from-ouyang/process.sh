#!/bin/bash -e 

# To run, this assumes that data are in the work directory, for example, mo, and the output file is output/feature.csv.
# To run, it works to use
#
#   ./process.sh mo output/feature.csv
#

python collect.py $1 $2

Rscript ClusterEnsemble.R

Rscript L2.R
