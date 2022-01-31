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
	private int arrival; // arrival time
	private int duration; // remaining time
	Dish(int id, int arrival, int burst)
	{
		this.id = id;
		this.arrival = arrival;
		this.duration = burst;
	}
	public int getId() { return this.id; }
	public int getArrival() { return this.arrival; }
	public int getDuration() { return this.duration; }
	
	public void setDuration (int d) { this.duration = d; }
}

class Heap
{
	private Dish[] allDishes; // queue for all the dishes
	private Dish[] dishes; //heap array of dishes
	private int heapSize;  
	Heap(int[] a, int[] b)
	{
		this.allDishes = new Dish[a.length];
		this.dishes = new Dish[a.length];
		this.heapSize = 0;
		int index = 0;
		boolean addedToHeap = false;
		int j = 0;
		for (int i = 0; i < a.length; ++i)//TODO: confusing logic
		{
			addedToHeap = false;
			if (this.dishes[0] != null && this.dishes[0].getArrival() > a[i])
			{
				for (j = 0; j < heapSize; ++j) this.allDishes[index++] = this.dishes[j];
				this.dishes = new Dish[a.length];
				this.heapSize = 0;
			}
			if (this.dishes[0] == null || this.dishes[0].getArrival() == a[i])
			{
				this.dishes[heapSize++] = new Dish(i,a[i],b[i]);
				addedToHeap = true;
			}
			if (!addedToHeap) this.allDishes[index++] = new Dish(i,a[i],b[i]);
		}
		Dish[] temp = new Dish[index];
		for (int i = 0; i < index; ++i)
			temp[i] = allDishes[i];
		allDishes = temp;
		build();
		Arrays.sort(allDishes,new Comparator<Dish>(){
			public int compare(Dish dish1, Dish dish2)
			{
				return dish1.getArrival()-dish2.getArrival();
			}
		});
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
			if (dishes[left(root)].getDuration() < dishes[min].getDuration())
				min = left(root);
			else if (dishes[left(root)].getDuration() == dishes[min].getDuration() && dishes[left(root)].getArrival() < dishes[min].getArrival())
				min = left(root);
			else if (dishes[left(root)].getDuration() == dishes[min].getDuration() && dishes[left(root)].getArrival() == dishes[min].getArrival() && dishes[left(root)].getId() < dishes[min].getId())
				min = left(root);
		}
		if (right(root) < heapSize)
		{
			if (dishes[right(root)].getDuration() < dishes[min].getDuration())
				min = right(root);
			else if (dishes[right(root)].getDuration() == dishes[min].getDuration() && dishes[right(root)].getArrival() < dishes[min].getArrival())
				min = right(root);
			else if (dishes[right(root)].getDuration() == dishes[min].getDuration() && dishes[right(root)].getArrival() == dishes[min].getArrival() && dishes[right(root)].getId() < dishes[min].getId())
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
		if (allDishes == null || allDishes.length == 0) return null;
		return allDishes[0];
	}
	public void putBack(Dish dish) //put back the dish with remaining time into heap
	{
		dishes[heapSize] = dish;
		int i = heapSize;
		++heapSize;
		Dish temp = null;
		while (parent(i) >= 0 && ((dishes[parent(i)].getDuration() > dishes[i].getDuration()) || (dishes[parent(i)].getDuration() == dishes[i].getDuration() && dishes[parent(i)].getArrival() > dishes[i].getArrival()) || (dishes[parent(i)].getDuration() == dishes[i].getDuration() && dishes[parent(i)].getArrival() == dishes[i].getArrival() && dishes[parent(i)].getId() > dishes[i].getId())))
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
		int i = 0;
		for (i = 0; i < allDishes.length; ++i)
		{
			if (allDishes[i].getArrival() == timePoint)
				putBack(allDishes[i]);
			else break;
		}
		if (i == allDishes.length)
		{
			allDishes = null;
			return;
		}
		Dish[] temp = new Dish[allDishes.length-i]; //TODO: decrease order
		int j = allDishes.length-1;
		for (i = temp.length-1; i >= 0; --i)
			temp[i] = allDishes[j--];
		allDishes = temp;
	}
	/*public void printHeap()
	{
		System.out.println("Heap-");
		for (int i = 0; i < heapSize; ++i)
			System.out.print("("+dishes[i].getId()+","+dishes[i].getArrival()+","+dishes[i].getDuration()+")-");
		System.out.println();
		System.out.println("Queue-");
		if (allDishes != null)
			for (int i = 0; i < allDishes.length; ++i)
				System.out.print("("+allDishes[i].getId()+","+allDishes[i].getArrival()+","+allDishes[i].getDuration()+")-");
		System.out.println();
	}*/
}

class Restaurant
{
	private static int max = 8, min = 1; //max BT, min BT for any order 
	public static void main(String[] args)throws IOException
	{
		String line = "";
		String[] tokens = null;
		int n = 10; // CAN CHANGE // number of process
		int i = 0, timer = 0, currDuration = -1;
		int[] arrivalTimestamps = new int[n], burstTimes = new int[n];
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

			timer = heap.seeNextDish().getArrival();
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
					currDuration = Math.min(currDish.getDuration(),nextDish.getArrival()-timer);
				else
					currDuration = currDish.getDuration();
				
				if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == currDish.getId())
					stopTimes.set(stopTimes.size()-1,new Integer(timer+currDuration));
				else
				{
					startTimes.add(timer);
					stopTimes.add(timer+currDuration);
					ids.add(currDish.getId());
				}
				currDish.setDuration(currDish.getDuration()-currDuration);
				if (currDish.getDuration() > 0)
					heap.putBack(currDish);

				timer += currDuration;
				if (nextDish != null && nextDish.getArrival() > timer)
				{
					if (!heap.isEmpty())
						continue;
					if (ids.size() != 0 && (Integer)ids.get(ids.size()-1) == -1)
						stopTimes.set(stopTimes.size()-1,new Integer(nextDish.getArrival()));
					else
					{
						startTimes.add(timer);
						stopTimes.add(nextDish.getArrival());
						ids.add(-1);
					}
				}
				else if (nextDish != null && nextDish.getArrival() == timer)
				{
					heap.updateHeap(timer);
				}
				else if (nextDish != null)
				{
					System.out.println("Calculation went wrong");
				}
			}

			System.out.println();
			System.out.println("Given Testcase-");
			System.out.print("AT- ");
			for (i = 0; i < arrivalTimestamps.length; ++i)
				System.out.print(arrivalTimestamps[i]+" ");
			System.out.println();
			System.out.print("BT- ");
			for (i = 0; i < arrivalTimestamps.length; ++i)
				System.out.print(burstTimes[i]+" ");
			System.out.println();
			System.out.println("START\tEND\tID");
			for (i = 0; i < ids.size(); ++i)
				System.out.println(startTimes.get(i)+"\t"+stopTimes.get(i)+"\t"+((Integer)ids.get(i) == -1 ? "idle" : ids.get(i)));
		}

		reader.close();
	}
}
//TODO: gantt chart printing