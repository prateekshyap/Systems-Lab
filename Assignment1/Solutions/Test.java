import java.util.concurrent.Semaphore;

class Process extends Thread
{
	int id;
	int couponNo;
	Semaphore mutex;
	Process(int id, int couponNo, Semaphore mutex)
	{
		this.id = id;
		this.couponNo = couponNo;
		this.mutex = mutex;
	}
	public void run()
	{
		try{
		mutex.acquire();
		System.out.println(couponNo+" is running");
		}catch(Exception e){}
	}
}

class Heap
{
	private Process[] processes;
	private int heapSize = 20;
	Heap(Process[] processes)
	{
		this.processes = processes;
		build();
	}
	private int left(int i) {return 2*i+1;}
	private int right(int i) {return 2*i+2;}
	private void build()
	{
		for (int i = (heapSize/2)-1; i >= 0; --i)
			heapify(i);
	}
	private void heapify(int root) //modified heapify method
	{
		int min = root; //store root in min
		if (left(root) < heapSize && processes[left(root)].couponNo < processes[min].couponNo) min = left(root);
		if (right(root) < heapSize && processes[right(root)].couponNo < processes[min].couponNo) min = right(root);
		
		if (min != root) //if min and root are not same, swap and heapify min
		{
			Process temp = processes[min];
			processes[min] = processes[root];
			processes[root] = temp;
			heapify(min);
		}
	}
	public Process extractRoot()
	{
		Process result = processes[0];
		processes[0] = processes[--heapSize];
		heapify(0);
		return result;
	}
	public void startProcesses()
	{
		for (int i = 0; i < heapSize; ++i)
			processes[i].start();
	}
}

class Test
{
	public static Semaphore[] blocks;
	public static boolean[] wasNotified;
	public static void main(String[] ags)
	{
		blocks = new Semaphore[20];
		for (int i = 0; i < 20; ++i)
			blocks[i] = new Semaphore(0);
		Process[] processes = new Process[20];
		wasNotified = new boolean[20];	

		for (int i = 0; i < processes.length; ++i)
			processes[i] = new Process(i,(int)(Math.random()*(999-100+1)+100),blocks[i]);

		Heap heap = new Heap(processes);
		try{
		heap.startProcesses();
		for (int i = 0; i < 20; ++i)
		{
			Process temp = heap.extractRoot();
			blocks[temp.id].release();
			Thread.sleep(50*20);
		}
		}catch(Exception e){}
	}
}