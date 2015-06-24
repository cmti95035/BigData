#!/bin/Rscript
args <- commandArgs(TRUE)
CorrAns = args[1]
library(ggmap)
library(lattice)
library(animation)
library(XML)
#get google map with (longitude, latitude), zoom level and hybrid type
localmap = get_map(location = c(lon = -105.265, lat = 40.005), zoom = 15, maptype = 'hybrid', source = "google")
localmap = ggmap(localmap, extent = 'device')
df <- read.csv(CorrAns)
dynam <- function(x) {
  df$LTE_UE_Tx_Power <- df[,x]
#plot geom_point onto localmap with x as longitude, y as latitude, LTE_UE_Tx_power as colour, alpha as opacity, and for easy to compare, use scale_colour_gradient with low end as green and high end as red
  p <- localmap + geom_point(aes(x = -W, y = N, colour = LTE_UE_Tx_Power), size = 7, alpha = 0.5, data = df)  + scale_colour_gradient(limits=c(-200, 50), low="green", high="red") + labs(title='LTE Measurements at University of Colorado, Boulder\n')
}
t <- seq(5,104)
saveGIF(for (i in t) print(dynam(i)))
