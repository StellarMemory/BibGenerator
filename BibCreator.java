// ----------------------------------------------------------------------------
// Written by: Nuo Jie (Miguel) Cheng, Concordia University, Montreal, QC
// COMP249
// Date: March 16, 2018
// LinkedIn Profile: https://www.linkedin.com/in/nuo-jie-miguel-cheng-3aa16569/
// ----------------------------------------------------------------------------

package comp249;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**BibCreator class process JSON (.bib) files and produce formatted bibliography using IEEE, ACM, and NJ reference style.
 * @author Cheng
 * @see FileInvalidException
 */
public class BibCreator 
{
	private static int invalidFileCounter = 0;
	private static int articleCounter = 0;
	private static String author = "";
	private static String journal = "";
	private static String title = "";
	private static String month = "";
	private static String year = "";
	private static String volume = "";
	private static String number = "";
	private static String pages = "";
	private static String doi = "";

	/**Attempts to open input files for reading. 
	 * @param inputFile an array of objects from Scanner class
	 */
	public static void openInputFiles(Scanner[] inputFile)
	{
		for (int i = 0; i < inputFile.length; i++)
		{
			try
			{
				inputFile[i] = new Scanner(new FileInputStream("Latex" + (i + 1) + ".bib")); //can change directory address
			}

			catch (FileNotFoundException e)
			{
				System.out.println("Error: Could not open input file Latex" + (i + 1) + ".bib for reading.\n"
						+ "Please check if file exists! Program will terminate after closing any opened files.");
				for (int j = 0; j < i - 1; j++)
					inputFile[j].close();
				System.exit(0);
			}
		}	
	}

	/**Attempts to create output files for PrintWriter class to write on, and for File class to perform necessary operations. 
	 * @param inputFile an array of objects from Scanner class
	 * @param outputFile a 2D array of objects from PrintWriter class
	 * @param file a 2D array of objects from File class
	 */
	public static void createOutputFiles(Scanner[] inputFile, PrintWriter[][] outputFile, File[][] file)
	{
		String[] citationType = {"IEEE", "ACM", "NJ"};
		int i = 0, j = 0;
		try
		{
			for (i = 0; i < outputFile.length; i++)
				for (j = 0; j < outputFile[i].length; j++)
				{
					file[i][j] = new File(citationType[j] + (i + 1) + ".json");
					outputFile[i][j] = new PrintWriter(new FileOutputStream(file[i][j]));
				}
		}

		catch (FileNotFoundException e)
		{
			System.out.println("Error: Problem creating output file " + citationType[j] + (i + 1) + ".json\n"
					+ "Please check if file name already in use, or if system low memory\n"
					+ "System will terminate after deleting created files and closing input files...");
			deleteFiles(outputFile, file);
			for (int k = 0; k < inputFile.length; i++)
				inputFile[k].close();
			System.exit(0);
		}
	}
	
	/**Delete all files in array
	 * @param outputFile a 2D array of objects from PrintWriter class
	 * @param file a 2D array of objects from File class
	 */
	public static void deleteFiles(PrintWriter[][] outputFile, File[][] file)
	{
		for (int i = 0; i < outputFile.length; i++)
			for (int j = 0; j < outputFile[i].length; j++)
			{
				if (outputFile[i][j] != null)
					outputFile[i][j].close();
				if (file[i][j].exists())
					file[i][j].delete();
			}
	}
	
	/**Main processing method, takes static attributes for each article, and write to corresponding output files.
	 * Catch FileInvalidException and delete the corresponding output files, continue processing other input files. 
	 * @param inputFile an array of objects from Scanner class	
	 * @param outputFile a 2D array of objects from PrintWriter class
	 * @param file a 2D array of objects from File class
	 */
	public static void processFilesForValidation(Scanner[] inputFile, PrintWriter[][] outputFile, File[][] file)
	{
		for (int i = 0; i < inputFile.length; i++)
		{
			nextFile:
			while (inputFile[i].hasNextLine())
			{
				if (inputFile[i].nextLine().contains("@ARTICLE"))
				{
					articleCounter++;
					String line = inputFile[i].nextLine();
					do
					{
						try
						{
							setAttribute(inputFile[i], line);
						}
						catch (FileInvalidException e)
						{
							System.out.println("Problem detected with input file: Latex" + (i + 1) + ".bib");
							System.out.println(e.getMessage());
							invalidFileCounter++;
							for (int j = 0; j < outputFile[i].length; j++)
							{
								outputFile[i][j].close();
								file[i][j].delete();
							}
							break nextFile;
						}
						line = inputFile[i].nextLine();
					}
					while (!line.startsWith("}"));
					
					writeIEEE(outputFile[i][0]);
					writeACM(outputFile[i][1]);
					writeNJ(outputFile[i][2]);
				}
			}
			articleCounter = 0;
		}
		closePrintWriterAndScanner(inputFile, outputFile, file);
		System.out.println(totalInvalidStatement(inputFile));
	}
	
	/**Produce a statement that shows the number of invalid files, and the number of valid files being created
	 * @param inputFile an array of objects from Scanner class
	 * @return a String value
	 */
	public static String totalInvalidStatement(Scanner[] inputFile)
	{
		return "A total of " + invalidFileCounter + " files were invalid, and could not be processed. All other "
				+ (inputFile.length - invalidFileCounter) + " \"Valid\" files have been created.\n";
	}
	
