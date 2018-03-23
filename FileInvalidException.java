// -------------------------------------------------------------------
// Written by: Nuo Jie Cheng, ID:21347829
// COMP249
// Assignment #3
// Due Date: March 16, 2018
// -------------------------------------------------------------------

package comp249;

/**A child exception class that extends Exception class
 * @author cheng
 * @see BibCreator 
 */
public class FileInvalidException extends Exception
{
	/**A default constructor that stores a default message
	 */
	public FileInvalidException()
	{
		super("Error: Input file cannot be parsed due to missing information\n" + 
				"(i.e. month={}, title={}, etc.)");
	}
	
	/**A parametrized constructor that takes a any message string and store it. 
	 * @param message a String value
	 */
	public FileInvalidException(String message)
	{
		super(message);
	}
	
	/**overrides getMessage() method
	 */
	public String getMessage()
	{
		return super.getMessage();
	}
}
