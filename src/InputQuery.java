import java.util.ArrayList;

/**
 * This class is an extended class of Query which has information on which algorithm to use in order to solve it.
 * It represents a query in the text file.
 * @author daniel
 *
 */
public class InputQuery extends Query {

	private int _algorithmNum;

	public InputQuery(String var, String valueToCalc, ArrayList<String[]> conditions, int algorithmNum) {
		super(var, valueToCalc, conditions);
		this._algorithmNum = algorithmNum;
	}

	public int get_algorithmNum() {
		return _algorithmNum;
	}


}
