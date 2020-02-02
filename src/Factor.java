import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class represents a factor for the variable elimination.
 * it represents the matrix of the table.
 * @author daniel
 *
 */
public class Factor {

	private ArrayList<String> _factorNames;
	private ArrayList<ArrayList<String>> _table;
	private int _numOfSums = 0;
	private int _numOfProducts = 0;

	public Factor(ArrayList<String> factorNames) {
		_factorNames = new ArrayList<String>(factorNames);
		_table = new ArrayList<ArrayList<String>>();
	}

	public Factor() {
		_factorNames = new ArrayList<String>();
		_table = new ArrayList<ArrayList<String>>();
	}

	/**
	 * This function returns a joined factor with other and this
	 * @param other Factor
	 */
	public void join(Factor other) {

		ArrayList<String> commonVarNames = getIntersectList(_factorNames, other._factorNames);
		ArrayList<String> oldFactors = new ArrayList<String>(_factorNames);
		ArrayList<String> newFactorsForTable = getUnionList(_factorNames, other._factorNames);
		ArrayList<ArrayList<String>> newTable =  new ArrayList<ArrayList<String>>();
		ArrayList<String> newRowToAdd;
		String rowValue;
		for (ArrayList<String> row : _table) {
			for (ArrayList<String> rowOther : other._table) {
				//if this multiplication needs to be made
				if(isListOfVarValueEqual(other,row, rowOther, commonVarNames)) {
					newRowToAdd = new ArrayList<String>();
					for (String nameOfCollumn : newFactorsForTable) {
						if(other.isFactor(nameOfCollumn))
							newRowToAdd.add(rowOther.get(other.getColumnIndex(nameOfCollumn)));
						else
							newRowToAdd.add(row.get(getCollumnIndex(nameOfCollumn, oldFactors)));
					}
					_numOfProducts++;
					rowValue = String.valueOf(Double.parseDouble(row.get(row.size()-1)) * Double.parseDouble(rowOther.get(rowOther.size()-1)));
					newRowToAdd.add(rowValue);
					newTable.add(newRowToAdd);
				}
			}
		}
		_factorNames = new ArrayList<String>(newFactorsForTable);
		_table = new ArrayList<ArrayList<String>>(newTable);
	}

	/**
	 * This function sums out varName
	 * @param varName
	 */
	public void sumOut(String varName) {

		ArrayList<String> factorsWithoutVarName = new ArrayList<String>(_factorNames);
		factorsWithoutVarName.remove(varName);
		ArrayList<ArrayList<String>> newTable = new ArrayList<ArrayList<String>>();

		ArrayList<String> newRow;
		boolean newRowToBeAdded;
		double value;
		boolean addedToValueAlready = false;
		//iterate over each row over other rows
		for (ArrayList<String> row1 : _table) {
			newRowToBeAdded = false;
			value = Double.parseDouble(row1.get(row1.size()-1));
			for (ArrayList<String> row2 : _table) {
				if(row1.equals(row2))
					continue;
				//if row1 and row2 are equal values without varName
				if(isListOfVarValueEqual(row1, row2, factorsWithoutVarName)) {
					//if the new table does not have already this row
					if(!tableContainsRow(newTable,listWithoutFactorValue(row1, varName))) {
						newRowToBeAdded = true;
						_numOfSums++;
						value += Double.parseDouble(row2.get(row2.size()-1)); 
					}
				}
			}
			//if a new row needs to be created
			if(newRowToBeAdded) {
				newRow = new ArrayList<String>(listWithoutFactorValue(row1, varName));
				newRow.remove(newRow.size()-1);
				newRow.add(Double.toString(value));
				newTable.add(newRow);
			}
		}

		//update factor with new table and factors
		_factorNames = new ArrayList<String>(factorsWithoutVarName);
		_table = new ArrayList<ArrayList<String>>(newTable);	
	}

	/**
	 * This function normelizes the factor assuming it only has one factorName
	 * @throws Exception 
	 */
	public void normalize() throws Exception {
		if(_factorNames.size() > 1)
			throw new Exception("Cannot normelize factor. more then one factorName");
		double alpha;
		double sum = 0.0;
		for (ArrayList<String> row : _table) {
			sum += Double.parseDouble(row.get(row.size()-1));
			_numOfSums++;
		}
		_numOfSums--;
		alpha = 1.0/sum;
		double numToSet;
		for (ArrayList<String> row : _table) {
			numToSet = Double.parseDouble(row.get(row.size()-1)) * alpha;
			row.set(row.size()-1, Double.toString(numToSet));
		}

	}