	/**Write to PrintWriter object using IEEE reference format
	 * @param outputFile an object from PrintWriter class
	 */
	public static void writeIEEE(PrintWriter outputFile)
	{
		outputFile.print("[" + articleCounter + "]\t" + author.replace(" and ", ", ") + ". ");
		outputFile.print("\"" + title + "\", ");
		outputFile.print(journal + ", ");
		outputFile.print("vol. " + volume + ", ");
		outputFile.print("no. " + number + ", ");
		outputFile.print("p. " + pages + ", ");
		outputFile.println(month + " " + year + ".\n");
	}
	
	/**Write to PrintWriter object using ACM reference format
	 * @param outputFile an object from PrintWriter class
	 */
	public static void writeACM(PrintWriter outputFile)
	{
		outputFile.print("[" + articleCounter + "]\t" + countAuthor(author));
		outputFile.print(year + ". " + title + ". " + journal + ". " + volume + ", " + number + " (" + year + "), ");
		outputFile.println(pages + ". DOI:https://doi/org/" + doi + ".\n");
	}
	
	/**Write to PrintWriter object using NJ reference format
	 * @param outputFile an object from PrintWriter class
	 */
	public static void writeNJ(PrintWriter outputFile)
	{
		outputFile.print("[" + articleCounter + "]\t" + author.replace(" and ", " & ") + ". ");
		outputFile.println(title + ". " + journal + ". " + volume + ", " + pages + "(" + year + ").\n");
	}
	
	/**Return the number of authors, for ACM reference formatting purposes
	 * @param author a String value
	 * @return a String value
	 */
	public static String countAuthor(String author)
	{
		StringTokenizer nameFactory = new StringTokenizer(author, "and");
		if (nameFactory.countTokens() == 1)
			return author;
		else 
			return nameFactory.nextToken() + "et al. ";
	}
	
	/**Close all Scanner objects and PrintWriter objects stored in the array, provided they exist
	 * @param inputFile an array of objects from Scanner class
	 * @param outputFile a 2D array of objects from PrintWriter class
	 * @param file a 2D array of objects from File class
	 */
	public static void closePrintWriterAndScanner(Scanner[] inputFile, PrintWriter[][] outputFile, File[][] file)
	{
		for (int i = 0; i < inputFile.length; i++)
		{
			inputFile[i].close();
			for (int j = 0; j < outputFile[i].length; j++)
				if (file[i][j].exists())
					outputFile[i][j].close();
		}
	}
	
	/**Set static attributes by identifying the appropriate field from line string
	 * @param inputFile an object from Scanner class
	 * @param line a String value
	 * @throws FileInvalidException if the file contains empty field
	 */
	public static void setAttribute(Scanner inputFile, String line) throws FileInvalidException
	{
		if (line.contains("{}"))
		{
			System.out.println("Error: Detected Empty Field!\n============================");
			throw new FileInvalidException("File is Invalid: Field \"" + line.substring(0, line.indexOf("=")) + "\""
					+ " is Empty. Processing stopped at this point. Other empty fields may be present as well!\n");
		}
		if (line.startsWith("author="))
			author = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("journal="))
			journal = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("title="))
			title = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("month="))
			month = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("year="))
			year = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("volume="))
			volume = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("number="))
			number = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("pages="))
			pages = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
		else if (line.startsWith("doi="))
			doi = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
	}
	
	/**Display contents from the start to the end of a file taken by an object of the BufferedReader class
	 * @param fileIn an object from the BufferedReader class
	 * @param fileName a String value
	 * @throws IOException if an error occurs at displaying the content 
	 */
	public static void displayContent(BufferedReader fileIn, String fileName) throws IOException
	{
		System.out.println("Here are the contents of the successfully created JSON File: " + fileName);
		String line = fileIn.readLine();
		while (line != null)
		{
			System.out.println(line);
			line = fileIn.readLine();
		}
		fileIn.close();
	}
	
	/**Prompt user to enter a file name to display. Call displayContent to read content. 
	 * If user enters an incorrect file name, FileNotFoundException is thrown. User given a second and final chance.
	 */
	public static void displayFile()
	{
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Please enter the name of one of the files that you need to review: ");
		String fileName = keyboard.next();
		BufferedReader br = null;
		
		try
		{
			br = new BufferedReader(new FileReader(fileName));
			displayContent(br, fileName);
		}
		
		catch (FileNotFoundException e)
		{
			System.out.println("Could not open input file. File does not exist; possibly it could not be created!");
			System.out.println("However, you will be allowed a second and final chance to enter another file name.\n");
			System.out.print("Please enter the name of one of the files that you need to review: ");
			String newFileName = keyboard.next();
			try
			{
				br = new BufferedReader(new FileReader(newFileName));
				displayContent(br, newFileName);
			}
			
			catch (FileNotFoundException e2)
			{
				System.out.println("Could not open input file again! Either file does not exist or could not be created.");
				System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
				System.exit(0);
			}
			
			catch (IOException e2)
			{
				System.out.println("Error: An error has occurred while reading from the " + newFileName + " file.");
				System.out.println("Program will terminate.");
				System.exit(0);
			}
		}
		
		catch (IOException e)
		{
			System.out.println("Error: An error has occurred while reading from the " + fileName + " file.");
			System.out.println("Program will terminate.");
			System.exit(0);
		}
		
		keyboard.close();
	}
	
	public static void main(String[] args)
	{
		System.out.println("Welcome to BibCreator!\n");
	
		Scanner[] sc = new Scanner[10];
		openInputFiles(sc);
	
		PrintWriter[][] pw = new PrintWriter[10][3];
		File[][] file = new File[10][3];
		createOutputFiles(sc, pw, file);
		
		processFilesForValidation(sc, pw, file);
		
		displayFile();
		
		System.out.println("Goodbye! Hope you enjoyed creating the needed files using BibCreator.");
	}
}

