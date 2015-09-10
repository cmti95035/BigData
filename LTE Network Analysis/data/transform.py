# USAGE
# python transform.py mo newoutput

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
	for j in xrange(1, len(content)):
		res = []
		sentence = content[j].split(',')
		for k in [0, 5, 10, 101, 102, 150, 221, 222, 223, 224]:
			if sentence[k] != '':
				res.append(sentence[k])
		if len(res) < 10:
			continue
		s = ', '.join(res)
		output.write('\n'  + s)
	output.close()