import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

// a class to store the dish details
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
		this.heapSize = 0; //heap size is initially 0
		this.queueIndex = a.length-1; //set queue index to the last position of the queue
		
		int i = 0;
		for (i = 0; i < a.length; ++i) //add every dish to queue
			dishQueue[i] = new Dish(i,a[i],b[i]);

		//sort the queue in descending order of arrival time
		quickSort(0,queueIndex);
		/*Arrays.sort(dishQueue,new Comparator<Dish>(){
			public int compare(Dish dish1, Dish dish2)
			{
				return dish2.getArrivalTime()-dish1.getArrivalTime();
			}
		});*/

		int minArrivalTime = dishQueue[queueIndex].getArrivalTime(); //get the minimum arrival time from the queue
		while (queueIndex >= 0 && dishQueue[queueIndex].getArrivalTime() == minArrivalTime) //for all the dishes that have the minimum arrivel time
			dishes[heapSize++] = dishQueue[queueIndex--]; //add them to heap
		build(); //call build heap method
		//printHeap();
	}
	private void quickSort(int low, int high)
	{
		if (low < high)
        {
            int pivot = partition(low, high);
            quickSort(low, pivot-1);
            quickSort(pivot+1, high);
        }
	}
	private int partition(int low, int high)
	{
		 //taking the last element as pivot
        Dish pivot = dishQueue[high];

        //set i outside the window
        int i = low-1;

        //for every element in the window except the pivot
        for (int j = low; j < high; ++j)
        {
            //if the element is smaller
            if (dishQueue[j].getArrivalTime() >= pivot.getArrivalTime())
            {
                //increase i and swap with j
                ++i;
                Dish temp = dishQueue[i];
                dishQueue[i] = dishQueue[j];
                dishQueue[j] = temp;
            }
        }

        //store the pivot at its correct position
        Dish temp = dishQueue[i+1];
        dishQueue[i+1] = dishQueue[high];
        dishQueue[high] = temp;

        //return the index of pivot
        return i+1;
	}
	private int left(int i) { return (2*i)+1; } //standard left child method
	private int right(int i) { return (2*i)+2; } //standard right child method
	private int parent(int i) { return (i-1)/2; } //standard parent method
	private void build() //standard build heap method
	{
		for (int i = (heapSize/2)-1; i >= 0; --i)
			heapify(i);
	}
	private void heapify(int root) //modified heapify method
	{
		int min = root; //store root in min
		if (left(root) < heapSize)
		{
			if (dishes[left(root)].getRemainingTime() < dishes[min].getRemainingTime()) //if left child has less remaining time, update min
				min = left(root);
			else if (dishes[left(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[left(root)].getArrivalTime() < dishes[min].getArrivalTime()) //if remaining time is same but arrival time is smaller, then also update min
				min = left(root);
			else if (dishes[left(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[left(root)].getArrivalTime() == dishes[min].getArrivalTime() && dishes[left(root)].getId() < dishes[min].getId()) //if both are same then update min according to the process id
				min = left(root);
		}
		if (right(root) < heapSize)
		{
			if (dishes[right(root)].getRemainingTime() < dishes[min].getRemainingTime()) //if right child has less remaining time, update min
				min = right(root);
			else if (dishes[right(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[right(root)].getArrivalTime() < dishes[min].getArrivalTime()) //if remaining time is same but arrival time is smaller, then also update min
				min = right(root);
			else if (dishes[right(root)].getRemainingTime() == dishes[min].getRemainingTime() && dishes[right(root)].getArrivalTime() == dishes[min].getArrivalTime() && dishes[right(root)].getId() < dishes[min].getId()) //if both are same then update min according to the process id
				min = right(root);
		}
		if (min != root) //if min and root are not same, swap and heapify min
		{
			Dish temp = dishes[min];
			dishes[min] = dishes[root];
			dishes[root] = temp;
			heapify(min);
		}
	}
	public Dish getNextDish() //standard extract root method  (dish with minimum criteria of BT->AT->ID)
	{
		if (heapSize < 1) return null; //return null if heap is empty
		Dish result = dishes[0]; //hold the root
		dishes[0] = dishes[--heapSize]; //replace root with the last element
		heapify(0); //heapify root
		return result; //return held value
	}
	public Dish seeNextDish() //return root without extracting it
	{
		return dishes[0];
	}
	public Dish seeNextArrivalPoint() //return the next arrival time point
	{
		if (dishQueue == null || queueIndex < 0) return null; //return null if dish queue is empty
		return dishQueue[queueIndex]; //return the last element of the queue
	}
	public void putBack(Dish dish) //put back the dish with remaining time into heap
	{
		dishes[heapSize] = dish; //store at the last index
		int i = heapSize++; //store the index in i
		Dish temp = null;
		while (parent(i) >= 0 && ((dishes[parent(i)].getRemainingTime() > dishes[i].getRemainingTime()) || (dishes[parent(i)].getRemainingTime() == dishes[i].getRemainingTime() && dishes[parent(i)].getArrivalTime() > dishes[i].getArrivalTime()) || (dishes[parent(i)].getRemainingTime() == dishes[i].getRemainingTime() && dishes[parent(i)].getArrivalTime() == dishes[i].getArrivalTime() && dishes[parent(i)].getId() > dishes[i].getId()))) //till parent is larger than the child
		{
			//swap
			temp = dishes[i];
			dishes[i] = dishes[parent(i)];
			dishes[parent(i)] = temp;
			i = parent(i); //update parent
		}
	}
	public boolean isEmpty() //standard isEmpty method
	{
		if (heapSize == 0) return true;
		return false;
	}
	public void updateHeap(int timePoint) //adds dishes from queue to heap
	{
		while (queueIndex >= 0 && dishQueue[queueIndex].getArrivalTime() == timePoint) //till arrival time is equal to the current time point
			putBack(dishQueue[queueIndex--]); //add dish from queue to heap
		if (queueIndex == -1) //if queue is empty
		{
			dishQueue = null; //set queue to null
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

		BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the input file name-");
		File inputFile = new File(cmdReader.readLine());
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		
		while ((line = reader.readLine()) != null) //for each test case
		{
			//redefine the results
			startTimes = new ArrayList<Integer>();
			stopTimes = new ArrayList<Integer>();
			ids = new ArrayList<Integer>();
			tokens = line.trim().split(" ");
			n = tokens.length;

			//store arrival times and generate random burst times
			for (i = 0; i < n; ++i)
			{
				arrivalTimestamps[i] = Integer.parseInt(tokens[i]);
				burstTimes[i] = (int)(Math.random()*(max-min+1)+min);
			}
			heap = new Heap(arrivalTimestamps,burstTimes); //create a heap

			timer = heap.seeNextDish().getArrivalTime(); //get the arrival time of the first process which is going to arrive
			if (timer != 0) //if timer is not 0, add idle entry
			{
				startTimes.add(0);
				stopTimes.add(timer);
				ids.add(-1);
			}
			while (!heap.isEmpty()) //till heap has more elements
			{
				//heap.printHeap();
				currDish = heap.getNextDish(); //get the next dish
				nextDish = heap.seeNextArrivalPoint(); //see the next dish from queue which has not yet arrived
				if (nextDish != null) //if queue has dishes
					currDuration = Math.min(currDish.getRemainingTime(),nextDish.getArrivalTime()-timer); //store the minimum out of current remaining time and the duration till the preemption time
				else //if queue has no more new dishes
					currDuration = currDish.getRemainingTime(); //store the current remaining time
				
				if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == currDish.getId()) //if the previous process is not preempted then merge the current entry to it
					stopTimes.set(stopTimes.size()-1,new Integer(timer+currDuration));
				else //if the previous process is preempted then add a new entry
				{
					startTimes.add(timer);
					stopTimes.add(timer+currDuration);
					ids.add(currDish.getId());
				}
				currDish.setRemainingTime(currDish.getRemainingTime()-currDuration); //update the remaining time of the current dish
				if (currDish.getRemainingTime() > 0) //if time is remaining
					heap.putBack(currDish); //add the dish back to the heap

				timer += currDuration; //increase the timer
				if (nextDish != null && nextDish.getArrivalTime() > timer) //if queue has more new dishes to come and the arrival time of the new dishes is more than the current time point
				{
					if (!heap.isEmpty()) //if heap is non empty then continue
						continue;
					if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == -1) //if the previous process is also idle then merge the current entry to it
						stopTimes.set(stopTimes.size()-1,new Integer(nextDish.getArrivalTime()));
					else //if the previous process was not idle then add a new entry
					{
						startTimes.add(timer);
						stopTimes.add(nextDish.getArrivalTime());
						ids.add(-1);
					}
				}
				else if (nextDish != null && nextDish.getArrivalTime() == timer) //if queue has more new dishes to come and the arrival time of the new dishes is equal to the current time point
					heap.updateHeap(timer); //add dishes from queue to heap
				else if (nextDish != null) //if queue has more new dishes to come and the arrival time of the new dishes is less than the current time point
					System.out.println("Calculation went wrong"); //according to the logic it should not be allowed
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
			avgWaitingTime = 0;
			for (i = 0; i < n; ++i)
				avgWaitingTime += (double)waitingTimestamps[i];
			avgWaitingTime /= n;
			System.out.println(avgWaitingTime);
			System.out.println();
		}

		reader.close();
		cmdReader.close();
	}
}