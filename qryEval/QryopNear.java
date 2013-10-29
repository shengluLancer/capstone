import java.io.IOException;
import java.util.Vector;


public class QryopNear extends Qryop{

	
	int distance;
	public QryopNear(String str){
		int firstIndex = str.indexOf("/");
		distance = Integer.valueOf(str.substring(firstIndex + 1, str.length()));
		this.name = str;
		this.weight = 1;
	}
	
	public QryopNear(String str, double weight){
		int firstIndex = str.indexOf("/");
		distance = Integer.valueOf(str.substring(firstIndex + 1, str.length()));
		this.name = str;
		this.weight = weight;
	}
	
	@Override
	public QryResult evaluate() throws IOException {
		if(args.size() == 0 || args.size() == 1) 
			return new QryResult();
		
	    QryResult result = args.get(args.size() - 1).evaluate();
	    this.field = args.get(args.size() - 1).field;
	    // Each pass of the loop evaluates one query argument.
	    for (int i = args.size() - 2; i >= 0; i --) {

	      QryResult iResult = args.get(i).evaluate();
	      int rDoc = 0; /* Index of a document in result. */
	      int iDoc = 0; /* Index of a document in iResult. */

	      while(rDoc < result.invertedList.postings.size()) {
	    	  
	        while((iDoc < iResult.invertedList.postings.size())
	            && (result.invertedList.postings.get(rDoc).docid 
	            	> iResult.invertedList.postings.get(iDoc).docid)) {
	            iDoc++;
	        }
	        int oldtf = result.invertedList.postings.get(rDoc).tf;
	        
	        /* if found the same document */
	        if((iDoc < iResult.invertedList.postings.size())
	            && (result.invertedList.postings.get(rDoc).docid  
	            	== iResult.invertedList.postings.get(iDoc).docid)) {
	            int rPosition = 0;
	            int iPosition = 0;
	            Vector<Integer> rP = result.invertedList.postings.get(rDoc).positions;
	            Vector<Integer> iP = iResult.invertedList.postings.get(iDoc).positions;                
	            while(iPosition < iP.size()){
	            	
	            	while(rPosition < rP.size() 
	            			&& (iP.get(iPosition) > rP.get(rPosition))){
	            		result.invertedList.postings.get(rDoc).tf --;
	            		rP.remove(rPosition);
	            	}
	                if(rPosition == rP.size()) {
	                	break;
	                }
	                /* matches near operator */
	                else if(rP.get(rPosition) - iP.get(iPosition) <= distance){
	                	rP.set(rPosition, iP.get(iPosition));
	                	rPosition ++;
	                	iPosition ++;
	                }
	                else{
	                	iPosition ++;
	                }
	            }
	            /* if there is rPosition left */
	            while(rPosition < rP.size()){
	            	result.invertedList.postings.get(rDoc).tf --;
	            	rP.remove(rPosition);
	            }        
	        	if(rP.size() == 0){
	        		result.invertedList.postings.remove(rDoc);
	        		result.invertedList.ctf -= oldtf;
	        		result.invertedList.df --;
	        		iDoc ++;
	        	}
	        	else{
	        		result.invertedList.ctf += - oldtf + result.invertedList.postings.get(rDoc).tf;
	        		rDoc ++;
	        		iDoc ++;
	        	}
	        } else {
	          result.invertedList.postings.remove(rDoc);
	          result.invertedList.df --;
	          result.invertedList.ctf -=  oldtf;
	        }
	      }
	    }
		return result;
	}
}
