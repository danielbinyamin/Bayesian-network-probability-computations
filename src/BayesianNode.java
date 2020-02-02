import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class represents a single Node in a Bayesian network.
 * @author daniel
 *
 */
public class BayesianNode implements Comparable<BayesianNode> {

	private String _name;
	private String[] _values;
	private HashMap<String ,BayesianNode> _parents;
	private CPT _cpt;

	public BayesianNode(BayesianNode other) {
		_name = other._name;
		_values = Arrays.copyOf(other._values, other._values.length);
		_parents = new HashMap<String, BayesianNode>(other._parents);
		_cpt = new CPT(other._cpt);
	}

	public BayesianNode(String name,String[] values ,HashMap<String ,BayesianNode> parents, CPT cpt) {
		_parents = new HashMap<String ,BayesianNode>(parents);
		_values = Arrays.copyOf(values, values.length);
		_cpt = new CPT(cpt);
		_name = new String(name);
	}

	public BayesianNode(String name, String[] values, CPT cpt) {
		_parents = new HashMap<String ,BayesianNode>();
		_values = Arrays.copyOf(values, values.length);
		_cpt = new CPT(cpt);
		_name = new String(name);
	}

	public BayesianNode(String name) {
		_name = name;
		_parents = new HashMap<String ,BayesianNode>();
		_values = new String[0];
		_cpt = new CPT(name);
	}

	/**
	 * Add parent to node
	 * @param parent
	 */
	public void addParent(BayesianNode parent) {
		_parents.put(parent._name, parent);
	}

	public boolean hasNoParents() {
		return _parents.isEmpty();
	}

	/**
	 * returns true if other is a parents of this
	 * @param other
	 * @return
	 */
	public boolean isParent(BayesianNode other) {
		for (BayesianNode currParent : _parents.values()) {
			if(currParent.equals(other))
				return true;
		}
		return false;
	}

	/**
	 * This function creates a list as follows:
	 * {{_name, value1}, {_name, value2}...}
	 * @return
	 */
	public ArrayList<String[]> createListOfValuesAndNames() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] currPair;
		for (int i = 0; i < _values.length; i++) {
			currPair = new String[2];
			currPair[0] = _name;
			currPair[1] = _values[i];
			list.add(currPair);
		}
		
		return list;
	}
	
	/**
	 * This function returns a hash map of (parents[i], bayesianNode(i))
	 * @param parents
	 * @return
	 */
	public static HashMap<String ,BayesianNode> createMapOfPerantNames(String[] parents) {
		HashMap<String ,BayesianNode> ans = new HashMap<String ,BayesianNode>();
		for (int i = 0; i < parents.length; i++) {
			ans.put(parents[i], new BayesianNode(parents[i]));
		}
		return ans;
	}

	/**
	 * updates parent name
	 * @param parentName
	 * @param parent
	 */
	public void updateParent(String parentName, BayesianNode parent) {
		_parents.replace(parentName, parent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BayesianNode other = (BayesianNode) obj;
		if (_cpt == null) {
			if (other._cpt != null)
				return false;
		} else if (!_cpt.equals(other._cpt))
			return false;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_parents == null) {
			if (other._parents != null)
				return false;
		} else if (!_parents.equals(other._parents))
			return false;
		if (!Arrays.equals(_values, other._values))
			return false;
		return true;
	}

	//--getters
	public HashMap<String, BayesianNode> get_parents() {
		return _parents;
	}

	public String get_name() {
		return _name;
	}

	public CPT get_cpt() {
		return _cpt;
	}

	public String[] get_values() {
		return _values;
	}
	
	public int getNumOfValues() {
		return _values.length;
	}

	@Override
	public String toString() {
		String ans = "Node "+_name+"\n";
		ans+= "values: " + Arrays.toString(_values) + "\n";
		ans+= "parents: ";
		for (BayesianNode bayesianNode : _parents.values()) {
			ans+=bayesianNode._name+", ";
		}
		ans = ans.substring(0, ans.length()-2);
		ans+="\n";
		ans+=_cpt.toString();
		return ans;		
	}

	@Override
	public int compareTo(BayesianNode other) {
		
		return this.get_name().compareTo(other.get_name());
		
	}	

}
