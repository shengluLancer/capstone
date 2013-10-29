import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class QryopUW extends Qryop{

	int distance;
	public QryopUW(String str){
		int firstIndex = str.indexOf("/");
		distance = Integer.valueOf(str.substring(firstIndex + 1, str.length()));
		this.name = str;
		this.weight = 1;
	}
	
	public QryopUW(String str, double weight){
		int firstIndex = str.indexOf("/");
		distance = Integer.valueOf(str.substring(firstIndex + 1, str.length()));
		this.name = str;
		this.weight = weight;
	}
	
	@Override
	public QryResult evaluate() throws IOException {
		/* invalid input */
		QryResult result = new QryResult();
		if(args.size() == 0 || args.size() == 1) 
			return new QryResult();
		this.field = args.get(args.size() - 1).field;
        int[] dPointers = new int[args.size()];
        QryResult[] results = new QryResult[args.size()];
        initializeResult(results);
        int[] listLength = new int[args.size()];
        initializeLength(listLength, results);
        
        /* try to match the same document */
        while(checkLengthAvailable(dPointers, listLength)){
        	while(checkLengthAvailable(dPointers, listLength) && 
        			!checkSameDoc(dPointers, results)){
        		int minimumIndex = getMinimumIndex(dPointers, results);
        		dPointers[minimumIndex] ++;
        	}
        	if(!checkLengthAvailable(dPointers, listLength)) break;
        	/* there is a match */
        	else{
        		int docId = results[0].invertedList.postings.get(dPointers[0]).docid;
        		int[] pPointers = new int[args.size()];
        		/* length of position list of the same doc for each term */
        		int[] totalnumbers = new int[args.size()];
        		List<Integer> newPositions = new ArrayList<Integer>();
        		InvList.DocPosting[] postings = new InvList.DocPosting[args.size()];
        		storePositions(totalnumbers, dPointers, results, postings);
        		while(checkLengthAvailable(pPointers, totalnumbers)){
        			int maxPosition = getMaxPosition(pPointers, postings);
        			int minPosition = getMinPosition(pPointers, postings);
        			if(1 + maxPosition - minPosition <= distance){
        			  newPositions.add(minPosition);
        			  /* all pointer adds 1 */
        			  moveNext(pPointers);	
        			}
        			else{
        			  int minIndex = getMinIndex(pPointers, postings);
        			  pPointers[minIndex] ++;
        			}
        		}
        		setResult(result, docId, newPositions);  
        		/*
        		if(docId == 150708){
        			System.out.println("*** final ***"); result.invertedList.print(docId);
        		} */
        		moveNext(dPointers);
        	}
        }
        /*
        results[0].invertedList.print();
        results[1].invertedList.print();
        result.invertedList.print();
        */
        //System.out.println("ctf " + result.invertedList.ctf);
		return result;
	}
	
	private void setResult(QryResult result, int docId, List<Integer> positions){
		if(positions.size() == 0) return;
		result.invertedList.df += 1;
		result.invertedList.ctf += positions.size();
		int[] tempPositions = new int[positions.size()];
		int current = 0;
		for(Integer i : positions) tempPositions[current ++] = i;		
		result.invertedList.addDocPostiong(docId, tempPositions);
	}
	
	private void moveNext(int[] pPointers){
		for(int i = 0 ; i < pPointers.length; i ++){
			pPointers[i] ++;
		}
	}
	
	private int getMaxPosition(int[] pPointers, InvList.DocPosting[] postings){
			int currentPosition = postings[0].positions.get(pPointers[0]);
			for(int i = 1; i < pPointers.length; i ++){
				currentPosition = Math.max(postings[i].positions.get(pPointers[i])
							 ,currentPosition);
			}
			return currentPosition;
	}
	
	private int getMinPosition(int[] pPointers, InvList.DocPosting[] postings){
		int currentPosition = postings[0].positions.get(pPointers[0]);
		for(int i = 1; i < pPointers.length; i ++){
			currentPosition = Math.min(postings[i].positions.get(pPointers[i])
					 ,currentPosition);
		}
		return currentPosition;
	}
	
	private int getMinIndex(int[] pPointers, InvList.DocPosting[] postings){
		int currentPosition = postings[0].positions.get(pPointers[0]);
		int result = 0;
		for(int i = 1; i < pPointers.length; i ++){		
			currentPosition = Math.min(postings[i].positions.get(pPointers[i])
					 ,currentPosition);
			if(currentPosition == postings[i].positions.get(pPointers[i]))
				result = i;
		}
		return result;
	}
	
	private int getMinimumIndex(int[] dPointers, QryResult[] results){
		   int currentMinDoc = results[0].invertedList.postings.get(dPointers[0]).docid;
		   int result = 0;
		   for(int i = 1; i < dPointers.length; i ++){
			   if(results[i].invertedList.postings.get(dPointers[i]).docid
					< currentMinDoc){
				   currentMinDoc = results[i].invertedList.postings.get(dPointers[i]).docid;
				   result = i;
			   }
		   }
		   return result;
	}
	private boolean checkSameDoc(int[] dPointers, QryResult[] results){
		int currentDocId = results[0].invertedList.postings.get(dPointers[0]).docid;
		for(int i = 1; i < dPointers.length; i ++){
			if(results[i].invertedList.postings.get(dPointers[i]).docid 
					!= currentDocId) return false;
		}
		return true;
	}
	
	private boolean checkLengthAvailable(int[] pointers, int[] listLength){
		for(int i = 0; i < pointers.length; i ++){
			if(pointers[i] >= listLength[i]) return false;
		}
		return true;
	}
	private void initializeResult(QryResult[] results) throws IOException{
		int current = 0;
		for(Qryop op : args){
			results[current ++] = op.evaluate();
		}
	}
	
	private void initializeLength(int[] listLength, QryResult[] results){
		int current = 0;
	    for(QryResult res : results){
	    	listLength[current ++] = res.invertedList.postings.size();
	    }	
	}
	
	private void storePositions(int[] totalnumbers, int[] dPointers, 
	    QryResult[] results, InvList.DocPosting[] postings){
		int current = 0;
		for(QryResult res : results){
			totalnumbers[current ] = res.invertedList.postings.get(dPointers[current]).positions.size();
			postings[current] = res.invertedList.postings.get(dPointers[current]);
			current ++;
		}
	}
}
