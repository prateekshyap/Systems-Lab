import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class Dish
{
	private int id; //unique order id
	private int arrivalTime; // arrival time
	private int remainingTime; // remaining time
	Dish(int id, int arrivalTime, int burstTime)
	{
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.remainingTime = burstTime;
	}
	public int getId() { return this.id; }
	public int getArrivalTime() { return this.arrivalTime; }
	public int getRemainingTime() { return this.remainingTime; }
	
	public void setRemainingTime (int d) { this.remainingTime = d; }
}

class Heap
{
	private Dish[] dishQueue; //queue for all the dishes
	private Dish[] dishes; //heap array of dishes
	private int heapSize;
	private int queueIndex;  
	Heap(int[] a, int[] b)
	{
		this.dishQueue = new Dish[a.length];
		this.dishes = new Dish[a.length];
		this.heapSize = 0;
		this.queueIndex = a.length-1;
		//int index = 0;
		int i = 0;
		for (i = 0; i < a.length; ++i)
			dishQueue[i] = new Dish(i,a[i],b[i]);
		Arrays.sort(dishQueue,new Comparator<Dish>(){
			public int compare(Dish dish1, Dish dish2)
			{
				return dish2.getArrivalTime()-dish1.getArrivalTime();
			}
		});
		int minArrivalTime = dishQueue[queueIndex].getArrivalTime();
		while (queueIndex >= 0 && dishQueue[queueIndex].getArrivalTime() == minArrivalTime)
			dishes[heapSize++] = dishQueue[queueIndex--];
		build();
		//printHeap();
	}
	private int left(int i) { return (2*i)+1; }
	private int right(int i) { return (2*i)+2; }
	private int parent(int i) { return (i-1)/2; }
	private void build()
	{
		for (int i = (heapSize/2)-1; i >= 0; --i)
			heapify(i);
	}
	private void heapify(int root)
	{
		int min = root;
		if (left(root) < heapSize)
		{
			if (dishes[left(root)].getRemainingTime() < dishes[min].getRemainingTime())
				min = left(root);
			else if (dishes[left(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[left(root)].getArrivalTime() < dishes[min].getArrivalTime())
				min = left(root);
			else if (dishes[left(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[left(root)].getArrivalTime() == dishes[min].getArrivalTime() && dishes[left(root)].getId() < dishes[min].getId())
				min = left(root);
		}
		if (right(root) < heapSize)
		{
			if (dishes[right(root)].getRemainingTime() < dishes[min].getRemainingTime())
				min = right(root);
			else if (dishes[right(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[right(root)].getArrivalTime() < dishes[min].getArrivalTime())
				min = right(root);
			else if (dishes[right(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[right(root)].getArrivalTime() == dishes[min].getArrivalTime() && dishes[right(root)].getId() < dishes[min].getId())
				min = right(root);
		}
		if (min != root)
		{
			Dish temp = dishes[min];
			dishes[min] = dishes[root];
			dishes[root] = temp;
			heapify(min);
		}
	}
	public Dish getNextDish() //extract root or dish with minimum criteria of BT->AT->ID
	{
		if (heapSize < 1) return null;
		Dish result = dishes[0];
		dishes[0] = dishes[heapSize-1];
		--heapSize;
		heapify(0);
		return result;
	}
	public Dish seeNextDish() //return minimum dish
	{
		return dishes[0];
	}
	public Dish seeNextArrivalPoint()
	{
		if (dishQueue == null || queueIndex < 0) return null;
		return dishQueue[queueIndex];
	}
	public void putBack(Dish dish) //put back the dish with remaining time into heap
	{
		dishes[heapSize] = dish;
		int i = heapSize;
		++heapSize;
		Dish temp = null;
		while (parent(i) >= 0 && ((dishes[parent(i)].getRemainingTime() > dishes[i].getRemainingTime()) || (dishes[parent(i)].getRemainingTime() == dishes[i].getRemainingTime() && dishes[parent(i)].getArrivalTime() > dishes[i].getArrivalTime()) || (dishes[parent(i)].getRemainingTime() == dishes[i].getRemainingTime() && dishes[parent(i)].getArrivalTime() == dishes[i].getArrivalTime() && dishes[parent(i)].getId() > dishes[i].getId())))
		{
			temp = dishes[i];
			dishes[i] = dishes[parent(i)];
			dishes[parent(i)] = temp;
			i = parent(i);
		}
	}
	public boolean isEmpty()
	{
		if (heapSize == 0) return true;
		return false;
	}
	public void updateHeap(int timePoint)
	{
		while (queueIndex >= 0 && dishQueue[queueIndex].getArrivalTime() == timePoint)
			putBack(dishQueue[queueIndex--]);
		if (queueIndex == -1)
		{
			dishQueue = null;
			return;
		}
	}
	public void printHeap()
	{
		System.out.println("Heap-");
		for (int i = 0; i < heapSize; ++i)
			System.out.print("("+dishes[i].getId()+","+dishes[i].getArrivalTime()+","+dishes[i].getRemainingTime()+")-");
		System.out.println();
		System.out.println("Queue-");
		if (dishQueue != null)
			for (int i = 0; i <= queueIndex; ++i)
				System.out.print("("+dishQueue[i].getId()+","+dishQueue[i].getArrivalTime()+","+dishQueue[i].getRemainingTime()+")-");
		System.out.println();
	}
}

class Restaurant
{
	private static int max = 8, min = 1; //max BT, min BT for any order 
	public static void main(String[] args)throws IOException
	{
		String line = "";
		String[] tokens = null;
		int n = 10; // CAN CHANGE // number of process
		int i = 0, j = 0, timer = 0, currDuration = -1;
		double avgWaitingTime = 0.0;
		int[] arrivalTimestamps = new int[n], burstTimes = new int[n], completionTimestamps = new int[n], turnAroundTimestamps = new int[n], waitingTimestamps = new int[n];
		ArrayList<Integer> startTimes = null;
		ArrayList<Integer> stopTimes = null;
		ArrayList<Integer> ids = null;
		
		Heap heap = null;
		Dish currDish = null, nextDish = null;

		File inputFile = new File("arrival.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		
		while ((line = reader.readLine()) != null)
		{
			startTimes = new ArrayList<Integer>();
			stopTimes = new ArrayList<Integer>();
			ids = new ArrayList<Integer>();
			tokens = line.trim().split(" ");
			n = tokens.length;
			for (i = 0; i < n; ++i)
			{
				arrivalTimestamps[i] = Integer.parseInt(tokens[i]);
				burstTimes[i] = (int)(Math.random()*(max-min+1)+min);
			}
			heap = new Heap(arrivalTimestamps,burstTimes);

			timer = heap.seeNextDish().getArrivalTime();
			if (timer != 0)
			{
				startTimes.add(0);
				stopTimes.add(timer);
				ids.add(-1);
			}
			while (!heap.isEmpty())
			{
				//heap.printHeap();
				currDish = heap.getNextDish();
				nextDish = heap.seeNextArrivalPoint();
				if (nextDish != null)
					currDuration = Math.min(currDish.getRemainingTime(),nextDish.getArrivalTime()-timer);
				else
					currDuration = currDish.getRemainingTime();
				
				if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == currDish.getId())
					stopTimes.set(stopTimes.size()-1,new Integer(timer+currDuration));
				else
				{
					startTimes.add(timer);
					stopTimes.add(timer+currDuration);
					ids.add(currDish.getId());
				}
				currDish.setRemainingTime(currDish.getRemainingTime()-currDuration);
				if (currDish.getRemainingTime() > 0)
					heap.putBack(currDish);

				timer += currDuration;
				if (nextDish != null && nextDish.getArrivalTime() > timer)
				{
					if (!heap.isEmpty())
						continue;
					if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == -1)
						stopTimes.set(stopTimes.size()-1,new Integer(nextDish.getArrivalTime()));
					else
					{
						startTimes.add(timer);
						stopTimes.add(nextDish.getArrivalTime());
						ids.add(-1);
					}
				}
				else if (nextDish != null && nextDish.getArrivalTime() == timer)
					heap.updateHeap(timer);
				else if (nextDish != null)
					System.out.println("Calculation went wrong");
			}

			//calculating timestamps
			for (i = 0; i < ids.size(); ++i) //completion time
				if ((Integer)ids.get(i) != -1) completionTimestamps[(Integer)ids.get(i)] = (Integer)stopTimes.get(i);
			for (i = 0; i < n; ++i) //turn around time
				turnAroundTimestamps[i] = completionTimestamps[i] - arrivalTimestamps[i];
			for (i = 0; i < n; ++i) //waiting time
				waitingTimestamps[i] = turnAroundTimestamps[i] - burstTimes[i];


			//printing process details and gantt chart
			System.out.println();
			System.out.println();
			System.out.println("===============================================================================================================");
			System.out.println();
			System.out.println("Process Details-");
			System.out.println("---------------------------------------------");
			System.out.println("|  PID  |  AT  |  BT  |  CT  |  TAT  |  WT  |");
			System.out.println("---------------------------------------------");
			for (i = 0; i < n; ++i)
				System.out.println("|   "+i+"   |  "+(arrivalTimestamps[i] < 10 ? " " : "")+arrivalTimestamps[i]+"  |   "+burstTimes[i]+"  |  "+(completionTimestamps[i] < 10 ? " " : "")+completionTimestamps[i]+"  |   "+(turnAroundTimestamps[i] < 10 ? " " : "")+turnAroundTimestamps[i]+"  |  "+(waitingTimestamps[i] < 10 ? " " : "")+waitingTimestamps[i]+"  |");
			System.out.println("---------------------------------------------");
			System.out.println();
			System.out.println("Gantt Chart-");
			System.out.print("-");
			for (i = 0; i < ids.size(); ++i)
			{
				currDuration = (Integer)stopTimes.get(i)-(Integer)startTimes.get(i);
				for (j = 0; j < currDuration; ++j)
					System.out.print("--");
				System.out.print("-");
			}
			System.out.println();
			System.out.print("|");
			for (i = 0; i < ids.size(); ++i)
			{
				currDuration = (Integer)stopTimes.get(i)-(Integer)startTimes.get(i);
				for (j = 0; j < (currDuration/2)-1; ++j)
					System.out.print("  ");
				if ((Integer)ids.get(i) == -1) System.out.print("NA");
				else if ((Integer)ids.get(i) < 10) System.out.print(" "+(Integer)ids.get(i));
				else System.out.print((Integer)ids.get(i));
				for (; j < currDuration-1; ++j)
					System.out.print("  ");
				System.out.print("|");
			}
			System.out.println();
			System.out.print("-");
			for (i = 0; i < ids.size(); ++i)
			{
				currDuration = (Integer)stopTimes.get(i)-(Integer)startTimes.get(i);
				for (j = 0; j < currDuration; ++j)
					System.out.print("--");
				System.out.print("-");
			}
			System.out.println();
			System.out.print("0");
			for (i = 0; i < ids.size(); ++i)
			{
				currDuration = (Integer)stopTimes.get(i)-(Integer)startTimes.get(i);
				for (j = 0; j < currDuration-1; ++j)
					System.out.print("  ");
				System.out.print(((Integer)stopTimes.get(i) < 10 ? "  " : " ")+(Integer)stopTimes.get(i));
			}
			System.out.println();
			System.out.println();
			System.out.println("Average Waiting Time-");
			for (i = 0; i < n; ++i)
				avgWaitingTime += (double)waitingTimestamps[i];
			avgWaitingTime /= n;
			System.out.println(avgWaitingTime);
			System.out.println();
		}

		reader.close();
	}
}