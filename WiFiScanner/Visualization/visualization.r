#install.packages("dplyr")
#install.packages("ggplot2")

library("dplyr")
library("ggplot2")

csv = read.csv2("../CSV data/wifimeasurements.csv", header = FALSE, sep = ",",
          dec = ";", fill = TRUE, comment.char = "")

csv = data.frame(csv)

roomNames = distinct(csv,V1)

macAdd = c(toString(csv$V3[1]), toString(csv$V3[2]), toString(csv$V3[3]), toString(csv$V3[4]))

WiFiData = NA

for(name in roomNames$V1)
{
  for(mac in macAdd)
  {
    WiFiData = rbind(WiFiData,c(name,  mean(csv[csv$V3 ==  mac & csv$V1 == name,5]), mac))
  }
}

WiFiData = WiFiData[2:nrow(WiFiData),]
WiFiData = data.frame(WiFiData)
names(WiFiData)[names(WiFiData) == "X3"] = "Mac address"
test = transform(WiFiData, X2 = as.numeric(as.character(X2)))
ggplot(data=test, aes(x=X1, y=X2, group= Mac.address, color= Mac.address)) +
  geom_line() +
  geom_point() +
  ylab("Signal Strength (dBm)") +
  xlab("") + 
  theme(legend.title = element_text(size=16, face="bold"))
  
  
