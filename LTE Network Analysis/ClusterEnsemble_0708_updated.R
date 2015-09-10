#filenames <- list.files("mo", pattern = "*.csv", full.names = TRUE)
#library(plyr)
#library(dostats)
#outFile <- "data/feature.csv"
#outFileColNames <- c("V1","V2","V3","V4","V5","V6")
#result <- NULL
#for (i in  seq_along(filenames)) {
#	f <- read.csv(filenames[i])
#    if (dim(f)[1] < 23) next
#    sentence <- f[23, c(6, 8:12)]
#    flag <- FALSE
#    for (j in seq(1:length(sentence))) if (is.na(sentence[j])) flag <- TRUE
#    if (flag) next
#    result <- rbind(result, sentence)
#}

#write.table(result,file=outFile,sep=',',row.names=FALSE,quote=FALSE)

rm(list=ls())

to.similarity.matrix <- function(data) {
	len = length(data)
	smatrix=mat.or.vec(len, len)
	for (i in 1:len-1) {
		smatrix[i,i]=1
		for (j in i+1:len) {
			if (identical(data[i],data[j])) {
				smatrix[i,j]=1
				smatrix[j,i]=1	
			}
		}
	}
	smatrix[len,len]=1
	return(smatrix);
}

to.cluster.vector <- function(smatrix) {
	len = dim(smatrix)[1]
	cvector = mat.or.vec(len, 1)
	cluster = 1
	for (i in 1:len) {
		if (cvector[i]==0) {
			cvector[i] <- cluster
			if ((len-i) > 0) {
				temp = tail(smatrix[i,],len-i)
				temp_len = length(temp)
				for (j in 1:temp_len) {
					if (temp[j]==1) {
						cvector[i+j]=cluster
					}
				}
			}
			cluster = cluster + 1
		}
	}
	return(cvector)
}

round.similarity.matrix <- function(smatrix, threshold) {
	smatrix[smatrix < threshold] <- 0
	smatrix[smatrix >= threshold] <- 1
	return(smatrix)
}


#featureFile <- "data/feature24mean/transformed_61098.csv"
featureFile <- "data/feature.csv"
clusterFile <- "cluster.csv"

clusterFileColNames <- c("CellID","Cluster")

# Prepare data
mydata <- read.csv(featureFile)
#time <- mydata[,1]
#mydata[,1] <- as.numeric(factor(mydata[,1]))

#View(mydata)

# Select related features
#mydata.features = mydata[, c(4,9)]
mydata.features = mydata

# Clean up data

mydata.features$CellID <- NULL

# Remove Inf from Data
mydata.features[is.infinite(as.matrix(mydata.features))] <- 300

#View(mydata.features)


mydata.clusters = 3
round.threshold = 0.5
cluster.method = 5

# K-Means Clustering
fit <- kmeans(mydata.features, mydata.clusters)
smatrix1 = to.similarity.matrix(fit$cluster)


# Ward Hierarchical Clustering
d <- dist(mydata.features)
fit <- hclust(d)
groups <- cutree(fit, k=mydata.clusters)
smatrix2 = to.similarity.matrix(groups)


# Model Based Clustering
library(mclust)
fit <- Mclust(mydata.features)
smatrix3 = to.similarity.matrix(fit$classification)


# DBSCAN
library(fpc)
fit <- dbscan(mydata.features, 0.2)
smatrix4 = to.similarity.matrix(fit$cluster)


# CLARANS
library(cluster)
fit <- clara(mydata.features, mydata.clusters)
smatrix5 = to.similarity.matrix(fit$clustering)


# Clustering Ensemble
smatrix = (smatrix1 + smatrix2 + smatrix3 + smatrix4 + smatrix5)/cluster.method

# Round to Integer
smatrix = round.similarity.matrix(smatrix, round.threshold)

# Convert to Clustering Vector
cvector = to.cluster.vector(smatrix)

clusterResult <- cbind(mydata$CellID,cvector)
write.table(clusterResult,file=clusterFile,sep=',',row.names=FALSE,quote=FALSE)

# Plot result
#cluster <- read.csv("cluster.csv")
#Hour <- mydata[,1]/2
#plot(mydata.features[] , type = "p", col = cvector)
#plot(Hour, mydata.features[,2] , ylab = names(mydata.features)[2], 
#     type = "p", col = cvector)

location <- read.csv("TransmittersVIS491330BLH291348.csv")
finalRes <- cbind(location[1:length(cvector),3:4],cvector + 200)
write.table(finalRes,file="gotomap.csv",sep=',',col.names=c("Latitude","Longitude","Cluster"),row.names=FALSE)

library(rCharts)
library(rMaps)
library(plyr)
M <- Leaflet$new()
M$setView(c(37.4032836,  -121.91278), 10)
M$tileLayer(provider = "MapQuestOpen.OSM")

data = read.csv("gotomap.csv")
data = toJSONArray2(na.omit(data), json = F, names = F)
M$addAssets(jshead = c("http://leaflet.github.io/Leaflet.heat/dist/leaflet-heat.js"))
M$setTemplate(afterScript = sprintf("
<script>
  var addressPoints = %s
  var heat = L.heatLayer(addressPoints, {radius: 20, blur: 15, minOpacity: 0.5}).addTo(map)           
</script>
", rjson::toJSON(data)
))
M