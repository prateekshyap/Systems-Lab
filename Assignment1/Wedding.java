import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.concurrent.Semaphore;
import java.util.HashSet;


class Friend extends Thread
{
	private int id; //unique
	private int couponNo; //unique
	private int[] requests; //sequence of requests
	private Semaphore mutex; //semaphore for blocking
	private Heap heap, runningHeap; //heap instance
	Friend(int id, Semaphore mutex)
	{
		this.id = id;
		//generate unique coupon no
		this.couponNo = (int)(Math.random()*(999-100+1)+100);
		while (Wedding.couponNos.contains(this.couponNo))
			++this.couponNo;
		Wedding.couponNos.add(this.couponNo);
		this.requests = new int[(int)(Math.random()*(2-1+1)+1)]; //at most 3 requests
		for (int i = 0; i < this.requests.length; ++i)
			this.requests[i] = (int)(Math.random()*(10-1+1)+1); //random number between 1 to 10
		this.mutex = mutex;
	}
	public void setHeap(Heap heap, Heap runningHeap) { this.heap = heap; this.runningHeap = runningHeap; }
	public int getThreadId() { return this.id; }
	public int getCouponNo() { return this.couponNo; }
	public int getRequestsLength() { return this.requests.length; }
	public void run()
	{
		try
		{
			for (int i = 0; i < requests.length; ++i)//for each request
			{
				this.mutex.acquire(); //block the thread (it will be released after heap extraction)
				if (this.requests[i] > Wedding.readProb) //0.7 probability for reading
					Wedding.readFromCard(this.id,this.couponNo);
				else //0.3 for writing
					Wedding.writeToCard(this.id,this.couponNo);
				Wedding.states[id] = false; //indicate completion of one request
				if (i != requests.length-1) heap.putBack(this); //if more requests present, put it back to heap
			}
		}
		catch(InterruptedException e){}
	}
}

class Heap
{
	private Friend[] friends;
	private int heapSize;
	Heap(int n)
	{
		this.heapSize = 0;
		this.friends = new Friend[n];
	}
	Heap(Friend[] friends)
	{
		this.friends = friends;
		this.heapSize = friends.length;
		build();
	}
	private int left(int i) {return 2*i+1;} //standard left child method
	private int right(int i) {return 2*i+2;} //standard right child method
	private int parent(int i) { return (i-1)/2; } //standard parent method
	private void build()
	{
		for (int i = (heapSize/2)-1; i >= 0; --i)
			heapify(i);
	}
	private void heapify(int root) //modified heapify method
	{
		int min = root; //store root in min
		if (left(root) < heapSize && friends[left(root)].getCouponNo() < friends[min].getCouponNo()) min = left(root);
		if (right(root) < heapSize && friends[right(root)].getCouponNo() < friends[min].getCouponNo()) min = right(root);
		
		if (min != root) //if min and root are not same, swap and heapify min
		{
			Friend temp = friends[min];
			friends[min] = friends[root];
			friends[root] = temp;
			heapify(min);
		}
	}
	public Friend extractRoot() //standard extract root method
	{
		Friend result = friends[0];
		friends[0] = friends[--heapSize];
		heapify(0);
		return result;
	}
	public void putBack(Friend friend) //standard insertion
	{
		friends[heapSize] = friend;
		int i = heapSize++;
		Friend temp = null;
		while (parent(i) >= 0 && friends[parent(i)].getCouponNo() > friends[i].getCouponNo())
		{
			temp = friends[i];
			friends[i] = friends[parent(i)];
			friends[parent(i)] = temp;
			i = parent(i);
		}
	}
	public void startProcesses() //start all the threads
	{
		for (int i = 0; i < heapSize; ++i)
			friends[i].start();
	}
	public boolean isEmpty()
	{
		return this.heapSize == 0;
	}
/*	public int getHeapSize()
	{
		return this.heapSize;
	}
	public int getRootCoupon()
	{
		return friends[0].getCouponNo();
	}*/
}

