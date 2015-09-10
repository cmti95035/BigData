rm(list = ls())

filenames <- list.files("data/feature24mean", pattern = "*.csv", full.names = TRUE)
library(plyr)
library(dostats)
outFile <- "AllBaseStation_0821.csv"
outFileColNames <- c("cellnumber", "Time", "HSDPA.UE.Mean.Cell", "HSDPA.MeanChThroughput")
res <- NULL
for (i in seq_along(filenames)) {
  f <- read.csv(filenames[i])
  res <- rbind(res, cbind(rep(i, dim(f)[1]), f[, 1], f[, 3], f[,4]))
}
# write.table(res,file=outFile,sep=',',row.names=FALSE,col.names=outFileColNames,quote=FALSE)

library(scatterplot3d)
scatterplot3d((res[,2])/2, res[,1], res[, 3], color = res[, 3],
              zlim = c(0, 10),
              ylab = "CellNumber", # y axis 
              xlab = "Time",     # x axis
              zlab = "HSDPA.Call.Drop.Ratio",    # z axis
              main="BaseStation KPI",
              type = "p",
              pch = 16)

scatterplot3d(res[,4], res[,1], res[, 3], 
              color = res[, 3],
              zlim = c(95, 100),
              #xlim = c(0, 8),
              ylab = "CellNumber", # x axis 
              xlab = "HSDPA.UE.Mean.Cell",     # y axis
              zlab = "HSDPA.Call.Drop.Ratio",    # z axis
              main="BaseStation KPI",
              type = "p",
              pch = 16
             )

library(plot3D)
scatter3D(res[,4], res[,1], res[, 3], 
              color = res[, 3],
              clim = c(0, 10),
              zlim = c(0, 10),
              xlim = c(0, 50),
              ylab = "CellNumber", # y axis 
              xlab = "HSDPA.UE.Mean.Cell",     # x axis
              zlab = "HSDPA.Call.Drop.Ratio",    # z axis
              main="BaseStation KPI",
              type = "p",
              theta = 40,
              phi = 10,
              pch = 16,
              panel.first = TRUE
)


