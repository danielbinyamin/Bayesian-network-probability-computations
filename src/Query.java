import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents a query from the form:
 * (_var = _valueToCalc | _condtions.get(0)[0] = _condtions.get(0)[1],  _condtions.get(1)[0] = _condtions.get(1)[1], ...)
 * @author daniel
 *
 */
public class Query {

	private String _var;
	private String _valueToCalc;
	private ArrayList<String[]> _conditions;
	
	public Query(Query other) {
		_var = other._var;
		_valueToCalc = other._valueToCalc;
		_conditions = new ArrayList<String[]>(other._conditions);
	}
	
	public Query(String var, String valueToCalc, ArrayList<String[]> conditions) {
		_var = new String(var);
		_valueToCalc = new String(valueToCalc);
		_conditions = new ArrayList<String[]>(conditions);
	}
	
	public Query(String var, String valueToCalc) {
		_var = new String(var);
		_valueToCalc = new String(valueToCalc);
		_conditions = new ArrayList<String[]>();
	}

	public void addCondition(String[] condition) {
		_conditions.add(condition);
	}
	
	public void addConditions(ArrayList<String[]> conditions) {
		_conditions.addAll(conditions);
	}
	
	/**
	 * Returns value of a given condition. "" if not found
	 * @param conditionName
	 * @return
	 */
	public String getConditionValue(String conditionName) {
		for (String[] strings : _conditions) {
			if (strings[0].equals(conditionName))
				return strings[1];
		}
		return "";
	}
	
	/**
	 * Returns value of given condition in query.
	 * @param varName
	 * @return: value of condition. "" if not found
	 */
	public String valueOfConditionFromQuery(String varName) {
		for (String[] strings : _conditions) {
			if(strings[0].equals(varName))
				return strings[1];
		}
		return "";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if(!(_conditions == null)) {
			for (String[] strings : _conditions) {
				result+=Arrays.hashCode(strings);
			}
		}
		result = prime * result + ((_valueToCalc == null) ? 0 : _valueToCalc.hashCode());
		result = prime * result + ((_var == null) ? 0 : _var.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (_conditions == null) {
			if (other._conditions != null)
				return false;
		} else {
			boolean falseFlag = false;
			if(_conditions.size() != other._conditions.size())
				return false;
			for (String[] strings : _conditions) {
				for (String[] strings2 : other._conditions) {
					if(Arrays.equals(strings, strings2))
						falseFlag = true;
				}
				if(!falseFlag)
					return false;
				falseFlag = false;
			}
		}
		if (_valueToCalc == null) {
			if (other._valueToCalc != null)
				return false;
		} else if (!_valueToCalc.equals(other._valueToCalc))
			return false;
		if (_var == null) {
			if (other._var != null)
				return false;
		} else if (!_var.equals(other._var))
			return false;
		return true;
	}

	//------getters--------
	public String get_var() {
		return _var;
	}
	
	public String get_valueToCalc() {
		return _valueToCalc;
	}

	public ArrayList<String[]> get_conditions() {
		return _conditions;
	}
	
	@Override
	public String toString() {
		String ans = _var+"="+_valueToCalc+" | ";
		for (String[] strings : _conditions) 
			ans+=strings[0]+"="+strings[1]+", ";
		ans = ans.substring(0, ans.length()-2);
		return ans;
	}
	
	
	
	
}
