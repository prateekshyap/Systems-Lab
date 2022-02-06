class three_threads {
	public static void main(String args[]) {
		mythread1 obj1 = new mythread1();
		mythread2 obj2 = new mythread2();
		mythread3 obj3 = new mythread3();
		obj1.start();
		obj2.start();
		obj3.start();
	}
}

class mythread1 extends Thread {
	public void run() {
		for (int i = 1; i <= 20; i++) {
			System.out.println("1st THread " + i);
		}
	}
}

class mythread2 extends Thread {
	public void run() {
		for (int i = 1; i <= 20; i++) {
			System.out.println("2nd THread " + i);
		}
	}
}

class mythread3 extends Thread {
	public void run() {
		for (int i = 1; i <= 20; i++) {
			System.out.println("3rd THread " + i);
		}
	}
}