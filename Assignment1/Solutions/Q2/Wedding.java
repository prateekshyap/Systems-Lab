//import concurrent.PrioritySemaphore;
import EDU.oswego.cs.dl.util.concurrent.PrioritySemaphore;

class Friend extends Thread
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
}