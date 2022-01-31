import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

//import java.util.concurrent.Semaphore;
import java.util.concurrent.*;
// import java.util.InterruptedException;

class Party extends Thread
{
	int id;
	int[] maximumNeed;
	int[] currentAllocation;
	int[] remainingNeed;
	Semaphore mutex,mutex2;
	Party(int id, int[] m, int[] c, int[] r, Semaphore mutex,Semaphore mutex2)
	{
		this.id = id;
		this.maximumNeed = m;
		this.currentAllocation = c;
		this.remainingNeed = r;
		this.mutex = mutex;
		this.mutex2 = mutex2;
		//print();
	}
	public void run()
	{
		try{

			for (int r = 0; r < remainingNeed.length; ++r)
			{
				if (remainingNeed[r] > Resource.available[r])
				{
					mutex.acquire();
					r = -1;
					mutex.release();
					continue;
				}
			}

			mutex.release();
			mutex2.acquire();
			System.out.println("--> Process "+id);
			System.out.print("\tAllocated: ");
			for (int r = 0; r < currentAllocation.length; ++r)
				System.out.print(currentAllocation[r]+" ");
			System.out.println();
			System.out.print("\tNeeded: ");
			for (int r = 0; r < remainingNeed.length; ++r)
				System.out.print(remainingNeed[r]+" ");
			System.out.println();
			System.out.print("\tAvailable: ");
			for (int r = 0; r < Resource.available.length; ++r)
				System.out.print(Resource.available[r]+" ");
			System.out.println();
			System.out.println("\tResource Allocated");
			System.out.println("\tProcess executing...");
			System.out.println("\tExecution completed");
			System.out.println("\tResource released");
			Resource.updateAvailableResources(currentAllocation);
			System.out.print("\tNow Available: ");
			for (int r = 0; r < Resource.available.length; ++r)
				System.out.print(Resource.available[r]+" ");
			System.out.println();
			System.out.println();
			System.out.println();
			mutex2.release();
		//mutex.release();
		//}
		}catch(InterruptedException ie)
		{}
	}
}

class Resource
{
	static int[] available;
	Resource(int[] a)
	{
		available = a;
	}
	public static void updateAvailableResources(int[] currentAllocation)
	{
		for (int r = 0; r < available.length; ++r)
			available[r] += currentAllocation[r];
	}
}

class Restaurant
{
	static int n = 0, m = 0;
	static int[] currentAvailable = null, safeSequence = null, available = null;
	static int[][] currentAllocation = null, maximumNeed = null, remainingNeed = null;

	public static void main(String[] args)throws IOException
	{
		int r = 0, s = 0, p = 0, safeProcessCount = 0;
		String line = "";
		String[] tokens = null;
		boolean set = false;
		boolean[] satisfied = null;
		Party[] processes = null;
		
		File inputFile = new File("input.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));

		tokens = reader.readLine().trim().split(" ");
		n = Integer.parseInt(tokens[0]);
		m = Integer.parseInt(tokens[1]);
		
		currentAllocation = new int[n][m];
		maximumNeed = new int[n][m];
		currentAvailable = new int[m];
		available = new int[m];
		remainingNeed = new int[n][m];
		safeSequence = new int[n];
		satisfied = new boolean[n];
		processes = new Party[n];
		
		read(reader,tokens); //read from input file

		//calculate remaining need
		for (p = 0; p < n; ++p)
			for (r = 0; r < m; ++r)
				remainingNeed[p][r] = maximumNeed[p][r] - currentAllocation[p][r];

		//finding out safe sequence
		for (s = 0; s < n; ++s) //for sequence index
		{
			for (p = 0; p < n; ++p) //for each process
			{
				if (satisfied[p]) continue; //if current process is already satisfied, continue for next process
				set = true; //assume we can allocate resources to pth process
				for (r = 0; r < m; ++r) //for each resource
				{
					if (remainingNeed[p][r] > currentAvailable[r]) //if need exceeds availability
					{
						set = false; //set boolean variable to false
						break;
					}
				}
				if (set) //if allocation possible
				{
					safeSequence[s] = p; //store process index in safe sequence
					satisfied[p] = true; //set true
					++safeProcessCount;
					//add all the previously allocated resources to current available
					for (r = 0; r < m; ++r)
						currentAvailable[r] += currentAllocation[p][r];
					break;
				}
			}
		}

		if (safeProcessCount != n)
		{
			System.out.print("Safe sequence not found");
			return;
		}

		System.out.println("One of the Safe sequences-");
		for (p = 0; p < n; ++p)
			System.out.print("P"+safeSequence[p]+" ");
		System.out.println();

		Semaphore mutex = new Semaphore(0);
		Semaphore mutex2 = new Semaphore(1);
		Resource resource = new Resource(available);
		for (p = 0; p < n; ++p)
			processes[p] = new Party(p,maximumNeed[p],currentAllocation[p],remainingNeed[p],mutex,mutex2);

		for (p = 0; p < n; ++p)
			processes[p].start();

		reader.close();
	}

	public static void read(BufferedReader reader, String[] tokens)throws IOException
	{
		int i = 0, j = 0;

		//reading total available resources
		tokens = reader.readLine().trim().split(" ");
		for (j = 0; j < m; ++j)
		{
			currentAvailable[j] = Integer.parseInt(tokens[j]);
			available[j] = currentAvailable[j];
		}

		//reading current allocation
		for (i = 0; i < n; ++i)
		{
			tokens = reader.readLine().trim().split(" ");
			for (j = 0; j < m; ++j)
				currentAllocation[i][j] = Integer.parseInt(tokens[j]);
		}

		//reading maximum need
		for (i = 0; i < n; ++i)
		{
			tokens = reader.readLine().trim().split(" ");
			for (j = 0; j < m; ++j)
				maximumNeed[i][j] = Integer.parseInt(tokens[j]);
		}
	}

	public static void print()
	{
		int i = 0, j = 0;
		System.out.println("Total Available-");
		for (j = 0; j < m; ++j)
			System.out.print(currentAvailable[j]+"\t");
		System.out.println();

		System.out.println("\tMaximum Need\t\t|Allocation\t\t|Remaining Need");
		//printing current allocation
		for (i = 0; i < n; ++i)
		{
			System.out.print("P"+i+"\t");
			for (j = 0; j < m; ++j)
				System.out.print(maximumNeed[i][j]+"\t");
			System.out.print("|");
			for (j = 0; j < m; ++j)
				System.out.print(currentAllocation[i][j]+"\t");
			System.out.print("|");
			for (j = 0; j < m; ++j)
				System.out.print(remainingNeed[i][j]+"\t");
			System.out.println();
		}
	}
}