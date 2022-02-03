import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.concurrent.Semaphore;

import EDU.oswego.cs.dl.util.concurrent.PrioritySemaphore;

class Friend extends Thread
{
	private int id;
	private int couponNo;
	private int[] requests;
	private Semaphore mutex;
	private int index = 0;
	private Heap heap;
	Friend(int id, Semaphore mutex)
	{
		this.id = id;
		this.couponNo = (int)(Math.random()*(999-100+1)+100);
		this.requests = new int[(int)(Math.random()*(5-1+1)+1)];
		for (int i = 0; i < this.requests.length; ++i)
			this.requests[i] = (int)(Math.random()*(10-1+1)+1);
		this.mutex = mutex;
	}
	public void setHeap(Heap heap) { this.heap = heap; }
	public int getThreadId() { return this.id; }
	public int getCouponNo() { return this.couponNo; }
	public int getIndex() { return this.index; }
	public int getRequestsLength() { return this.requests.length; }
	public void run()
	{
		try
		{
			while (this.index < this.requests.length)
			{
				this.mutex.acquire();
				//this.setPriority(couponNo/100);
				if (this.requests[this.index] > Wedding.readProb)
					Wedding.readFromCard(this.id,this.couponNo);
				else
					Wedding.writeToCard(this.id,this.couponNo);
				Wedding.states[id] = false;
				++this.index;
				if (this.index < this.requests.length) heap.putBack(this);
			}
		}
		catch(InterruptedException e){}
	}
}

class Heap
{
	private Friend[] friends;
	private int heapSize;
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
	public Friend extractRoot()
	{
		Friend result = friends[0];
		friends[0] = friends[--heapSize];
		heapify(0);
		return result;
	}
	public void putBack(Friend friend)
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
	public void startProcesses()
	{
		for (int i = 0; i < heapSize; ++i)
			friends[i].start();
	}
	public void setPriority(int id, int couponNo)
	{
		friends[id].setPriority(couponNo/100);
	}
}

class Wedding
{
	static int readCount;
	static PrioritySemaphore serviceQueue;
	static Semaphore semCard, rMutex;
	static Semaphore[] blocks;
	static boolean[] states;
	static Friend[] friends;
	static int readProb;
	static Heap heap = null;

	public static void readFromCard(int id, int couponNo)
	{
		try{
			System.out.println("***>Coupon No: "+couponNo+" ***>TRYING to READ<***");

			setPriority(id,couponNo);
			
			serviceQueue.acquire();

			rMutex.acquire();
			
			++readCount;
			if (readCount == 1)
				semCard.acquire();
			
			serviceQueue.release();
			rMutex.release();

			System.out.println("* Coupon No: "+couponNo+" is READING...");
			Thread.sleep((int)(Math.random()*(6000-4000+1)+4000));
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
			System.out.println("--->Coupon No: "+couponNo+" --->TRYING to WRITE<---");

			setPriority(id,couponNo);
			
			serviceQueue.acquire();

			semCard.acquire();
			serviceQueue.release();

			System.out.println("- Coupon No: "+couponNo+" is WRITING...");
			Thread.sleep((int)(Math.random()*(7000-5000+1)+5000));
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
		readProb = 3;
		readCount = 0;
		serviceQueue = new PrioritySemaphore(1);
		semCard = new Semaphore(1);
		rMutex = new Semaphore(1);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter the value of n-");
		n = Integer.parseInt(reader.readLine());
		blocks = new Semaphore[n];
		states = new boolean[n];
		for (int i = 0; i < n; ++i)
			blocks[i] = new Semaphore(0);

		friends = new Friend[n];
		for (int i = 0; i < n; ++i)
		{
			friends[i] = new Friend(i,blocks[i]);
			//friends[i].setPriority(friends[i].getCouponNo()/100);
		}
		heap = new Heap(friends);
		for (int i = 0; i < n; ++i)
		{
			friends[i].setHeap(heap);
		}
		try{
			heap.startProcesses();
			for (int i = 0; i < n; ++i)
			{
				Friend temp = heap.extractRoot();
				states[temp.getThreadId()] = true;
				blocks[temp.getThreadId()].release();
				Thread.sleep(3000);
			}
		}catch(InterruptedException e){}
	}
}


/*
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.concurrent.Semaphore;

import EDU.oswego.cs.dl.util.concurrent.PrioritySemaphore;

class Friend extends Thread
{
	private int couponNo;
	private int[] requests;
	Friend()
	{
		this.couponNo = (int)(Math.random()*(999-100+1)+100);
		this.requests = new int[(int)(Math.random()*(3-1+1)+1)];
		for (int i = 0; i < this.requests.length; ++i)
			this.requests[i] = (int)(Math.random()*(10-1+1)+1);
	}
	public int getCouponNo() { return this.couponNo; }
	public void run()
	{
			for (int i = 0; i < requests.length; ++i)
			{
				if (requests[i] > Wedding.readProb)
					Wedding.readFromCard(couponNo);
				else
					Wedding.writeToCard(couponNo);
			}
	}
}

class Wedding
{
	static int readCount;
	static PrioritySemaphore serviceQueue;
	static Semaphore semCard, rMutex;
	static int readProb;

	public static void readFromCard(int couponNo)
	{
		try{
		System.out.println("***>Coupon No: "+couponNo+" ***>TRYING to READ<***");
			serviceQueue.acquire();
		rMutex.acquire();
				++readCount;
				if (readCount == 1)
					semCard.acquire();
			serviceQueue.release();
		rMutex.release();

		System.out.println("* Coupon No: "+couponNo+" is READING...");
		Thread.sleep((int)(Math.random()*(3000-1000+1)+1000));
		System.out.println("* ...READING done by Coupon No: "+couponNo);

		rMutex.acquire();
			--readCount;
			if (readCount == 0)
				semCard.release();
		rMutex.release();
	}catch(InterruptedException ie) {}
	}

	public static void writeToCard(int couponNo)
	{
		try{
		System.out.println("--->Coupon No: "+couponNo+" --->TRYING to WRITE<---");
			serviceQueue.acquire();
		semCard.acquire();
			serviceQueue.release();

		System.out.println("- Coupon No: "+couponNo+" is WRITING...");
		Thread.sleep((int)(Math.random()*(4000-2000+1)+2000));
		System.out.println("- ...WRITING done by Coupon No: "+couponNo);

		semCard.release();
	}catch(InterruptedException ie){}
	}

	public static void main(String[] args) throws IOException
	{
		int n = 0;
		readProb = 3;
		readCount = 0;
		serviceQueue = new PrioritySemaphore(1);
		semCard = new Semaphore(1);
		rMutex = new Semaphore(1);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter the value of n-");
		n = Integer.parseInt(reader.readLine());

		Friend[] friends = new Friend[n];
		for (int i = 0; i < n; ++i)
		{
			friends[i] = new Friend();
			friends[i].setPriority(friends[i].getCouponNo()/100);
		}
		for (int i = 0; i < n; ++i)
		{
			friends[i].start();
		}
	}
}
*/