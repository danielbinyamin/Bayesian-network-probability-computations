import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * The main executable class
 * @author daniel
 *
 */

public class Main {

//	static final String input = "input.txt";
//	static final String output = "output.txt";
	
	public static void main(String[] args)  {
		System.out.println("Input file: " + args[0]);
		System.out.println("Output file: " + args[1]);
		ArrayList<Answer> answers = getAnswers(args[0]);
		outputFileCreator fc = new outputFileCreator(args[1]);
		fc.createOutputFile(answers);
		System.out.println("Done. Output file created: " + args[1]);
	}

	/**
	 * returns a list of answers for a given input file
	 * @param filePath
	 * @return list of answers
	 */
	public static ArrayList<Answer> getAnswers(String filePath) {
		BayesianNetwork BN = new BayesianNetwork();
		ArrayList<InputQuery> qs = new ArrayList<>();
		try {
			BN.buildBayesianNetworkFromTextFile(filePath);
			TextParser tx = new TextParser(filePath);
			qs = tx.getQueries();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Answer> answers = new ArrayList<Answer>();
		for (int i = 0; i < qs.size(); i++) {
			answers.add(BN.calcQuery(qs.get(i)));
		}

		return answers;
	}

}
