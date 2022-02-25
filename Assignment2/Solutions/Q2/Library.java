import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

class Book
{
    private String subjectId;
    private int trayIndex;
    private int[] params = new int[4]; //id, initialTimeStamp, frequency, latestTimeStamp;
    Book(int index, String subjectId)
    {
        this.params[0] = index; //id
        this.params[1] = -1; //initialTimeStamp
        this.params[2] = 0; //frequency (also denotes the book id)
        this.params[3] = -1; //latestTimeStamp
        this.subjectId = subjectId; //subject id
        this.trayIndex = -1; //stores the current tray index
    }

    public int getParamByIndex(int index) { return params[index]; }
    public void setParamByIndex(int index, int data) { this.params[index] = data; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public int getTrayIndex() { return trayIndex; }
    public void setTrayIndex(int trayIndex) { this.trayIndex = trayIndex; }
}

class Tray
{
    private int capacity, size;
    private Book[] booksTray; //tray
    private HashSet<String> isBookInTray; //used during searching of a book in tray
    private HashMap<String,Book> depo; //depo
    private int heapifyIndex;
    private int timer;
    private int pageFaults;
    Tray(int order, int noOfSubjects, int capacity, String[] requests)
    {
        this.capacity = capacity;
        this.size = 0;
        this.heapifyIndex = order;
        this.timer = 0;
        this.pageFaults = 0;
        this.booksTray = new Book[capacity];
        this.isBookInTray = new HashSet<String>();
        this.depo = new HashMap<String,Book>();

        int index = 1;
        for (String request : requests)
            if ((request.charAt(0) == 'B' && heapifyIndex == 1) || (request.charAt(0) == 'M' && heapifyIndex == 2) || (request.charAt(0) == 'P' && heapifyIndex == 3))
                depo.put(request,new Book(index++,request));
    }
    private int left(int i) { return (2*i)+1; } //standard left child method
    private int right(int i) { return (2*i)+2; } //standard right child method
    private int parent(int i) { return (i-1)/2; } //standard parent method
    private void heapify(int root)
    {
        int min = root;
        if (left(root) < size)
        {
            if (booksTray[left(root)].getParamByIndex(heapifyIndex) < booksTray[min].getParamByIndex(heapifyIndex))
                min = left(root);
            else if (booksTray[left(root)].getParamByIndex(heapifyIndex) == booksTray[min].getParamByIndex(heapifyIndex) && booksTray[left(root)].getParamByIndex(1) < booksTray[min].getParamByIndex(1))
                min = left(root);
        }
        if (right(root) < size)
        {
            if (booksTray[right(root)].getParamByIndex(heapifyIndex) < booksTray[min].getParamByIndex(heapifyIndex))
                min = right(root);
            else if (booksTray[right(root)].getParamByIndex(heapifyIndex) == booksTray[min].getParamByIndex(heapifyIndex) && booksTray[right(root)].getParamByIndex(1) < booksTray[min].getParamByIndex(1))
                min = right(root);
        }
        if (min != root)
        {
            Book temp = booksTray[min];
            booksTray[min] = booksTray[root];
            booksTray[root] = temp;
            booksTray[min].setTrayIndex(min);
            booksTray[root].setTrayIndex(root);
            heapify(min);
        }
    }
    public void issue(String subjectId)
    {
        int i = -1;
        if (isBookInTray.contains(subjectId))
        {
            int trayIndex = depo.get(subjectId).getTrayIndex();
            booksTray[trayIndex].setParamByIndex(2,booksTray[trayIndex].getParamByIndex(2)+1); //frequency
            booksTray[trayIndex].setParamByIndex(3,timer++); //latest time stamp
            heapify(trayIndex);
        }
        else
        {
            ++pageFaults;
            if (size == capacity) evict();
            booksTray[size] = depo.get(subjectId);
            booksTray[size].setTrayIndex(size);
            isBookInTray.add(subjectId);
            booksTray[size].setParamByIndex(1,timer); //initial time stamp
            booksTray[size].setParamByIndex(2,booksTray[size].getParamByIndex(2)+1); //frequency
            booksTray[size].setParamByIndex(3,timer++); //latest time stamp
            i = size++;
            Book book = null;
            while (parent(i) >= 0 && ((booksTray[parent(i)].getParamByIndex(heapifyIndex) > booksTray[i].getParamByIndex(heapifyIndex)) || (booksTray[parent(i)].getParamByIndex(heapifyIndex) == booksTray[i].getParamByIndex(heapifyIndex) && booksTray[parent(i)].getParamByIndex(1) > booksTray[i].getParamByIndex(1))))
            {
                book = booksTray[i];
                booksTray[i] = booksTray[parent(i)];
                booksTray[parent(i)] = book;
                booksTray[i].setTrayIndex(i);
                booksTray[parent(i)].setTrayIndex(parent(i));
                i = parent(i);
            }
        }
        if (Library.devMode)
        {
            System.out.println((heapifyIndex == 1 ? "FIFO" : (heapifyIndex == 2 ? "LFU" : (heapifyIndex == 3 ? "LRU" : "null"))));
            printTray(new StringBuffer());
        }
    }
    public Book evict()
    {
        Book result = booksTray[0];
        booksTray[0] = booksTray[--size];
        booksTray[0].setTrayIndex(0);
        heapify(0);
        isBookInTray.remove(new String(result.getSubjectId()));
        return result;
    }
    public void printTray(StringBuffer buffer)
    {
        int j, spaces;
        String printString;

        System.out.print("-");
        buffer.append("-");
        for (int i = 0; i < size; ++i)
        {
            System.out.print("----------------");
            buffer.append("----------------");
        }

        System.out.println();
        buffer.append("\n");

        System.out.print("|");
        buffer.append("|");
        for (int i = 0; i < size; ++i)
        {
            printString = "SID = "+booksTray[i].getSubjectId();
            spaces = 15-printString.length();
            j = 0;
            for (; j < spaces/2; ++j)
            {
                System.out.print(" ");
                buffer.append(" ");
            }
            System.out.print(printString);
            buffer.append(printString);
            for (; j < spaces; ++j)
            {
                System.out.print(" ");
                buffer.append(" ");
            }
            System.out.print("|");
            buffer.append("|");
        }

        System.out.println();
        buffer.append("\n");

        System.out.print("|");
        buffer.append("|");
        for (int i = 0; i < size; ++i)
        {
            printString = "BID = ";
            printString += booksTray[i].getParamByIndex(2);
            spaces = 15-printString.length();
            j = 0;
            for (; j < spaces/2; ++j)
            {
                System.out.print(" ");
                buffer.append(" ");
            }
            System.out.print(printString);
            buffer.append(printString);
            for (; j < spaces; ++j)
            {
                System.out.print(" ");
                buffer.append(" ");
            }
            System.out.print("|");
            buffer.append("|");
        }

        System.out.println();
        buffer.append("\n");

        System.out.print("-");
        buffer.append("-");
        for (int i = 0; i < size; ++i)
        {
            System.out.print("----------------");
            buffer.append("----------------");
        }

        System.out.println();
        buffer.append("\n");
        System.out.println("Number of times the librarian has to go to search the entire book depo = "+this.pageFaults);
        buffer.append("Number of times the librarian has to go to search the entire book depo = "+this.pageFaults+"\n");
        int maxFreq = -1;
        Book maxFreqBook = null, book;
        Iterator iterator = depo.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry bookEntry = (Map.Entry)iterator.next();
            book = (Book)bookEntry.getValue();
            if (book.getParamByIndex(2) > maxFreq)
            {
                maxFreq = book.getParamByIndex(2);
                maxFreqBook = book;
            }
        }
        System.out.println("ID of the subject for which the maximum number of books are issued = "+maxFreqBook.getSubjectId());
        System.out.println();
        System.out.println();
        buffer.append("ID of the subject for which the maximum number of books are issued = "+maxFreqBook.getSubjectId()+"\n");
        buffer.append("\n");
        buffer.append("\n");
    }
}

