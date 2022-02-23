/*
 * CS558 System Lab, IITG | Assignment 02
 * Concept: Partition Allocation Methods in Memory Management
 * Roll Num: 214101037 (Prateekshya Priyadarshini), 214101058 (Vijay Purohit)
 * Detailed Question is available in the pdf.
 */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;


/**
 * Class Block representing Blocks consisting of rooms for each manager
 */
class Block
{
    private int totalRooms, totalAllocations;
    private int[] roomTypes, remainingRooms, satisfied;
    private static int[] prices;
    private long revenueGenerated;
    private boolean devMode;

    Block(int types, String[] counts, boolean mode)
    {
        this.devMode = mode;
        this.roomTypes = new int[types];
        this.remainingRooms = new int[types];
        this.satisfied = new int[types];

        for (int i = 0; i < types; ++i)
        {
            roomTypes[i] = remainingRooms[i] = Integer.parseInt(counts[i]);
            totalRooms += roomTypes[i];
        }

        prices = new int[types];
        prices[0] = 5000;
        prices[1] = 9000;
        prices[2] = 12500;

        this.revenueGenerated = 0;
        this.totalAllocations = 0;
    }

    public void printData()
    {
        for (int i = 0; i < satisfied.length; ++i)
            System.out.print(satisfied[i]+" ");
        System.out.println();
        System.out.println(revenueGenerated);
    }

    public int getRemRoomsByIndex(int index) { return remainingRooms[index]; }

    public void setRemRoomsByIndex(int index, int data) { this.remainingRooms[index] = data; }

    public int[] getPrices() { return prices; }

    public int getSatReqByIndex(int index) { return satisfied[index]; }

    public int[] getAllocations() { return this.satisfied; }

    public void setSatReqByIndex(int index, int data)
    {
        this.satisfied[index] = data;
        this.revenueGenerated += prices[index];
        ++this.totalAllocations;
    }
    public long getRevenueGenerated() { return this.revenueGenerated; }
    public int getTotalAllocations() { return this.totalAllocations; }
}


class Taj
{
    public static void main(String[] args)throws IOException
    {
        boolean devMode = false;
        if (args.length > 0) devMode = Boolean.parseBoolean(args[0]);
        int choice = 0, rem = 0, bookingRequests = 0, noOfBlocks = 3, roomTypes = 3, maxRooms = 10, minRooms = 1, maxRequests = 25, minRequests = 10, j = 0;
        int[] satisfiedReq = null, requestEntries = null;
        String inputFileName = "input.txt", outputFileName = "output.txt", temp = "";
        String[] managers = {"Aman", "Raj", "Alok"};

        BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("========================================================");
        System.out.println("                    WELCOME TO Hotel TAJ");
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

            //number of rooms for single, double and triple occupancies
            inputFileGenerator.write((int)(Math.random()*(maxRooms-minRooms+1)+minRooms)+" "+(int)(Math.random()*(maxRooms-minRooms+1)+minRooms)+" "+(int)(Math.random()*(maxRooms-minRooms+1)+minRooms));
            inputFileGenerator.newLine();

            //number of booking requests
            int noOfRequests = (int)(Math.random()*(maxRequests-minRequests+1)+minRequests);
            inputFileGenerator.write(Integer.toString(noOfRequests));
            inputFileGenerator.newLine();

            //request entries
            int entry = -1;
            for (int i = 0; i < noOfRequests; ++i)
            {
                entry = (int)(Math.random()*(3-1+1)+1);
                inputFileGenerator.write(entry == 1 ? "S" : (entry == 2 ? "D" : "T"));
                if (i != noOfRequests-1) inputFileGenerator.write(" ");
            }

            inputFileGenerator.close();
        }

