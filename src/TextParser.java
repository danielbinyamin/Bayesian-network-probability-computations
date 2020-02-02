import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class is in charge of parsing the input text file into Bayesian nodes and queries
 * @author daniel
 *
 */
public class TextParser {

	private String _path;
	private BufferedReader _br;

	public TextParser(String path) {
		_path = new String(path);
		try {
			_br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.out.println("could not create TextReader Object.\n");
			e.printStackTrace();
		}
	}

	/**
	 * This function returns a bayesian node from the text file (the next one by the buffers location)
	 * @return
	 * @throws Exception
	 */
	public BayesianNode getNextBayesianNodeWithoutParents() throws Exception {
		String currLine = _br.readLine();
		//skip header if first var in list
		if(currLine.contains("Network")){
			_br.readLine(); _br.readLine(); 
			currLine = _br.readLine();
		}

		if(currLine.contains("Queries")) 
			throw new Exception("could not read next bayesianNode in text file. reached Queries");

		String varName = currLine.split("\\s{1,}")[1]; //get var name

		//get var values
		String[] values;
		currLine = _br.readLine();	
		values = currLine.split(":");		
		values = Arrays.copyOfRange(values, 1, values.length);
		values[0] = values[0].replace(" ", "");
		values = values[0].split(",");

		//create list of parent names
		currLine = _br.readLine();
		String[] parentNames;
		parentNames = currLine.split(":");		
		parentNames = Arrays.copyOfRange( parentNames, 1, values.length);
		parentNames[0] =  parentNames[0].replace(" ", "");
		parentNames =  parentNames[0].split(",");

		//--create cpt of var--
		_br.readLine(); //skip cpt header

		String cptText = "";
		currLine = _br.readLine();
		while(!currLine.equals("")) {
			cptText+=currLine;
			cptText+="\n";
			currLine = _br.readLine();
		}

		CPT cpt = buildCPT(cptText, varName, values, parentNames);

		BayesianNode bayNode;

		if(Arrays.asList(parentNames).contains("none")) {
			bayNode = new BayesianNode(varName, values, cpt);
			return bayNode;
		}
		HashMap<String ,BayesianNode> tempParents = BayesianNode.createMapOfPerantNames(parentNames);
		bayNode = new BayesianNode(varName, values, tempParents, cpt);
		return bayNode;		
	}

	/**
	 * This function returns a list of InputQueries from the text file(the queries)
	 * @return
	 * @throws IOException
	 */
	public ArrayList<InputQuery> getQueries() throws IOException {
		String currLine = _br.readLine();
		while(!currLine.contains("Queries")) 
			currLine = _br.readLine();

		//read first query
		currLine = _br.readLine();

		//init vars
		ArrayList<InputQuery> queries = new ArrayList<InputQuery>();
		InputQuery currQuery;
		String currVar;
		String currValueToCalc;
		ArrayList<String[]> currConditions;
		String[] currentPairCondition;
		String currentConditionName;
		String currentConditionVal;
		int currAlgorithmNum;
		String[] splitLine;

		//iterate over queries
		while(currLine != null) {
			currLine = currLine.replace(" ", "");
			currLine = currLine.substring(2);
			splitLine = currLine.split(",");
			currAlgorithmNum = Integer.parseInt(splitLine[splitLine.length-1]);
			currLine = currLine.substring(0, currLine.length()-2);
			currVar = currLine.substring(0, currLine.indexOf("="));
			currValueToCalc = currLine.substring(currLine.indexOf("=")+1, currLine.indexOf("|"));
			currLine = currLine.substring(currLine.indexOf("|")+1);
			currLine = currLine.replace(")", "");
			splitLine = currLine.split(",");
			currConditions = new ArrayList<String[]>();
			//iterate over conditions
			for (int i = 0; i < splitLine.length; i++) {
				currentPairCondition = new String[2];
				currentPairCondition[0] = splitLine[i].substring(0, splitLine[i].indexOf("="));//var name
				currentPairCondition[1] = splitLine[i].substring(splitLine[i].indexOf("=")+1);//var value
				currConditions.add(currentPairCondition);	
			}
			currQuery = new InputQuery(currVar, currValueToCalc, currConditions, currAlgorithmNum);
			queries.add(currQuery);

			currLine = _br.readLine();//read next line

		}

		return queries;
	}

	//function that buils a CPT object from the string
	private CPT buildCPT(String cptText, String varName,String[] values, String[] parentNames) {
		//init vars		
		CPT cpt = new CPT(varName);
		String[] currSplitLine;
		ArrayList<String[]> currConditions;
		Query currQuery;
		boolean nextIsProp;
		String valToCalc = "";
		ArrayList<String> valsToCalFromLine = new ArrayList<String>();

		String[] lines = cptText.split("\\r?\\n");
		//iterate over lines of cpt
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].replace(" ", "");
			currSplitLine = lines[i].split(",");
			int parentIndex = 0;
			currConditions = new ArrayList<String[]>();
			nextIsProp = false;
			//iterate over line
			for (int j = 0; j < currSplitLine.length; j++) {

				if(!currSplitLine[j].contains("=") && !nextIsProp) {
					String[] currPair = new String[2];
					currPair[0] = parentNames[parentIndex];
					currPair[1] = currSplitLine[j];
					currConditions.add(currPair);
					parentIndex++;
				}
				else if(currSplitLine[j].contains("=")){
					valToCalc = currSplitLine[j].substring(1);
					valsToCalFromLine.add(valToCalc);
					nextIsProp = true;
					continue;
				}
				else {
					cpt.addQueryEqulas(valToCalc, currConditions, Double.parseDouble(currSplitLine[j]));
					nextIsProp = false;
				}
			}
			for (int k = 0; k < values.length; k++) {
				if(!valsToCalFromLine.contains(values[k])) {
					valToCalc = values[k];
					break;
				}
			}
			double ans = BigDecimal.valueOf(1.0).subtract(BigDecimal.valueOf(cpt.sumProb(currConditions))).doubleValue();
			cpt.addQueryEqulas(valToCalc, currConditions, ans);

		}//end lines

		return cpt;
	}


}
