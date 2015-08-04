#!/bin/bash

#nohup ~/scaws-from-s3.sh > ~/scaws-from-s3.log &

set -x #echo on

#run in cygwin: /tracking-app/src/main/my_note/scaws-from-s3.cygwin
#run hbase schema : /tracking-lib/src/main/script/schema/hbase_schema.txt
#need to manually run:
sudo yum -y install dos2unix
chmod u+wrx ~/*sh
chmod u+wr ~/*
dos2unix ~/*sh

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"


mkdir -p /mnt/upload/unzip

hdfs dfs -mkdir /data
hdfs dfs -mkdir /data/input
hdfs dfs -mkdir /data/input/mr
hdfs dfs -mkdir /data/input/drivetest

 hdfs dfs -put ~/0102885120140928160610ms9.csv /data/input/drivetest

#import drive test data
 
hdfs dfs -rm -r /data/output/drivetest
 hadoop jar ~/tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsDriveTestDataBulkLoader /data/input/drivetest /data/output/drivetest -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" 
 hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/drivetest drive_test_data

#build road cells
 
java -cp ~/tracking-app-1.0-SNAPSHOT.jar:/home/hadoop/lib/hbase.jar -Djava.ext.dirs=/home/hadoop/lib/lib -Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml com.cmti.analytics.app.tracking.task.scan.ScanDriveTestData

#run MR import 

~/scaws-from-s3-import-mr.sh gsm0 gsm1 gsm2 gsm3 gsm4 gsm5 gsm6 gsm7 gsm8 gsm9 tong0 tong1 tong2 tong3 tong4 tong5 tong6 tong7 tong8 tong9 yi0 > ~/scaws-from-s3-import-mr-gsm-tong.log &

# ~/scaws-from-s3-import-mr.sh tong0 tong1 tong2 tong3 tong4 tong5 tong6 tong7 tong8 tong9 yi0 > ~/scaws-from-s3-import-mr-tong.log &

~/scaws-from-s3-import-mr.sh 2063 2066 2124 2129 2141 3762 3777 3780  2061 2064 2067 2125 2135 2142 3763 3778 3781  2062 2065 2068 2127 2139 2143 3776 3779 3782 > ~/scaws-from-s3-import-mr-yi.log 


# hadoop jar ~/tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > ~/MrMR.log 

 