        BufferedReader fileReader = new BufferedReader(new FileReader(new File(inputFileName)));
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(outputFileName)));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Read input and store
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Block[] blocks = new Block[noOfBlocks];

        //number of single, double and triple occupancy rooms
        String[] roomCount = fileReader.readLine().trim().split(" +");
        for (int i = 0; i < noOfBlocks; ++i)
            blocks[i] = new Block(roomTypes,roomCount,devMode);

        //number of requests
        bookingRequests = Integer.parseInt(fileReader.readLine().trim());

        //request entries
        requestEntries = new int[bookingRequests];
        String[] tokens = fileReader.readLine().trim().split(" +");
        for (int i = 0; i < bookingRequests; ++i)
            requestEntries[i] = tokens[i].equals("S") ? 0 :
                                    tokens[i].equals("D") ? 1 :
                                        tokens[i].equals("T") ? 2 : -1;

        int[] order = new int[roomTypes];
        order[0] = 2; //triple room
        order[1] = 0; //single room
        order[2] = 1; //double room

        fileReader.close();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Aman's Allocation for block A (First Fit)
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (devMode)
        {
            System.out.println();
            System.out.println("Aman (Block A)");
        }
        for (Integer request : requestEntries)
        {
            for (j = 0; j < roomTypes; ++j)
            {
                rem = blocks[0].getRemRoomsByIndex(order[j]); //get the first type of rooms available
                if (rem > 0 && request <= order[j]) //if rooms are available and type of request can be satisfied with jth type of room
                {
                    blocks[0].setRemRoomsByIndex(order[j],rem-1); //reduce the count by 1
                    blocks[0].setSatReqByIndex(request,blocks[0].getSatReqByIndex(request)+1); //increase request satisfied count
                    break; //to stop after one allocation
                }
            }
            if (devMode)
            {
                System.out.println("For request: "+(request+1));
                blocks[0].printData();
            }
        }
        if (devMode)
        {
            System.out.println();
            blocks[0].printData();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Raj's Allocation for block B
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (devMode)
        {
            System.out.println();
            System.out.println("Raj (Block B)");
        }
        for (Integer request : requestEntries)
        {
            for (j = roomTypes-1; j >= 0; --j)
            {
                rem = blocks[1].getRemRoomsByIndex(j); //get the worst fit rooms available
                if (rem > 0 && request <= j) //if rooms are available and type of request can be satisfied with jth type of room
                {
                    blocks[1].setRemRoomsByIndex(j,rem-1); //reduce the count by 1
                    blocks[1].setSatReqByIndex(request,blocks[1].getSatReqByIndex(request)+1); //increase request satisfied count
                    break; //to stop after one allocation
                }
            }
            if (devMode)
            {
                System.out.println("For request: "+(request+1));
                blocks[1].printData();
            }
        }
        if (devMode)
        {
            System.out.println();
            blocks[1].printData();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Alok's Allocation for block C
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (devMode)
        {
            System.out.println();
            System.out.println("Alok (Block C)");
        }
        for (Integer request : requestEntries)
        {
            for (j = request; j < roomTypes; ++j)
            {
                rem = blocks[2].getRemRoomsByIndex(j); //get the worst fit rooms available
                if (rem > 0) //if rooms are available and type of request can be satisfied with jth type of room
                {
                    blocks[2].setRemRoomsByIndex(j,rem-1); //reduce the count by 1
                    blocks[2].setSatReqByIndex(request,blocks[2].getSatReqByIndex(request)+1); //increase request satisfied count
                    break; //to stop after one allocation
                }
            }
            if (devMode)
            {
                System.out.println("For request: "+(request+1));
                blocks[2].printData();
            }
        }
        if (devMode)
        {
            System.out.println();
            blocks[2].printData();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Optimal Solution
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int[] optimalAllocation = new int[roomTypes]; //stores maximum possible requests those can be satisfied
        int[] requestsCount = new int[roomTypes]; //stores the total count of requests for each room type
        int[] rejectedRequests = new int[noOfBlocks]; //stores total number of requests rejected for each block
        long[] moneyWasted = new long[noOfBlocks]; //stores total money wasted for each block
        int[] allocations = null;
        int[] prices = blocks[0].getPrices();
        int optimalRevenue = 0, totalAllocations = 0, largerAvail = 0;

        for (int i = 0; i < bookingRequests; ++i)
            ++requestsCount[requestEntries[i]];
        for (int i = roomTypes-1; i >= 0; --i)
        { //double req = 10, rooms = 4, alloc = 4 ( 8 , 10) (unalloc = 6, largeravail = 4, newAlloc = 4), (unalloc = 6, largeravail = 8, newAlloc = 6)
            optimalAllocation[i] = Math.min(requestsCount[i],Integer.parseInt(roomCount[i]));
            if (Integer.parseInt(roomCount[i]) > optimalAllocation[i])
                largerAvail += Integer.parseInt(roomCount[i])-optimalAllocation[i];
            if (requestsCount[i] > optimalAllocation[i])
            {
                int unalloc = requestsCount[i]-optimalAllocation[i];
                int newAlloc = Math.min(unalloc,largerAvail);
                largerAvail -= newAlloc;
                optimalAllocation[i] += newAlloc;
            }
            optimalRevenue += optimalAllocation[i]*prices[i];
            totalAllocations += optimalAllocation[i];
        }
        for (int i = 0; i < noOfBlocks; ++i)
            moneyWasted[i] = optimalRevenue - blocks[i].getRevenueGenerated();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  Print
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        StringBuffer fileString = new StringBuffer("");
        int bestManager = 0;
        for (int i = 1; i < noOfBlocks; ++i)
            if (blocks[i].getRevenueGenerated() > blocks[bestManager].getRevenueGenerated())
                bestManager = i;

        System.out.println();
        System.out.println("---------------------------------------------------------------------------------------------------------");
        fileString.append("---------------------------------------------------------------------------------------------------------\n");
        System.out.println(managers[bestManager]+" is the best manager.");
        fileString.append(managers[bestManager]+" is the best manager.\n");

        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("|       Manager Name      |    Revenue Generated    |       Money Wasted      |     No. of Requests     |");
        System.out.println("|                         |           (Rs)          |   because of Nature of  |   rejected because of   |");
        System.out.println("|                         |                         |       Manager (Rs)      |    wrong allotment of   |");
        System.out.println("|                         |                         |                         |         rooms           |");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        fileString.append("---------------------------------------------------------------------------------------------------------\n");
        fileString.append("|       Manager Name      |    Revenue Generated    |       Money Wasted      |     No. of Requests     |\n");
        fileString.append("|                         |           (Rs)          |   because of Nature of  |   rejected because of   |\n");
        fileString.append("|                         |                         |       Manager (Rs)      |    wrong allotment of   |\n");
        fileString.append("|                         |                         |                         |         rooms           |\n");
        fileString.append("---------------------------------------------------------------------------------------------------------\n");

        for (int i = 0; i < noOfBlocks; ++i)
        {
            System.out.print("|");
            fileString.append("|");

            System.out.print(" " + managers[i]);
            fileString.append(" " + managers[i]);
            for (j = 0; j < 24 - managers[i].length(); ++j)
            {
                System.out.print(" ");
                fileString.append(" ");
            }

            System.out.print("|");
            fileString.append("|");

            String printStr = Long.toString(blocks[i].getRevenueGenerated());
            printStr += " (";
            for (j = 0; j < roomTypes; ++j)
                printStr += (blocks[i].getSatReqByIndex(j)+" ");
            printStr += ")";
            System.out.print(" " + printStr);
            fileString.append(" " + printStr);
            for (j = 0; j < 24 - printStr.length(); ++j)
            {
                System.out.print(" ");
                fileString.append(" ");
            }

            System.out.print("|");
            fileString.append("|");

            printStr = Long.toString(moneyWasted[i]);
            System.out.print(" " + printStr);
            fileString.append(" " + printStr);
            for (j = 0; j < 24 - printStr.length(); ++j)
            {
                System.out.print(" ");
                fileString.append(" ");
            }

            System.out.print("|");
            fileString.append("|");

            allocations = blocks[i].getAllocations();
            for (j = 0; j < roomTypes; ++j)
                rejectedRequests[i] += Math.max(0,optimalAllocation[j]-allocations[j]);
            printStr = Integer.toString(rejectedRequests[i]);
            printStr += " (";
            for (j = 0; j < roomTypes; ++j)
                printStr += (Math.max(0,optimalAllocation[j]-allocations[j])+" ");
            printStr += ")";
            System.out.print(" " + printStr);
            fileString.append(" " + printStr);
            for (j = 0; j < 24 - printStr.length(); ++j)
            {
                System.out.print(" ");
                fileString.append(" ");
            }

            System.out.println("|");
            fileString.append("|\n");
        }

        //print the optimal allocation details
        System.out.print("|");
        fileString.append("|");

        for (j = 0; j < 25; ++j)
        {
            System.out.print(" ");
            fileString.append(" ");
        }

        System.out.print("|");
        fileString.append("|");

        String printStr = Long.toString(optimalRevenue);
        printStr += " (";
        for (j = 0; j < roomTypes; ++j)
            printStr += (optimalAllocation[j]+" ");
        printStr += ")";
        System.out.print(" " + printStr);
        fileString.append(" " + printStr);
        for (j = 0; j < 24 - printStr.length(); ++j)
        {
            System.out.print(" ");
            fileString.append(" ");
        }

        System.out.print("|");
        fileString.append("|");

        printStr = "0";
        System.out.print(" " + printStr);
        fileString.append(" " + printStr);
        for (j = 0; j < 24 - printStr.length(); ++j)
        {
            System.out.print(" ");
            fileString.append(" ");
        }

        System.out.print("|");
        fileString.append("|");

        printStr = "0 (0 0 0 )";
        System.out.print(" " + printStr);
        fileString.append(" " + printStr);
        for (j = 0; j < 24 - printStr.length(); ++j)
        {
            System.out.print(" ");
            fileString.append(" ");
        }

        System.out.println("|");
        fileString.append("|\n");

        System.out.println("---------------------------------------------------------------------------------------------------------");
        fileString.append("---------------------------------------------------------------------------------------------------------\n");

        fileWriter.write(fileString.toString());
        fileWriter.close();
    }
}