	/**
	 * returns the answer assuming there is only one factor in the table
	 * @param factorName
	 * @param value
	 * @return -1 if value not found in table
	 * @throws Exception 
	 */
	public double getAnswer(String value) throws Exception {
		if(_factorNames.size() > 1)
			throw new Exception("Cannot get Answer. more then one factorName");
		for (ArrayList<String> row : _table) {
			if(row.get(0).equals(value))
				return Double.parseDouble(row.get(1));
		}
		return -1;
	}

	/**
	 * This function adds a name to the factor name list
	 * @param name
	 */
	public void addFactorName(String name) {
		_factorNames.add(name);
	}

	/**
	 * Change the factor names list to a given one(the header of the table)
	 * @param factorNames
	 */
	public void changeFactorNamesList(ArrayList<String> factorNames) {
		_factorNames = new ArrayList<String>(factorNames);
	}

	/**
	 * Add a row to the factor table
	 * @param row
	 */
	public void addRow(ArrayList<String> row) {
		_table.add(new ArrayList<String>(row));
	}

	/**
	 * returns true if factor contains name
	 * @param name
	 * @return
	 */
	public boolean containsFactorName(String name) {
		return _factorNames.contains(name);
	}

	public int getNumOfSumsAndZero() {
		int rtn = _numOfSums;
		_numOfSums = 0;
		return rtn;
	}

	public int getNumOfProductsAndZero() {
		int rtn = _numOfProducts;
		_numOfProducts = 0;
		return rtn;
	}

	public boolean noNameFactor() {
		return _factorNames.isEmpty();
	}

	public int size() {
		return _table.size();
	}


	//---------private functions-----------

	public int getColumnIndex(String varName) {
		return _factorNames.indexOf(varName);
	}

	//returns true if table contains a given row(only values of factors without the P)
	private boolean tableContainsRow(ArrayList<ArrayList<String>> table, ArrayList<String> row) {

		for (ArrayList<String> tableRow : table) {
			//if rows are equal without value
			if(tableRow.subList(0, tableRow.size()-1).equals(row.subList(0, tableRow.size()-1)))
				return true;
		}
		return false;
	}

	//returns the row without the value of varName

	private ArrayList<String> listWithoutFactorValue(ArrayList<String> row, String varName) {
		ArrayList<String> ans = new ArrayList<String>(row);
		ans.remove(getColumnIndex(varName));
		return ans;
	}


	//get index of the column of varName

	//get index of column from factorsNames

	private int getCollumnIndex(String varName, ArrayList<String> factorNames) {
		return factorNames.indexOf(varName);
	}

	//function returns true if given common varNames values are equal in two rows

	private boolean isListOfVarValueEqual(ArrayList<String> row1, ArrayList<String> row2, ArrayList<String> varNames) {

		for (String name : varNames) {
			if(!row1.get(getColumnIndex(name)).equals(row2.get(getColumnIndex(name))))
				return false;
		}
		return true;
	}

	//function returns true if given common varNames values are equal in two rows

	private boolean isListOfVarValueEqual(Factor other, ArrayList<String> row1, ArrayList<String> rowOther, ArrayList<String> varNames) {

		for (String name : varNames) {
			if(!row1.get(getColumnIndex(name)).equals(rowOther.get(other.getColumnIndex(name))))
				return false;
		}
		return true;
	}

	//returns joined list without duplicates

	private ArrayList<String> getUnionList(ArrayList<String> list1, ArrayList<String> list2) {
		Set<String> ansInSet = new HashSet<String>();
		ansInSet.addAll(list1);
		ansInSet.addAll(list2);
		return new ArrayList<String>(ansInSet);
	}

	//return intersection of two lists

	private ArrayList<String> getIntersectList(ArrayList<String> list1, ArrayList<String> list2) {
		ArrayList<String> ans = new ArrayList<String>();
		for (String string : list1) {
			if(list2.contains(string))
				ans.add(string);
		}
		return ans;
	}

	//returns true if varName is a factor

	private boolean isFactor(String varName) {
		return _factorNames.contains(varName);
	}

	

	@Override
	public String toString() {
		String ans = "";
		ans += "f(";
		for (String string : _factorNames) {
			ans += string+", ";
		}
		ans = ans.substring(0, ans.length()-2)+")" + "\n";
		for (ArrayList<String> row : _table) {
			ans+=row.toString()+"\n";
		}

		return ans;

	}



}
