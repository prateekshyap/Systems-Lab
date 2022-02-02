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


/*class Friend extends Thread
{
	int id;
	int[] requests;
	Friend(int id)
	{
		this.id = id;
		this.requests = new int[(int)(Math.random()*(2-1+1)+1)];
		for (int i = 0; i < this.requests.length; ++i)
			this.requests[i] = (int)(Math.random()*(10-1+1)+1);
	}
	public void run()
	{
		try{
		for (int i = 0; i < this.requests.length; ++i)
		{
			Wedding.semaphore.acquire();
			if (this.requests[i] > 3)
			{
				System.out.println(id+" reading");
				Thread.sleep(1000);
				System.out.println(id+" reading completed");
			}
			else if (this.requests[i] <= 3)
			{
				System.out.println(id+" writing");
				Thread.sleep(2000);
				System.out.println(id+" writing completed");
			}
			Wedding.semaphore.release();
		}
		}catch(InterruptedException ie)
		{}
	}
}

class Wedding
{
	static PrioritySemaphore semaphore;
	public static void main(String[] args)
	{
		semaphore = new PrioritySemaphore(1);
		Friend[] friends = new Friend[10];
		int[] priorities = {4,7,2,1,6,5,3,8,9,10};
		for (int i = 0; i < 10; ++i)
		{
			friends[i] = new Friend(i);
			friends[i].setPriority(priorities[i]);
		}
		for (int i = 0; i < 10; ++i)
		{
			friends[i].start();
		}
	}
}*/