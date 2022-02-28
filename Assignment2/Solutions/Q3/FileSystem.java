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
        int blockNo, counter = 1, dotIndex = -1;
        String currDir = "root", path;
        File rootDir = new File("root");
        File diskDir = new File("disk");
        File workingFile = null, newFile = null, diskFile = null;
        String command = "", filePath = "", text = "", line = "";
        String[] commandTokens = null, pathTokens = null, fileTokens = null, contentTokens = null;

        BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fileReader = null, blockReader = null;
        BufferedWriter fileWriter = null, blockWriter = null;

        System.out.println("WELCOME TO MY FILE SYSTEM");

        while (true)
        {
            System.out.println();
            System.out.print(currDir+" >> ");
            command = cmdReader.readLine().trim();
            commandTokens = command.split(" +");

            switch(commandTokens[0])
            {
            case "help":
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
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        System.out.println(" Directory already exists.");
                        break;
                    }
                }
                if (!isPresent)
                {
                    path = currDir;
                    path += "\\"+commandTokens[1];
                    workingFile = new File(path);
                    workingFile.mkdirs();
                }
                break;

            case "cd": //entering into a directory
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        break;
                    }
                }
                if (isPresent)
                    currDir += "\\"+commandTokens[1];
                else
                    System.out.println(" System cannot find the path specified.");
                break;

            case "cd..": //exiting from a directory
                pathTokens = currDir.split("\\\\");
                currDir = "root";
                for (int i = 1; i < pathTokens.length-1; ++i)
                    currDir += "\\"+pathTokens[i];
                break;

            case "mf": //creating a new file
                if (commandTokens.length < 3)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                overWrite = true;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        break;
                    }
                }
                if (isPresent)
                {
                    System.out.println(" File already exists. Want to overwrite? (Y/N)");
                    line = cmdReader.readLine();
                    while (!(line.equals("N") || line.equals("n") || line.equals("Y") || line.equals("y")))
                    {
                        if (line.equals("N") || line.equals("n")) { overWrite = false; break; }
                        else if (line.equals("Y") || line.equals("y")) { overWrite = true; break; }
                        else if (!(line.equals("Y") || line.equals("y"))) { System.out.println(" Invalid input, please try again!"); line = cmdReader.readLine(); }
                    }
                }
                if (!overWrite) break;
                workingFile = new File(currDir+"\\"+commandTokens[1]);
                workingFile.createNewFile();
                filePath = currDir+"\\"+commandTokens[1];
                fileWriter = new BufferedWriter(new FileWriter(new File(filePath)));
                if (commandTokens.length == 3)
                {
                    text = commandTokens[2].substring(1,commandTokens[2].length()-1);
                    for (int i = 0; i < text.length(); i += 4)
                    {
                        blockNo = (int)(Math.random()*(1000000000));
                        while (diskBlocks.contains(blockNo))
                            blockNo = (blockNo+1)%1000000000;
                        diskBlocks.add(blockNo);
                        blockWriter = new BufferedWriter(new FileWriter(new File("disk\\"+blockNo+".txt")));
                        if (i < text.length()-4) blockWriter.write(text.substring(i,i+4));
                        else blockWriter.write(text.substring(i));
                        blockWriter.close();
                        fileWriter.write((counter++)+" "+blockNo+"\n");
                    }
                }
                else
                {
                    isDotEncountered = false;
                    for (int i = 0; i < command.length(); ++i)
                    {
                        if (command.charAt(i) == '.')
                        {
                            isDotEncountered = true;
                            dotIndex = i;
                            break;
                        }
                    }
                    text = command.substring(dotIndex+6,command.length()-1);
                    for (int i = 0; i < text.length(); i += 4)
                    {
                        blockNo = (int)(Math.random()*(1000000000));
                        while (diskBlocks.contains(blockNo))
                            blockNo = (blockNo+1)%1000000000;
                        diskBlocks.add(blockNo);
                        blockWriter = new BufferedWriter(new FileWriter(new File("disk\\"+blockNo+".txt")));
                        if (i < text.length()-4) blockWriter.write(text.substring(i,i+4));
                        else blockWriter.write(text.substring(i));
                        blockWriter.close();
                        fileWriter.write((counter++)+" "+blockNo+"\n");
                    }
                }
                fileWriter.close();
                break;

            case "df": //deleting a file
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        break;
                    }
                }
                if (!isPresent)
                {
                    System.out.println(" File not found!");
                    break;
                }
                workingFile = new File(currDir+"\\"+commandTokens[1]);
                fileReader = new BufferedReader(new FileReader(workingFile));
                while ((line = fileReader.readLine()) != null)
                {
                    contentTokens = line.split(" +");
                    diskFile = new File("disk\\"+contentTokens[1]+".txt");
                    diskFile.delete();
                }
                fileReader.close();
                workingFile.delete();                
                break;

            case "rf": //renaming a file
                if (commandTokens.length < 3)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        break;
                    }
                }
                if (!isPresent)
                {
                    System.out.println(" File not found!");
                    break;
                }
                filePath = currDir+"\\"+commandTokens[1];
                workingFile = new File(filePath);
                filePath = currDir+"\\"+commandTokens[2];
                newFile = new File(filePath);
                workingFile.renameTo(newFile);
                break;

            case "pf": //displaying the contents of a file
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                isPresent = false;
                workingFile = new File(currDir);
                for (String subFile : workingFile.list())
                {
                    if (subFile.equals(commandTokens[1]))
                    {
                        isPresent = true;
                        break;
                    }
                }
                if (!isPresent)
                {
                    System.out.println(" File not found!");
                    break;
                }
                filePath = currDir+"\\"+commandTokens[1];
                fileReader = new BufferedReader(new FileReader(new File(filePath)));
                while ((line = fileReader.readLine()) != null)
                {
                    contentTokens = line.split(" +");
                    blockReader = new BufferedReader(new FileReader(new File("disk\\"+contentTokens[1]+".txt")));
                    System.out.print(blockReader.readLine());
                    blockReader.close();
                }
                fileReader.close();
                break;

            case "ls": //display all the file names
                fileTokens = new File(currDir).list();
                for (String file : fileTokens)
                    System.out.println(" "+file);
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