class Library
{
    public static boolean devMode = false;
    private static int noOfCourses = 3;
    private static String[] courses = {"B.Tech","M.Tech","Ph.D"}, requestEntries;
    private static int[] noOfSubjects, trayCapacity;
    private static Tray[] trays;
    public static void main(String[] args)throws IOException
    {
        if (args.length > 0) devMode = Boolean.parseBoolean(args[0]);
        int choice, maxIssue = 50, minIssue = 30, maxSubjects = 20, minSubjects = 5, maxTrayCap = 10, minTrayCap = 3, j;
        String inputFileName = "input.txt", outputFileName = "output.txt", temp;

        BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("========================================================");
        System.out.println("                   WELCOME TO LIBRARY");
        System.out.println("========================================================");

        System.out.println("Enter 0 for random input/ 1 for manual input-");
        choice = Integer.parseInt(cmdReader.readLine());

        System.out.println("Enter input file name (Press Enter for default file name input.txt)-");
        temp = cmdReader.readLine();
        if (temp.length() != 0) inputFileName = temp;
        System.out.println("Enter output file name (Press Enter for default file name output.txt)-");
        temp = cmdReader.readLine();
        if (temp.length() != 0) outputFileName = temp;

        //random input generation
        if (choice == 0)
        {
            BufferedWriter inputFileGenerator = new BufferedWriter(new FileWriter(new File(inputFileName)));

            //Number of Subjects for each Course
            int[] n = new int[noOfCourses];
            for (int i = 0; i < noOfCourses; ++i)
                n[i] = (int)(Math.random()*(maxSubjects-minSubjects+1)+minSubjects);
            for (int i = 0; i < noOfCourses; ++i)
                inputFileGenerator.write(n[i]+" ");
            inputFileGenerator.newLine();

            //Tray Capacity
            int[] t = new int[noOfCourses];
            for (int i = 0; i < noOfCourses; ++i)
                t[i] = (int)(Math.random()*(Math.min(maxTrayCap,n[i])-minTrayCap+1)+minTrayCap);
            for (int i = 0; i < noOfCourses; ++i)
                inputFileGenerator.write(t[i]+" ");
            inputFileGenerator.newLine();

            //Subject Ids being issued
            int noOfIssues = (int)(Math.random()*(maxIssue-minIssue+1)+minIssue);
            String subjectId;
            for (int i = 0; i < noOfIssues; ++i)
            {
                int courseId = (int)(Math.random()*(noOfCourses-1+1)+1);
                subjectId = Character.toString(courses[courseId-1].charAt(0));
                subjectId += Integer.toString((int)(Math.random()*(n[courseId-1]-1+1)+1));
                inputFileGenerator.write(subjectId+" ");
            }
            inputFileGenerator.newLine();

            inputFileGenerator.close();
        }

        BufferedReader fileReader = new BufferedReader(new FileReader(new File(inputFileName)));
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(outputFileName)));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Read input and store
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        noOfSubjects = new int[noOfCourses];
        trayCapacity = new int[noOfCourses];

        //Number of Subjects for each course
        String[] tokens = fileReader.readLine().trim().split(" +");
        for (int i = 0; i < noOfCourses; ++i)
            noOfSubjects[i] = Integer.parseInt(tokens[i]);

        //Tray capacity
        tokens = fileReader.readLine().trim().split(" +");
        for (int i = 0; i < noOfCourses; ++i)
            trayCapacity[i] = Integer.parseInt(tokens[i]);

        //Subject Ids being issued
        requestEntries = fileReader.readLine().trim().split(" +");

        fileReader.close();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Simulations
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        trays = new Tray[noOfCourses];
        for (int i = 0; i < noOfCourses; ++i)
            trays[i] = new Tray(i+1,noOfSubjects[i],trayCapacity[i],requestEntries);

        for (String request : requestEntries)
        {
            if (request.charAt(0) == 'B') trays[0].issue(request);
            else if (request.charAt(0) == 'M') trays[1].issue(request);
            else if (request.charAt(0) == 'P') trays[2].issue(request);
        }

        StringBuffer printString = new StringBuffer("");
        for (int i = 0; i < noOfCourses; ++i)
        {
            System.out.println("====================================================================================================================================");
            printString.append("====================================================================================================================================\n");
            System.out.println("Course = "+courses[i]);
            printString.append("Course = "+courses[i]+"\n");
            trays[i].printTray(printString);
        }

        fileWriter.write(printString.toString());
        fileWriter.close();
    }
}