# USAGE
# python transform.py mo newoutput
from PIL import Image
import random
import shutil
import uuid
import os
import glob2
import argparse
import sys

in_dir = sys.argv[1]
out_dir = sys.argv[2]
i = 0
if not os.path.exists(out_dir):
    os.mkdir(out_dir)
for filePath in glob2.iglob(in_dir + '/*.csv'):
	i += 1
	word = filePath.split('/')
	name = word[1].split('.')[0]
	outfile = out_dir + '/transformed_' + name + '.csv'
	print i, filePath + ' -> ' + outfile
	output = open(outfile, 'w')
	output.write('Time, HSDPA.RAB.Setup.Succ.Ratio.CELL,\
	HSDPA.Call.Drop.Ratio.cell,\
	HSDPA.UE.Mean.Cell,\
	HSDPA.UE.Max.Cell,\
	HSDPA.MeanChThroughput,\
	HSDPA.RAB.AttEstab,\
	HSDPA.RAB.SuccEstab,\
	HSDPA.RAB.AbnormRel,\
	HSDPA.RAB.NormRel')
	f = open(filePath)
	content = f.readlines()
	gather = {}
	for j in xrange(1, len(content)):
		res = []
		sentence = content[j].split(',')
		for k in [0, 5, 10, 101, 102, 150, 221, 222, 223, 224]:
			if sentence[k] != '' and sentence[k] != 'NaN':
				res.append(sentence[k])
		if len(res) < 10:
			continue
		ts = res[0].split()[1]
		gather[ts] = gather.get(ts, []) + [res[1:]]
	for each in gather.keys():
		result = []
		result.append(each)
		for m in range(9):
			tot = 0
			for item in gather[each]:
				tot += float(item[m])
			tot /= len(gather[each])
			result.append(str(tot))
		s = result[0]
		for n in range(1, 10): s += ', ' + result[n]
		output.write('\n'  + s)