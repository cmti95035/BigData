ts <- read.csv('RNC1536.csv')
ts1 <- ts[290:337, 2]
for (i in c(1:6)) ts1 <- cbind(ts1, ts[(2 + (i - 1) * 48):(49 + (i - 1) * 48), 2])
ts1 <- cbind.data.frame(ts1, ts$Time[2:49])
names(ts1) <- c('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Time')
plot(as.POSIXlt(ts1$Time), ts1[, 1], cex = 0.5, xlab = "Time", ylab = "VS.CS.Erlang.RNC", main = "VS.CS.Erlang.RNC\n for city center\n on M-F (black), Sat (green) and Sun (red)")
for (i in c(2:5)) points(as.POSIXlt(ts1$Time), ts1[, i], cex = 0.5)
points(as.POSIXlt(ts1$Time), ts1[, 6], cex = 0.5, col = "green")
points(as.POSIXlt(ts1$Time), ts1[, 7], cex = 0.5, col = "red")