class Wedding
{
	static int readCount;
	static Semaphore serviceQueue;
	static Semaphore semCard, rMutex;
	static Semaphore[] blocks;
	static boolean[] states;
	static Friend[] friends;
	static int readProb;
	static Heap heap = null, runningHeap = null;
	static HashSet<Integer> couponNos;

	public static void readFromCard(int id, int couponNo)
	{
		try{
			setPriority(id,couponNo); //set thread priority
			

			serviceQueue.acquire(); //acquire semaphore for priority
			System.out.println("***>Coupon No: "+couponNo+" ***>TRYING to READ<***");

			rMutex.acquire(); //semaphore for reading
			
			++readCount;
			if (readCount == 1)
				semCard.acquire(); //semaphore for database
			
			serviceQueue.release();
			rMutex.release();

			System.out.println("* Coupon No: "+couponNo+" is READING...");
			Thread.sleep((int)(Math.random()*(5000-3000+1)+3000));
			System.out.println("* ...READING done by Coupon No: "+couponNo);

			rMutex.acquire();
		
			--readCount;
			if (readCount == 0)
				semCard.release();
		
			rMutex.release();
		}catch(InterruptedException ie) {}
	}

	public static void writeToCard(int id, int couponNo)
	{
		try{
			setPriority(id,couponNo); //set thread priority
			
			serviceQueue.acquire(); //acquire semaphore for priority
			System.out.println("--->Coupon No: "+couponNo+" --->TRYING to WRITE<---");

			semCard.acquire(); //semaphore for database
			serviceQueue.release();

			System.out.println("- Coupon No: "+couponNo+" is WRITING...");
			Thread.sleep((int)(Math.random()*(6000-4000+1)+4000));
			System.out.println("- ...WRITING done by Coupon No: "+couponNo);

			semCard.release();
		}catch(InterruptedException ie){}
	}

	public static void setPriority(int id, int couponNo)
	{
		friends[id].setPriority(couponNo/100);
	}

	public static void main(String[] args) throws IOException
	{
		int n = 0;
		readProb = 3; //change this number to modify reading and writing probability
		readCount = 0;
		serviceQueue = new Semaphore(1);
		semCard = new Semaphore(1);
		rMutex = new Semaphore(1);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		couponNos = new HashSet<Integer>();
		
		System.out.println("Enter the value of n-");
		n = Integer.parseInt(reader.readLine());
		blocks = new Semaphore[n]; //individual semaphores for blocking threads
		states = new boolean[n];
		for (int i = 0; i < n; ++i)
			blocks[i] = new Semaphore(0);

		friends = new Friend[n];
		for (int i = 0; i < n; ++i)
		{
			friends[i] = new Friend(i,blocks[i]); //create threads
			//friends[i].setPriority(friends[i].getCouponNo()/100);
		}
		heap = new Heap(friends); //create heap
		runningHeap = new Heap(n); //blank heap for running processes
		for (int i = 0; i < n; ++i)
		{
			friends[i].setHeap(heap,runningHeap); //set heap instance in threads
		}
		try{
			heap.startProcesses(); //start all processes
			while (!heap.isEmpty()) //till we have more processes
			{
				Friend temp = heap.extractRoot(); //extract the process with minimum coupon number
				states[temp.getThreadId()] = true; //set for execution
				blocks[temp.getThreadId()].release(); //release the lock
				Thread.sleep(2000); //wait for 3 seconds
			}
			Thread.sleep(5000);
			while (!heap.isEmpty())
			{
				Friend temp = heap.extractRoot(); //extract the process with minimum coupon number
				states[temp.getThreadId()] = true; //set for execution
				blocks[temp.getThreadId()].release(); //release the lock
				Thread.sleep(2000); //wait for 3 seconds
			}
		}catch(InterruptedException e){}
	}
}