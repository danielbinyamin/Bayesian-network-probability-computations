import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represnets a Bayesian Network. It holds a list of Bayesian Nodes.
 * @author daniel
 *
 */
public class BayesianNetwork {

	private ArrayList<BayesianNode> _baysianNodes;

	public BayesianNetwork() {	
		_baysianNodes = new ArrayList<BayesianNode>();
	}

	public BayesianNetwork(BayesianNetwork other) {
		_baysianNodes = new ArrayList<BayesianNode>(other._baysianNodes);
	}

	/**
	 * This function build the Bayesian network from a given text file
	 * @param textFilePath
	 * @throws IOException
	 */
	public void buildBayesianNetworkFromTextFile(String textFilePath) throws IOException {
		TextParser tx = new TextParser(textFilePath);
		BayesianNode currNode;

		//add nodes from text file to network. no parent connection yet.
		while(true) {
			try {
				currNode = tx.getNextBayesianNodeWithoutParents();
				_baysianNodes.add(currNode);
			}
			catch (IOException ioException) {
				System.out.println("error reading baysianNode from text file\n");
				throw new IOException(ioException);
			}
			catch (Exception endOfVarsException) {
				break;
			}
		}

		updateParentsConnections();


	}

	/**
	 * This function returns the answer of a given InputQuery
	 * @param q
	 * @return
	 */
	public Answer calcQuery(InputQuery q) {
		Calculate c = null;

		switch (q.get_algorithmNum()) {
		case 1:
			c = new Algorithm1(this, q);
			break;
		case 2: 
			c = new Algorithm2(this, q);
			break;
		case 3: 
			c = new Algorithm3(this, q);
			break;
		default: 
			break;
		}
		Answer ans = null;
		try {
			ans = c.calc();
		} catch (NullPointerException ex) {
			System.out.println("algorithm number not recognized. out of range [1,2,3]. ");
			ex.printStackTrace();
		}

		return ans;
	}

	public ArrayList<BayesianNode> getParents(String varName) {
		BayesianNode varObject = getBayesianNode(varName);
		ArrayList<BayesianNode> ans = new ArrayList<BayesianNode>();
		for (BayesianNode bayesianNode : _baysianNodes) {
			if(varObject.isParent(bayesianNode))
				ans.add(bayesianNode);
		}
		return ans;
	}

	public ArrayList<BayesianNode> getParents(BayesianNode var) {
		ArrayList<BayesianNode> ans = new ArrayList<BayesianNode>();
		for (BayesianNode bayesianNode : _baysianNodes) {
			if(var.isParent(bayesianNode))
				ans.add(bayesianNode);
		}
		return ans;
	}

	/**
	 * A function which returns the BayesianNode of a string.
	 * returns null if not found
	 * @param varName
	 * @return
	 */
	public BayesianNode	getBayesianNode(String varName) {
		for (BayesianNode bayesianNode : _baysianNodes) {
			if(bayesianNode.get_name().equals(varName))
				return bayesianNode;
		}
		return null;
	}

	/**
	 * get names of all the nodes in the network
	 * @return
	 */
	public String[] getNamesOfVars() {
		String[] names = new String[_baysianNodes.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = _baysianNodes.get(i).get_name();
		}
		return names;
	}
	
	//links up all the nodes
	private void updateParentsConnections() {
		for (BayesianNode bayesianNode : _baysianNodes) {
			if(bayesianNode.hasNoParents())
				continue;

			for (String currentParent : bayesianNode.get_parents().keySet()) 
				for (BayesianNode otherbayesianNode : _baysianNodes) 
					if(otherbayesianNode.get_name().equals(currentParent))
						bayesianNode.updateParent(currentParent, otherbayesianNode);							
		}
	}

	/**
	 * get double answer from the network of a given Query.
	 * returns -1 if not found
	 * @param q
	 * @return
	 */
	public double getPrFromCPT(Query q) {
		double ans = -1.0;

		for (BayesianNode bayesianNode : _baysianNodes) {
			ans = bayesianNode.get_cpt().P(q);
			if(ans!=-1)
				break;
		}
		return ans;
	}

	public ArrayList<BayesianNode> get_baysianNodes() {
		return _baysianNodes;
	}

	public int size() {
		return _baysianNodes.size();
	}
	
	@Override
 	public String toString() {
		String ans = "Bayesian Netwrok: \n\n";
		for (BayesianNode bayesianNode : _baysianNodes) {
			ans+=bayesianNode.toString()+"\n";
		}
		return ans;
	}






}
