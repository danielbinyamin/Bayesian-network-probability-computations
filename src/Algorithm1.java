import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of algorithm 1.
 * @author danie
 *
 */
public class Algorithm1 implements Calculate {

	private Query _query;
	private BayesianNetwork _bn;
	private int _numOfSums;
	private int _numOfProducts;
	
	public Algorithm1(BayesianNetwork bn, Query query) {
		_query = new Query(query);
		_bn = new BayesianNetwork(bn);
		_numOfSums = 0;
		_numOfProducts = 0;
	}
	
	@Override
	public Answer calc() {
		
		ArrayList<String[]> jointDistOfQuery = new ArrayList<String[]>();
		String[] currVarPair = new String[2];
		currVarPair[0] = _query.get_var();
		currVarPair[1] = _query.get_valueToCalc();
		jointDistOfQuery.add(currVarPair);
		//iterate over conditions in query
		for (String[] condition : _query.get_conditions()) {
			currVarPair = new String[2];
			currVarPair[0] = condition[0];
			currVarPair[1] = condition[1];
			jointDistOfQuery.add(currVarPair);
		} 
		BayesianNode  queryVar = _bn.getBayesianNode(_query.get_var());
		double[] answersForNorm = new double[queryVar.get_values().length];
		int indexOfAnswers = -1;
		for (int i = 0 ; i < queryVar.get_values().length ; i++) {
			jointDistOfQuery.get(0)[1] = queryVar.get_values()[i];
			answersForNorm[i] = calcSemiJointDist(jointDistOfQuery);
			if(queryVar.get_values()[i].equals(_query.get_valueToCalc()))
				indexOfAnswers = i;
		}
		double alpha = calcAlpha(answersForNorm);

		//no need to calculate this as a product
		double ans = answersForNorm[indexOfAnswers]*alpha;

		return new Answer(ans, _numOfSums, _numOfProducts);
	}

	//function to calc a full joined distribution 
	private double calcFullJointDist(ArrayList<String[]> jointQueries) {
		double ans =1;
		Query q;
		String[] condition;
		String valOfParent = "";
		ArrayList<BayesianNode> currParents;
		ArrayList<String[]> conditionsForCurrentQuery;
		if(!jointQueries.isEmpty())
			_numOfProducts--;
		//iterate over each (var, value) in joint distribution
		for (String[] currentVar : jointQueries) {
			conditionsForCurrentQuery = new ArrayList<String[]>();
			currParents = _bn.getParents(currentVar[0]);
			//iterate over var's parents
			for (BayesianNode parent : currParents) {
				//find parent and its value for query
				for(String[] optionalParent : jointQueries) {
					if(parent.get_name().equals(optionalParent[0]))
						valOfParent = optionalParent[1];
				}
				condition = new String[2];
				condition[0] = parent.get_name();
				condition[1] = valOfParent;
				conditionsForCurrentQuery.add(condition);
			}
			q = new Query(currentVar[0], currentVar[1], conditionsForCurrentQuery);
			ans *= _bn.getPrFromCPT(q);
			_numOfProducts++;
		}		
		return ans;
	}

	//function to calc a joined distribution 
	private double calcSemiJointDist(ArrayList<String[]> jointDist) {
		double ans = 0;
		ArrayList<BayesianNode> hiddenVars = getHiddenVars(jointDist);
		if(hiddenVars.isEmpty()) {
			ans = calcFullJointDist(jointDist);
			return ans;
		}

		ArrayList<ArrayList<String[]>> allPermutations = getAllPermutations(getListsOfVarValues(hiddenVars));
		
		ArrayList<String[]> currentJointDistToCalc = new ArrayList<String[]>(jointDist);
		_numOfSums--;
		for (ArrayList<String[]> currentPermutations : allPermutations) {
			currentJointDistToCalc.addAll(currentPermutations);
			ans += calcFullJointDist(currentJointDistToCalc);
			_numOfSums++;
			currentJointDistToCalc = new ArrayList<String[]>(jointDist);
		}
		return ans;

	}

	private ArrayList<ArrayList<String[]>> getListsOfVarValues(ArrayList<BayesianNode> nodes) {
		ArrayList<ArrayList<String[]>> rtn = new ArrayList<ArrayList<String[]>>();
		
		for (BayesianNode currNode : nodes) 
			rtn.add(currNode.createListOfValuesAndNames());
		
		return rtn;		
	}
	
	private ArrayList<BayesianNode> getHiddenVars(ArrayList<String[]> jointQueries) {
		ArrayList<BayesianNode> hiddenVars = new ArrayList<BayesianNode>();
		boolean hidden = true;
		for (BayesianNode currVar : _bn.get_baysianNodes()) {
			for (String[] varInJointDist : jointQueries) 
				if(varInJointDist[0].equals(currVar.get_name())) {
					hidden = false;
					break;
				}
				else
					hidden = true;

			if(hidden) 
				hiddenVars.add(currVar);
		}

		return hiddenVars;
	}

	private double calcAlpha(double[] answers) {
		double sumArray = 0;
		if(answers.length > 0)
			_numOfSums--;
		for (int i = 0; i < answers.length; i++) {
			sumArray += answers[i];
			_numOfSums++;
		}
		return Math.pow(sumArray,-1.0);
	}

	//function to get all the diffrent permutations of list of lists
	private ArrayList<ArrayList<String[]>> getAllPermutations(List<ArrayList<String[]>> lists) {
		ArrayList<ArrayList<String[]>> resultLists = new ArrayList<ArrayList<String[]>>();
	    if (lists.size() == 0) {
	        resultLists.add(new ArrayList<String[]>());
	        return resultLists;
	    } else {
	        ArrayList<String[]> firstList = lists.get(0);
	        ArrayList<ArrayList<String[]>> remainingLists = getAllPermutations(lists.subList(1, lists.size()));
	        for (String[] condition : firstList) {
	            for (ArrayList<String[]> remainingList : remainingLists) {
	                ArrayList<String[]> resultList = new ArrayList<String[]>();
	                resultList.add(condition);
	                resultList.addAll(remainingList);
	                resultLists.add(resultList);
	            }
	        }
	    }
	    return resultLists;
	}
	
}
