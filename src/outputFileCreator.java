import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This class is in charge of creating the output fil from given answers
 * @author danie
 *
 */
public class outputFileCreator {
	
	private PrintWriter _outs;
	private String _fileName;
	
	public outputFileCreator(String fileName) {
		_fileName = fileName;
		try {
			_outs = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			System.out.println("Could not create printwriter for output file\n");
			e.printStackTrace();
		}
	}
	
	public void createOutputFile(ArrayList<Answer> answers) {
		for (int i = 0; i < answers.size(); i++) {
			_outs.println(answers.get(i).toString());
		}
		_outs.close();
	}
	
}
