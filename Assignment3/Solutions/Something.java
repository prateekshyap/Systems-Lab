import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;

class Something
{
	public static void main(String[] args)throws IOException
	{
		String[] dirs = {"DigitalOcean","Amazon","Flipkart","Cricbuzz","Codeforces","Instagram","Spotify","BookMyShow","Youtube","Twitter"};
		String[] dates = {"Mar19","Mar20","Mar21","Mar22","Mar23","Mar24"};
		String[] times = {" 0"," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		String[] packets = {"56","64","128","256","512","1024","2048"};
		String line = "", prevLine = "";
		String fileName = "";
		File file = null;
		BufferedReader fileReader = null;
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File("data.csv")));
		StringBuffer fileString = new StringBuffer("");
		ArrayList<ArrayList<Double>> rtts = new ArrayList<ArrayList<Double>>();
		for (int dir = 0; dir < 9; ++dir)
		{
			fileWriter = new BufferedWriter(new FileWriter(new File(dirs[dir]+".csv")));
			for (int dt = 0; dt < dates.length; ++dt)
			{
				rtts = new ArrayList<ArrayList<Double>>();
				ArrayList<Integer> existingTimes = new ArrayList<Integer>();
				for (int t = 0; t < times.length; ++t)
				{
					ArrayList<Double> newRow = new ArrayList<Double>();
					for (int s = 0; s < packets.length; ++s)
					{
						fileName = dirs[dir];
						fileName += "/";
						fileName += dates[dt];
						fileName += "_";
						fileName += times[t];
						fileName += ":00_";
						fileName += packets[s];
						fileName += "B.txt";

						try{
						fileReader = new BufferedReader(new FileReader(new File(fileName)));
						while ((line = fileReader.readLine()) != null)
							prevLine = new String(line);
						int slashCounter = 0, start = -1, end = -1;
						for (int i = 0; i < prevLine.length(); ++i)
						{
							if (prevLine.charAt(i) == '/') ++slashCounter;
							if (slashCounter == 4 && start == -1) start = i+1;
							if (slashCounter == 5) { end = i; break; }
						}
						System.out.println(dirs[dir]+" "+dates[dt]+" "+times[t]+" "+packets[s]+" = "+prevLine.length());
						if (start == -1 && end == -1) newRow.add(Double.MAX_VALUE);
						else newRow.add(Double.parseDouble(prevLine.substring(start,end)));
						}catch(FileNotFoundException fe)
						{
							System.out.println(fileName+" not found!");
						}
						if (fileReader != null) fileReader.close();
					}
					if (newRow.size() != 0)
					{
						existingTimes.add(Integer.parseInt(times[t].trim()));
						rtts.add(newRow);
					}
				}
				fileString = new StringBuffer("");
				for (int s = 0; s < packets.length; ++s)
				{
					if (fileString.length() != 0) fileString.append(",");
					fileString.append(packets[s]);
				}
				fileWriter.write(dates[dt]+","); fileWriter.write(fileString.toString()); fileWriter.newLine();
				for (int t = 0; t < existingTimes.size(); ++t)
				{
					ArrayList<Double> tempRtt = (ArrayList)rtts.get(t);
					fileString = new StringBuffer(Integer.toString((Integer)existingTimes.get(t)));
					for (int s = 0; s < packets.length; ++s)
					{
						fileString.append(",");
						double val = (Double)tempRtt.get(s);
						if (val == Double.MAX_VALUE)
							fileString.append("INF");
						else
							fileString.append(Double.toString(val));
					}
					fileWriter.write(fileString.toString()); fileWriter.newLine();
				}
			}
			fileWriter.close();
		}
	}
}
