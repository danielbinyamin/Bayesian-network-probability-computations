import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is the implementation of algorithm 3. 
 * Here we decide on the order of elimination
 * @author daniel
 *
 */
public class Algorithm3 extends Algorithm2 {

	public Algorithm3(BayesianNetwork bn, Query query) {
		super(bn, query);
	}

	/**
	 * This functions sorts varNames by the amount of children each node has
	 */
	@Override
	public void sortVarsToEliminate(ArrayList<String> varNames) {
		HashMap<String, Integer> childrenCounter = new HashMap<String, Integer>();
		int counter;
		//iterate over nodes in network
		for (String name : varNames) {
			counter = 0;
			for (BayesianNode bn2 : get_bn().get_baysianNodes()) {
				if(varNames.equals(bn2.get_name()))
					continue;
				if(bn2.isParent(get_bn().getBayesianNode(name)))
					counter++;
			}
			childrenCounter.put(name, counter);
		}

		HashMap<String, Integer> nodesSortedByChildren = sortByValue(childrenCounter);
		varNames.clear();
		for (Map.Entry<String, Integer> en : nodesSortedByChildren.entrySet()) {
			varNames.add(en.getKey());
		}
	}
	
	//a function to sort hashmap by value
	private HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) { 
		// Create a list from elements of HashMap 
		List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(hm.entrySet()); 

		// Sort the list 
		Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
			public int compare(Map.Entry<String, Integer> o1,  
					Map.Entry<String, Integer> o2) 
			{ 
				return (o1.getValue()).compareTo(o2.getValue()); 
			} 
		}); 

		// put data from sorted list to hashmap  
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
		for (Map.Entry<String, Integer> aa : list) { 
			temp.put(aa.getKey(), aa.getValue()); 
		} 
		return temp; 
	}

}
