# USAGE
# python collect.py mo ouput/feature.csv

# import the necessary packages
from PIL import Image
import argparse
import random
import shutil
import glob2
import uuid
import sys
workdir = sys.argv[1]
outfile = sys.argv[2]
i = 0
result = {}
output = open(outfile, "w")
output.write('V1, V2, V3, V4, V5, V6')
# loop over the csv files
for filePath in glob2.iglob(workdir + "/*.csv"):
    i += 1
    words = filePath.split('/')
    name = words[1].split('.')[0]
    print i, name
    f = open(filePath)
    content = f.readlines()
    if len(content) <= 23: continue
    res = []
    sentence = content[23].split(',')
    print sentence[0]
    for j in xrange(5, 12):
        # print sentence[j],
        if j != 6 and sentence[j] != '': res.append(sentence[j])
    # print
    if len(res) < 6: continue
    s = ', '.join(res)
    output.write('\n' + s)
# close the output file
output.close()