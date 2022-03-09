/*
 * CS558 System Lab, IITG | Assignment 02
 * Concept: File System
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

import java.util.HashSet;

class FileSystem
{
    public static boolean devMode = false;
    private static int inodeCounter = 0;
    private static HashSet<Integer> diskBlocks = new HashSet<Integer>();
    public static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles())
        {
            if (subfile.isDirectory()) deleteDirectory(subfile);
            subfile.delete();
        }
    }
    public static void main(String[] args)throws IOException
    {
        if (args.length > 0) devMode = Boolean.parseBoolean(args[0]);
        boolean isPresent = false, isDotEncountered = false, overWrite = true;
        int blockNo, counter = 1, spaceIndex = -1;
        String currDir = "root", path;
        File rootDir = new File("root");
        File diskDir = new File("disk");
        File workingFile = null, newFile = null, diskFile = null;
        String command = "", filePath = "", text = "", line = "", fileSeparator = String.valueOf(File.separatorChar), newSeparator = "";
        String[] commandTokens = null, pathTokens = null, fileTokens = null, contentTokens = null;

        BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fileReader = null, blockReader = null;
        BufferedWriter fileWriter = null, blockWriter = null;

        System.out.println("WELCOME TO SIMPLE FILE SYSTEM");

        while (true)
        {
            System.out.println();
            System.out.print(currDir+" >> ");
            command = cmdReader.readLine().trim();
            commandTokens = command.split(" +");

            switch(commandTokens[0])
            {
            case "help": //displays all possible commands
                System.out.println();
                System.out.println(" List of commands those can be executed:");
                System.out.println(" > cd <dirName>                     : enters into given directory");
                System.out.println(" > cd..                             : exits from current directory");
                System.out.println(" > df <fileName>                    : deletes the given file");
                System.out.println(" > exit                             : terminates the prompt");
                System.out.println(" > help                             : lists all possible commands");
                System.out.println(" > ls                               : lists all directories and files");
                System.out.println(" > md <dirName>                     : creates a new directory with given name");
                System.out.println(" > mf <fileName> <content>          : creates a new file with given name and content");
                System.out.println(" > pf <fileName>                    : displays the contents of the given file");
                System.out.println(" > quit                             : terminates the prompt");
                System.out.println(" > rf <oldFileName> <newFileName>   : renames old file to new file name");
                break;

            case "md": //creating a new directory
                if (commandTokens.length < 2) //if less than two words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if directory is already present
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        System.out.println(" Directory already exists.");
                        break;
                    }
                }
                if (!isPresent) //if not present, create
                {
                    path = currDir;
                    path += fileSeparator+commandTokens[1];
                    workingFile = new File(path);
                    workingFile.mkdirs();
                }
                break;

            case "cd": //entering into a directory
                if (commandTokens.length < 2) //if less than two words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if directory is already present
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        break;
                    }
                }
                if (isPresent) //if present
                    currDir += fileSeparator+commandTokens[1]; //update current directory
                else //otherwise
                    System.out.println(" System cannot find the path specified."); //print error message
                break;

            case "cd..": //exiting from a directory
                //pathTokens = currDir.split(fileSeparator);
                newSeparator = File.separator.replace("\\","\\\\");
                pathTokens = currDir.split(newSeparator);
                currDir = "root";
                for (int i = 1; i < pathTokens.length-1; ++i)
                    currDir += fileSeparator+pathTokens[i];
                break;

            case "mf": //creating a new file
                if (commandTokens.length < 3) //if less than three words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if file is already present
                isPresent = false;
                overWrite = true;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        break;
                    }
                }
                if (isPresent) //if present
                {
                    System.out.println(" File already exists. Want to overwrite? (Y/N)"); //print message
                    line = cmdReader.readLine();
                    while (!(line.equals("N") || line.equals("n") || line.equals("Y") || line.equals("y"))) //till input is not matched with requirement
                    {
                        System.out.println(" Invalid input, please try again!"); //print error message
                        line = cmdReader.readLine(); //ask for input again
                    }
                    if (line.equals("N") || line.equals("n")) break; //if user enters n, break
                }
                workingFile = new File(currDir+fileSeparator+commandTokens[1]); //store the file path
                workingFile.createNewFile();
                filePath = currDir+fileSeparator+commandTokens[1];
                fileWriter = new BufferedWriter(new FileWriter(new File(filePath)));
                if (commandTokens.length == 3) //if single word input
                {
                    text = commandTokens[2].substring(1,commandTokens[2].length()-1); //extract the text
                    for (int i = 0; i < text.length(); i += 4) //for each 4 letters
                    {
                        blockNo = (int)(Math.random()*(1000000000)); //generate a random block number
                        while (diskBlocks.contains(blockNo)) //if block is alredy occupied
                            blockNo = (blockNo+1)%1000000000; //move ahead
                        diskBlocks.add(blockNo); //add the block number to occupied list
                        blockWriter = new BufferedWriter(new FileWriter(new File("disk"+fileSeparator+blockNo+".txt"))); //write to block
                        if (i < text.length()-4) blockWriter.write(text.substring(i,i+4));
                        else blockWriter.write(text.substring(i));
                        blockWriter.close();
                        fileWriter.write((counter++)+" "+blockNo+"\n"); //write to file
                    }
                }
                else //if multi word input
                {
                    isDotEncountered = false; //find the position of the first dot encountered
                    for (int i = 0; i < command.length(); ++i)
                    {
                        if (command.charAt(i) == '.') //if the current character is dot
                        {
                            //isDotEncountered = true; //mark true
                            //spaceIndex = i; //store the index of the dot
                            spaceIndex = i;
                            while (command.charAt(spaceIndex) != '"') ++spaceIndex;
                            break;
                        }
                    }
                    text = command.substring(spaceIndex+1,command.length()-1); //extract the whole text after the first double quote that comes after the dot
                    for (int i = 0; i < text.length(); i += 4) //for each 4 letters
                    {
                        blockNo = (int)(Math.random()*(1000000000)); //generate a random block number
                        while (diskBlocks.contains(blockNo)) //if block is already occupied
                            blockNo = (blockNo+1)%1000000000; //move ahead
                        diskBlocks.add(blockNo); //add the block number to occupied list
                        blockWriter = new BufferedWriter(new FileWriter(new File("disk"+fileSeparator+blockNo+".txt"))); //write to block
                        if (i < text.length()-4) blockWriter.write(text.substring(i,i+4));
                        else blockWriter.write(text.substring(i));
                        blockWriter.close();
                        fileWriter.write((counter++)+" "+blockNo+"\n"); //write to file
                    }
                }
                fileWriter.write((inodeCounter++)+"\n");
                fileWriter.close();
                break;

            case "df": //deleting a file
                if (commandTokens.length < 2) //if less than two words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if file is already present
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        break;
                    }
                }
                if (!isPresent) //if not present
                {
                    System.out.println(" File not found!"); //print error message
                    break;
                }
                workingFile = new File(currDir+fileSeparator+commandTokens[1]); //get the file path
                fileReader = new BufferedReader(new FileReader(workingFile));
                while ((line = fileReader.readLine()) != null) //for each disk block
                {
                    contentTokens = line.split(" +");
                    diskFile = new File("disk"+fileSeparator+contentTokens[1]+".txt"); //get the block path
                    diskFile.delete(); //delete the block
                }
                fileReader.close();
                workingFile.delete(); //delete the file
                break;

            case "rf": //renaming a file
                if (commandTokens.length < 3) //if less than three words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if file is already present
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        break;
                    }
                }
                if (!isPresent) //if not present
                {
                    System.out.println(" File not found!"); //print error message
                    break;
                }
                filePath = currDir+fileSeparator+commandTokens[1]; //get the old file path
                workingFile = new File(filePath);
                filePath = currDir+fileSeparator+commandTokens[2]; //get the new file path
                newFile = new File(filePath);
                workingFile.renameTo(newFile); //rename file
                break;

            case "pf": //displaying the contents of a file
                if (commandTokens.length < 2) //if less than two words, reject
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                //check if file is already present
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1])) //if present
                    {
                        isPresent = true; //mark it
                        break;
                    }
                }
                if (!isPresent) //if not present
                {
                    System.out.println(" File not found!"); //print error message
                    break;
                }
                filePath = currDir+fileSeparator+commandTokens[1]; //get the file path
                fileReader = new BufferedReader(new FileReader(new File(filePath)));
                while ((line = fileReader.readLine()) != null) //for each block
                {
                    contentTokens = line.split(" +");
                    if (contentTokens.length >= 2)
                    {
                        blockReader = new BufferedReader(new FileReader(new File("disk"+fileSeparator+contentTokens[1]+".txt"))); //get the block path
                        System.out.print(blockReader.readLine()); //print the block contents
                        blockReader.close();
                    }
                }
                fileReader.close();
                break;

            case "ls": //display all the file names
                fileTokens = new File(currDir).list();
                for (String file : fileTokens)
                {
                    fileReader = new BufferedReader(new FileReader(new File(currDir+fileSeparator+file)));
                    String prevLine = "";
                    while ((line = fileReader.readLine()) != null)
                        prevLine = line;
                    System.out.println(prevLine+"\t"+file);
                    fileReader.close();
                }
                break;

            case "exit":
            case "quit":
            case "exit()":
            case "quit()":
                System.out.println("See you again.");
                deleteDirectory(rootDir);
                deleteDirectory(diskDir);
                return;

            default:
                System.out.println(" '"+command+"' is not listed as a command, try 'help'.");
            }
        }
    }
}