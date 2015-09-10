library(rCharts)
library(rMaps)
library(plyr)
M <- Leaflet$new()
M$setView(c(37.4032836,  -121.91278), 10)
M$tileLayer(provider = "MapQuestOpen.OSM")

data = read.csv("output/gotomap.csv")
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
