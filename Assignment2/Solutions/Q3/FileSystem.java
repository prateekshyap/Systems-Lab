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

class FileSystem
{
    public static boolean devMode = false;
    private static int inodeCounter = 0;
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
        boolean isPresent = false;
        String currDir = "root", path;
        File rootDir = new File("root");
        File diskDir = new File("disk");
        File workingFile = null, newFile = null;
        String command = "", filePath = "";
        String[] commandTokens = null, pathTokens = null, fileTokens = null;

        BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fileReader = null;
        BufferedWriter fileWriter = null;

        System.out.println("WELCOME TO MY FILE SYSTEM");

        while (true)
        {
            System.out.println();
            System.out.print(currDir+" >> ");
            command = cmdReader.readLine();
            commandTokens = command.split(" +");

            switch(commandTokens[0])
            {
            case "help":
                System.out.println();
                System.out.println(" List of commands those can be executed:");
                System.out.println(" > exit: terminates the prompt");
                System.out.println(" > help: lists all possible commands");
                System.out.println(" > quit: terminates the prompt");
                break;

            case "md": //creating a new directory
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                path = currDir;
                path += "\\"+commandTokens[1];
                workingFile = new File(path);
                workingFile.mkdirs();
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
                    System.out.println("System cannot find the path specified.");
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
                workingFile = new File(currDir+"\\"+commandTokens[1]);
                workingFile.createNewFile();
                filePath = currDir+"\\"+commandTokens[1];
                fileWriter = new BufferedWriter(new FileWriter(new File(filePath)));
                if (commandTokens.length == 3)
                    fileWriter.write(commandTokens[2].substring(1,commandTokens[2].length()-1));
                else
                {
                    fileWriter.write(commandTokens[2].substring(1)+" ");
                    for (int i = 3; i < commandTokens.length-1; ++i)
                        fileWriter.write(commandTokens[i]+" ");
                    fileWriter.write(commandTokens[commandTokens.length-1].substring(0,commandTokens[commandTokens.length-1].length()-1));   
                }
                fileWriter.close();
                break;

            case "df": //deleting a file
                if (commandTokens.length < 2)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
                    break;
                }
                workingFile = new File(currDir+"\\"+commandTokens[1]);
                workingFile.delete();                
                break;

            case "rf": //renaming a file
                if (commandTokens.length < 3)
                {
                    System.out.println(" Invalid command format. Try 'help'.");
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
                filePath = currDir+"\\"+commandTokens[1];
                fileReader = new BufferedReader(new FileReader(new File(filePath)));
                System.out.println(" "+fileReader.readLine());
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
                return;

            default:
                System.out.println(" '"+command+"' is not listed as a command, try 'help'.");
            }
        }
    }
}