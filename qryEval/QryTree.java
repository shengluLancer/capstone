import java.io.IOException;

public class QryTree {
	
	Qryop root;
	public QryTree(String query) throws IOException{
		parse(query);
	}
	
	/**
	 * start to parse prefix query language into a tree
	 * @param query prefix query language
	 * @throws IOException 
	 */
    public void parse(String query) throws IOException{
    	int firstIndex = query.indexOf("#");
    	int secondIndex = 0;
    	/* query has no explicit query operator */
    	if(firstIndex == -1){
    	   root = QryopFactory.getDefaultop(EngineArguments.retrievalAlgorithm);
    	   /* just put all items as the children of root */
    	   storeChildren(query, root);
    	}
    	else{
    		secondIndex = query.indexOf( "(", firstIndex);
    		root = QryopFactory.getQryop(query.substring(firstIndex, 
    				secondIndex).trim());
    		int lastIndex = query.lastIndexOf(")");
    		storeChildren(query.substring(secondIndex + 1, lastIndex).trim(), root);
    	}
    }
    
    public void storeChildren(String subquery, Qryop father) throws IOException{
    	if(subquery.length() == 0) return;
    	int firstIndex = subquery.indexOf("#");
    	/* base case, no boolean operator left in the query*/
    	if(firstIndex == -1){
    		String[] terms = QryEval.tokenizeQuery(subquery);
    		if(!checkWeight(terms)) {
    			storeTermsWithoutWeight(terms, father);
    		}
    		else {
    			storeTermsWithWeight(terms, father);
    		}		
    	}
    	/* has boolean operator left in the query */
    	else{
    		Qryop op;
    		int secondIndex = subquery.indexOf("(", firstIndex);
    		int lastIndex = findNextMatch(secondIndex, subquery);
    		/* if no weight before operator */
    		if(getWeightIndex(firstIndex, subquery) == -1){
    		  storeChildren(subquery.substring(0, firstIndex).trim(), father);
    		  op = QryopFactory.getQryop(subquery.substring(firstIndex, secondIndex).trim());
    		}
    		/* if there is a weight before operator */
    		else{
    		   int weightIndex = getWeightIndex(firstIndex, subquery);
    		   storeChildren(subquery.substring(0, weightIndex).trim(), father);
    		   double d = Double.parseDouble(subquery.substring(weightIndex, firstIndex));
    		   op = QryopFactory.getQryop(subquery.substring(firstIndex, secondIndex).trim(), d);
    		}
    				
    		father.args.add(op);
    		storeChildren(subquery.substring(secondIndex + 1, lastIndex).trim(), op);		
    		storeChildren(subquery.substring(lastIndex + 1, subquery.length()).trim(), father);
    	}
    }
    
    private boolean checkWeight(String[] terms){
    	if(terms.length % 2 == 1) return false;
    	for(int i = 0; i < terms.length; i += 2){
    		if(!isNumeric(terms[i])) return false;
    	}
    	return true;
    }
    private void storeTermsWithWeight(String[] terms, Qryop father) throws IOException{
    	    for(int i = 1; i < terms.length; i += 2){
    	    	double weight = Double.parseDouble(terms[i - 1]);
    	    	int fieldIndex = terms[i].indexOf(".");
    	    	if(fieldIndex != -1){
    	    	   if(QryEval.tokenizeQuery(terms[i].substring(0, fieldIndex)).length < 1) continue;
    	    	   String newTerm = QryEval.tokenizeQuery(terms[i].substring(0, fieldIndex))[0];
     			   String field = terms[i].substring(fieldIndex + 1);
     			   terms[i] = newTerm + "." + field;
    	    	}
    	    	if(terms[i].trim().length() != 0)
    	    	   father.args.add(QryopFactory.getQryop(terms[i].trim(), weight));
    	    }
    }
    
    private void storeTermsWithoutWeight(String[] terms, Qryop father) throws IOException{
    	   for(int i = 0; i < terms.length; i ++){
    		   int fieldIndex = terms[i].indexOf(".");
    		   if(fieldIndex != -1){
    			   if(QryEval.tokenizeQuery(terms[i].substring(0, fieldIndex)).length < 1) continue;
    			   String newTerm = QryEval.tokenizeQuery(terms[i].substring(0, fieldIndex))[0];
    			   String field = terms[i].substring(fieldIndex + 1);
    			   terms[i] = newTerm + "." + field;
    		   } 
    		   if(terms[i].trim().length() != 0)
    		       father.args.add(QryopFactory.getQryop(terms[i].trim()));
    	   }
    }
    
    private int getWeightIndex(int firstIndex, String query){
    	String subQuery = query.substring(0, firstIndex).trim();
    	String[] terms = subQuery.split("\\s");
    	int result = -1;
    	if(terms.length == 0) return -1;
    	else{
    		String last = terms[terms.length - 1];
    		if(isNumeric(last)){
    			try{
    				Double.parseDouble(last); 
    			    result = subQuery.lastIndexOf(last);
    			}
    			catch(NumberFormatException nfe)  
    			{  
    			  return -1;  
    			}  
    		}
    	}
    	return result;
    }
    
    private boolean isNumeric(String str){
    	return str.matches("-?\\d+(\\.\\d+)?");
    }
    private int findNextMatch(int start, String str){
    	int num = 1;
    	for(int i = start + 1; i < str.length(); i ++){
    		if(str.charAt(i) == '(') num ++;
    		else if(str.charAt(i) == ')') num --;
    		if(num == 0) return i;
    	}
    
    	return str.length();
    }
    
    /* for testing perpose */
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	DFS(str, root);
    	return str.toString();
    }
    
    private void DFS(StringBuilder str, Qryop root){
    	/* means it is a term */
    	if(root.args.size() == 0){
    		str.append(root.name + "(" + root.weight + ") ");
    	}
    	else{
    		str.append("( ");
    		boolean first = true;
    		for(Qryop op : root.args){
    		   if(!first) str.append(root.name + "(" + root.weight + ") ");
    		   if(first) first = false;
    		   DFS(str, op);
    		}
    		str.append(")" + " ");
    	}
    }
}
