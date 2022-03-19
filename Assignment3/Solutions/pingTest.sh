#!/bin/bash
dateVar=$(date +'%b%d')
timeVar=$(date +'%k')
count=5
ipArr=("46.101.167.146" "176.32.103.205" "163.53.78.110" "23.33.238.186" "213.248.110.126" "2a03:2880:f212:e5:face:b00c:0:4420" "35.186.224.25" "142.250.64.110" "104.16.123.37" "104.244.42.1")
nameArr=(DigitalOcean Amazon Flipkart Cricbuzz Codeforces Instagram Spotify Youtube BookMyShow Twitter)
for i in 0 1 2 3 4 5 6 7 8 9
do
	for packetSize in 56 64 128 256 512 1024 2048
	do
		echo "${nameArr[i]} $packetSize"
		fileName="${dateVar}_${timeVar}:00_${packetSize}B.txt"
		ping -c $count -s $packetSize ${ipArr[i]} -q > ${nameArr[i]}/$fileName
	done
done