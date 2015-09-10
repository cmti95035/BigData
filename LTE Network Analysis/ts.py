import glob2
import sys
output = open('feature.csv', 'w')
i = 0
st = 'Time, '
base = []
result = []
for filePath in glob2.iglob('mo/*.csv'):
    i += 1
    print i, filePath
    f = open(filePath)
    content = f.readlines()
    if len(content) < 1000: continue
    base.append(filePath.split('.')[0][3:])
    current = []
    rtime = []
    for k in xrange(1, 1000):
    	current.append(content[k].split(',')[5] if content[k].split(',')[5] != '' else 0)
    	rtime.append(content[k].split(',')[0])
    result.append(current)
print len(rtime)
output.write(st + ', '.join(base))
for j in xrange(1, 1000):
    words = rtime[j - 1]
    for m in xrange(len(result)):
    	words += ', ' + str(result[m][j - 1])
    output.write('\n' + words)
output.close()