rm(list=ls())
filenames <- list.files("data/feature24mean", pattern = "*.csv", full.names = TRUE)
library(plyr)
library(dostats)
# outFile <- "AllBaseStation.csv"
# outFileColNames <- c("cellnumber", "HSDPA.UE.Mean.Cell", "HSDPA.MeanChThroughput")
res <- NULL
for (i in seq_along(filenames)) {
  f <- read.csv(filenames[i])
  res <- rbind(res, cbind(rep(i, dim(f)[1]), f[, 4], f[, 6]))
  }
#write.table(res,file=outFile,sep=',',row.names=FALSE,col.names=outFileColNames,quote=FALSE)