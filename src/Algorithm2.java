import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class is the implementation of algorithm 2, Variable elimination.
 * @author danie
 *
 */
public class Algorithm2 implements Calculate {

	private Query _query;
	private BayesianNetwork _bn;
	private int _numOfSums;
	private int _numOfProducts;

	public Algorithm2(BayesianNetwork bn, Query query) {
		_query = new Query(query);
		_bn = new BayesianNetwork(bn);
		_numOfSums = 0;
		_numOfProducts = 0;
	}

	@Override
	public Answer calc() {
		//init factors
		ArrayList<String> evidenceVars = new ArrayList<String>();
		for (String[] string : _query.get_conditions()) {
			evidenceVars.add(string[0]);
		}
		ArrayList<Factor> allCurrentFactors = new ArrayList<Factor>();
		ArrayList<CPT> cptsToCreateFactor = new ArrayList<CPT>();
		for (BayesianNode currNode : _bn.get_baysianNodes()) {
			cptsToCreateFactor.add(currNode.get_cpt());
		}
		ArrayList<String> namesToEliminate = getHiddenVarNames();
		removeIreleventFactors(cptsToCreateFactor, namesToEliminate);
		
		for (CPT cpt : cptsToCreateFactor) {
			allCurrentFactors.add(turnCPTtoFactorTable(cpt, evidenceVars));
		}
		
		sortVarsToEliminate(namesToEliminate); //sort names by ABC order
		
		//for each hidden var, join factors and eliminate
		for (String name : namesToEliminate) {
			Factor joinedFactorofVar = joinAllFactors(name, allCurrentFactors);
			joinedFactorofVar.sumOut(name);
			_numOfSums += joinedFactorofVar.getNumOfSumsAndZero();
			allCurrentFactors.add(joinedFactorofVar);
		}
		//join remaining factors	
		Factor lastFactor;
		if(allCurrentFactors.size()>1)
			lastFactor = joinLastFactors(_query.get_var(), allCurrentFactors);
		else
			lastFactor = allCurrentFactors.get(0);
		try {
			lastFactor.normalize();
			_numOfSums += lastFactor.getNumOfSumsAndZero();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Double ans = 0.0;

		try {
			ans = lastFactor.getAnswer(_query.get_valueToCalc());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Answer(ans , _numOfSums, _numOfProducts);
	}
	
	/**
	 * sorts by ABC
	 * @param varNames
	 */
	public void sortVarsToEliminate(ArrayList<String> varNames) {
		Collections.sort(varNames);
	}

	//main function which delets irelevent cpt nodes for var elimination
	private void removeIreleventFactors(ArrayList<CPT> factors, ArrayList<String> namesToEliminateList) {
		for (Iterator<CPT> iterator = factors.iterator(); iterator.hasNext(); ) {
			CPT cpt = iterator.next();
			if(cptIsNotRelevet(cpt)) {
				iterator.remove();
				namesToEliminateList.remove(cpt.get_varName());
			}
		}		
	}
	
	//check if cpt is relevent to query
	private boolean cptIsNotRelevet(CPT cpt) {
		
		String varName = cpt.get_varName();
		ArrayList<BayesianNode> sources = new ArrayList<BayesianNode>();
		if(getQueryVarNames().contains(varName))
			return false;
		for (String name : getQueryVarNames()) {
			sources.add(_bn.getBayesianNode(name));
		}
		
		for (BayesianNode bayesianNode : sources) {
			if(BFS(bayesianNode, varName))
				return false;
		}
		return true;
	}
	
	private boolean BFS(BayesianNode source, String varNameToFind) {

		//init visited hashmap with false
		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
		for (BayesianNode currNode : _bn.get_baysianNodes()) {
			visited.put(currNode.get_name(), false);
		}

		Queue<String> q = new LinkedList<>();

		//mark node as visited and add to queue
		visited.replace(source.get_name(), true);		
		q.add(source.get_name());

		String u;
		BayesianNode uNode;

		while(!q.isEmpty()) {
			u = q.remove();
			uNode = _bn.getBayesianNode(u);
			//iterate over parents
			for (BayesianNode parent : uNode.get_parents().values()) 
				if(!visited.get(parent.get_name())) {
					visited.replace(parent.get_name(), true);
					if(parent.get_name().equals(varNameToFind))
						return true; //found node
					q.add(parent.get_name());
				}		
		}
		return false;
	}
	
	// This function returns an arrayList of the factors and removes them from 'factors'
	private ArrayList<Factor> getAndRemoveFactors(ArrayList<Factor> factors, String name) {

		ArrayList<Factor> rtn = new ArrayList<Factor>();
		for (Iterator<Factor> iterator = factors.iterator(); iterator.hasNext(); ) {
			Factor factor = iterator.next();
			if(factor.containsFactorName(name)) {
				rtn.add(factor);
				iterator.remove();
			}
		}	
		return rtn;
	}

	private ArrayList<String> getHiddenVarNames() {
		ArrayList<String> hiddenVars = new ArrayList<String>();
		boolean flag = true;
		for (BayesianNode currNode : _bn.get_baysianNodes()) {
			for (String[] currConditions : _query.get_conditions()) {
				if(currConditions[0].equals(currNode.get_name())) {
					flag = false;
					break;
				}
			}
			if(flag && !currNode.get_name().equals(_query.get_var())) 
				hiddenVars.add(currNode.get_name());
			flag = true;
		}
		return hiddenVars;
	}

	//This function joins all factors of a var
	private Factor joinAllFactors(String varName, ArrayList<Factor> factors) {
		//collect factors of name
		ArrayList<Factor> currentFactorsBeingJoined = getAndRemoveFactors(factors, varName);

		//while there are still factors to join
		while(currentFactorsBeingJoined.size() > 1) {
			//join 2 min factors and update currentFactorsBeingJoined
			int[] indexOfTwoMin = getIndexOfTwoMin(currentFactorsBeingJoined);
			Arrays.sort(indexOfTwoMin);
			//join one with the other
			currentFactorsBeingJoined.get(indexOfTwoMin[0]).join(currentFactorsBeingJoined.get(indexOfTwoMin[1]));//TODO: check join is made
			currentFactorsBeingJoined.remove(indexOfTwoMin[1]);//TODO: check remove factor(that it is removed
			_numOfProducts += currentFactorsBeingJoined.get(indexOfTwoMin[0]).getNumOfProductsAndZero();
		}

		return currentFactorsBeingJoined.get(0);

	}
	
	private Factor joinLastFactors(String varName, ArrayList<Factor> factors) {

		ArrayList<Factor> currentFactorsBeingJoined = new ArrayList<Factor>(factors);
		factors.clear();

		//while there are still factors to join
		while(currentFactorsBeingJoined.size() > 1) {
			//join 2 min factors and update currentFactorsBeingJoined
			int[] indexOfTwoMin = getIndexOfTwoMin(currentFactorsBeingJoined);
			Arrays.sort(indexOfTwoMin);
			//join one with the other
			currentFactorsBeingJoined.get(indexOfTwoMin[0]).join(currentFactorsBeingJoined.get(indexOfTwoMin[1]));//TODO: check join is made
			currentFactorsBeingJoined.remove(indexOfTwoMin[1]);//TODO: check remove factor(that it is removed
			_numOfProducts += currentFactorsBeingJoined.get(indexOfTwoMin[0]).getNumOfProductsAndZero();
		}

		return currentFactorsBeingJoined.get(0);
	}

	//This function turns the CPT into a table without evidence being factors
	private Factor turnCPTtoFactorTable(CPT cpt, ArrayList<String> evidenceVars) {

		//get names of vars for columns
		ArrayList<String> varsToInitForFactors = cpt.getVarsFromCPT();
		for (Iterator<String> iterator = varsToInitForFactors.iterator(); iterator.hasNext(); ) {
			String name = iterator.next();
			if(evidenceVars.contains(name))
				iterator.remove();
		}

		Factor factor = new Factor(varsToInitForFactors);

		Query q;
		int currentCollumIndex;
		ArrayList<String> rowToAdd;
		//iterate over queries in cpt	
		outerloop:
			for(Entry<Query, Double> entry : cpt.get_cpt().entrySet()) {
				q = entry.getKey();

				//check if current query needs to be added as a row
				//check query
				if(isEvidence(q.get_var()) && !q.get_valueToCalc().equals(_query.getConditionValue(q.get_var()))) 
					continue;
				//check evidence
				for (String[] condition : q.get_conditions()) {
					if(isEvidence(condition[0]) && !condition[1].equals(_query.valueOfConditionFromQuery(condition[0])))
						continue outerloop;
				}

				/*if code made it here, this query needs to be added as a row*/

				//init row with empty values
				rowToAdd = new ArrayList<String>();
				for (int i = 0; i < varsToInitForFactors.size(); i++) {
					rowToAdd.add("");
				}

				//check query question
				currentCollumIndex = factor.getColumnIndex(q.get_var());
				if(currentCollumIndex != -1)
					rowToAdd.set(currentCollumIndex, q.get_valueToCalc());


				//check conditions
				for (String[] condition : q.get_conditions()) {
					currentCollumIndex = factor.getColumnIndex(condition[0]);
					if(currentCollumIndex != -1)
						rowToAdd.set(currentCollumIndex, condition[1]);
				}

				//add value to last collumn
				String toAdd = Double.toString(cpt.P(q));
				rowToAdd.add(toAdd);
				factor.addRow(rowToAdd);
			}
		return factor;
	}

	//get indexes of two minimal factors. only works with list size>1
	private int[] getIndexOfTwoMin(ArrayList<Factor> factors) {

		int min1 = factors.get(0).size();
		int min2 = factors.get(1).size();

		int index1 = 0;
		int index2 = 1;

		if (min1 > min2) {
			int temp = min1;
			min1 = min2;
			min2 = temp;

			index1 = 1;
			index2 = 0;
		}

		for (int i = 2; i < factors.size(); i++) {
			if (factors.get(i).size() < min1) {
				int temp = min1;
				min1 = factors.get(i).size();
				min2 = temp;
				index2 = index1;
				index1 = i;
			} else if (factors.get(i).size() < min2) {
				min2 = factors.get(i).size();
				index2 = i;
			}
		}

		int[] ans = {index1, index2};
		return ans;

	}

	private boolean isEvidence(String varName) {
		for (String[] currentEvidence : _query.get_conditions()) {
			if(currentEvidence[0].equals(varName))
				return true;
		}
		return false;
	}

	private ArrayList<String> getQueryVarNames() {
		ArrayList<String> rtn = new ArrayList<String>();
		rtn.add(_query.get_var());
		for (String[] string : _query.get_conditions()) {
			rtn.add(string[0]);
		}
		return rtn;
	}

	public Query get_query() {
		return _query;
	}

	public BayesianNetwork get_bn() {
		return _bn;
	}
	
	
}
