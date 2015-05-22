# USAGE
# python gather.py

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
inp = open(workdir + "/locations.csv")
i = 0
result = {}
sent = inp.readlines()
for j in xrange(1, len(sent)):
    curr = sent[j].split(',')
    timest = (curr[0], curr[1])
    print timest
    locat = (curr[4], curr[5])
    print locat
    result[timest] = locat
output = open(outfile, "w")
st = ""
for n in xrange(100):
    st += ", Tx" + str(n)
output.write("N, W, Date, Time" + st)

# loop over the csv files
for filePath in glob2.iglob(workdir + "/*.csv"):
    #i += 1
    if filePath == workdir + "/locations.csv" or filePath == workdir + "/verizon.csv": continue
    words = filePath.split('-')
    date = words[0].split('/')[-1] + '/' + words[1] + '/' + words[2].split()[0]
    time = words[2].split()[-1] + ':' + words[3] + ' ' + words[-1].split()[-1][:2]
    ts = (date, time)
    print ts
    
    f = open(filePath)
    content = f.readlines()
    totLoss = 0
    totTx = 0
    totSRS = 0
    nLoss = 0
    nTx = 0
    nSRS = 0
    res = []
    for k in xrange(1, len(content)):
        sentence = content[k].split(',')
        #print sentence, sentence[5]
        #if sentence[5] != '""':
        #    totLoss += float(sentence[5].strip('"'))
        #    nLoss += 1
        if sentence[6] != '""':
            totTx += float(sentence[6].strip('"'))
            if nTx % 100 == 0: res.append(sentence[6].strip('"'))
            nTx += 1
        if nTx == 10000: break
        #if sentence[7] != '""':
        #    totSRS += float(sentence[7].strip('"'))
        #    nSRS += 1
    #print totLoss / nLoss, totTx / nTx, totSRS / nSRS
    if ts in result:
        s = ', '.join(res)
        output.write('\n' + result[ts][0] + ', ' + result[ts][1] + ', ' + ts[0] + ', ' + ts[1] + ', ' + s) 
        #str(totTx / nTx) + ', ' + str(totSRS / nSRS))
# close the output file
output.close()