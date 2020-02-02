import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class represents a CPT table of a given var in a Bayesian Network.
 * @author danie
 *
 */
public class CPT {

	private String _varName;
	private HashMap<Query, Double> _cpt;

	public CPT(String varName) {
		_varName = varName;
		_cpt = new HashMap<Query, Double>();
	}

	public CPT(CPT other) {
		_varName = other._varName;
		_cpt = new HashMap<Query, Double>(other._cpt);
	}

	public void addQueryEqulas(String valueToCalc, ArrayList<String[]> conditions, double equals) {
		Query q = new Query(_varName, valueToCalc, conditions);
		_cpt.put(q, equals);
	}

	/**
	 * Return the answer to a given query in the CPT. 
	 * Returns -1 if query is not found.
	 * @param q
	 * @return
	 */
	public double P(Query q){
		if(!_cpt.containsKey(q))
			return -1.0;
		return _cpt.get(q);
	}

	/**
	 * This function returns the answer from a given broken down query
	 * @param valueToCalc
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public double P(String valueToCalc, ArrayList<String[]> conditions) throws Exception {
		Query q = new Query(_varName, valueToCalc, conditions);
		if(!_cpt.containsKey(q))
			throw new Exception(q + "does not appear in the CPT of" + _varName);
		return _cpt.get(q);
	}

	/**
	 * This function returns the sum of probablities of a given row in the CPT(should be 1 when CPT is complete)
	 * @param conditions
	 * @return
	 */
	public double sumProb(ArrayList<String[]> conditions) {
		BigDecimal sum = new BigDecimal(0.0);
		for(Entry<Query, Double> entry : _cpt.entrySet()) 
			if(entry.getKey().get_conditions().equals(conditions))
				sum = sum.add(BigDecimal.valueOf(entry.getValue()));
		return sum.doubleValue();
	}

	/**
	 * a function which return a list of the vars in a cpt (query and evidence vars)
	 * @return
	 */
	public ArrayList<String> getVarsFromCPT() {
		//get first query in cpt
		Query q = _cpt.keySet().iterator().next();

		ArrayList<String> vars = new ArrayList<String>();
		vars.add(q.get_var());
		for (String[] evidence : q.get_conditions()) 
			vars.add(evidence[0]);
		return vars;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CPT other = (CPT) obj;
		if (_cpt == null) {
			if (other._cpt != null)
				return false;
		} else if (!_cpt.equals(other._cpt))
			return false;
		if (_varName == null) {
			if (other._varName != null)
				return false;
		} else if (!_varName.equals(other._varName))
			return false;
		return true;
	}

	public HashMap<Query, Double> get_cpt() {
		return _cpt;
	}

	/**
	 * Returns number of ALL queries in CPT(including the complement)
	 * @return size of CPT
	 */
	public int size() {
		return _cpt.size();
	}

	@Override
	public String toString() {
		String ans = "CPT:\n";
		for(Entry<Query, Double> entry : _cpt.entrySet()) {
			ans+="P(";
			ans+=entry.getKey().toString();
			ans+=")"+" = " + entry.getValue() + "\n";
		}
		return ans;	
	}

	public String get_varName() {
		return _varName;
	}
	
}
