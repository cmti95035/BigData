#!/bin/bash

# nohup ~/scaws-from-s3-import-mr.sh > ~/scaws-from-s3-import-mr.log & 

set -x #echo on

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"

# eth="gsm0 gsm1 gsm2 gsm3 gsm4 gsm5 gsm6 gsm7 gsm8 gsm9 tong0 tong1 tong2 tong3 tong4 tong5 tong6 tong7 tong8 tong9 yi0 2063 2066 2124 2129 2141 3762 3777 3780  2061 2064 2067 2125 2135 2142 3763 3778 3781  2062 2065 2068 2127 2139 2143 3776 3779 3782" 


cd /mnt/upload

for key in "$@";do
 echo $key
 aws s3 cp s3://cmri-gmo/sc-high/$key.zip $key.zip 
 unzip $key.zip -d unzip

 
 hdfs dfs -copyFromLocal unzip/$key /data/input/mr  

hdfs dfs -rm -r /data/output/mr/$key 
 hadoop jar ~/tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrBulkLoader /data/input/mr/$key  /data/output/mr/$key    -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > ~/HdfsMrBulkLoader-$key.log 

 hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/mr/$key mr
 
 rm $key.zip 
 rm -Rf unzip/$key
 hdfs dfs -rm -r /data/input/mr/$key 
 
done
