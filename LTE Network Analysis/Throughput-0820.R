filenames <- list.files("data/mo", pattern = "*.csv", full.names = TRUE)
library(plyr)
library(dostats)
# outFile <- "data/all.csv"
outFileColNames <- c("cellnumber", "HSDPA.UE.Mean.Cell", "HSDPA.MeanChThroughput")
res <- NULL
for (i in seq_along(filenames)) {
  f <- read.csv(filenames[i])
  res <- rbind(res, cbind(rep(i, dim(f)[1]), f[, 102], f[, 151]))
}
write.table(res,file=outFile,sep=',',row.names=FALSE,col.names=outFileColNames,quote=FALSE)