#!/bin/bash
dateVar=$(date +'%b%d')
timeVar=$(date +'%k')
count=25
ipArr=("46.101.167.146" "176.32.103.205" "163.53.78.110" "23.33.238.186" "213.248.110.126" "2a03:2880:f212:e5:face:b00c:0:4420" "35.186.224.25" "142.250.64.110" "104.16.123.37" "104.244.42.1")
nameArr=(DigitalOcean Amazon Flipkart Cricbuzz Codeforces Instagram Spotify Youtube BookMyShow Twitter)
for i in 0 1 2 3 4 5 6 7 8 9
do
	echo "${nameArr[i]} $packetSize"
	fileName="traceroute_${dateVar}_${timeVar}:00.txt"
	traceroute ${ipArr[i]} > "${nameArr[i]}/$fileName"
	sudo traceroute ${ipArr[i]} -T >> "${nameArr[i]}/$fileName"
done
