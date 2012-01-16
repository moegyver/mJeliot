package avinteraction.parser;

import java.io.StreamTokenizer;

/**
 * The main parser routines are encapsulated in this class.
 * Use it for comfortably parsing a file structure.  
 * @author Gina Haeussge, huge(at)rbg.informatik.tu-darmstadt.de
 */
public class MainParser
{
	protected StreamTokenizer stok;
	
	public MainParser (StreamTokenizer s)
	{
		stok = s;
	}
}