/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreList {

  /**
   * A little utilty class to create a <docid, score> object.
   */
  protected class ScoreListEntry implements Comparable<ScoreListEntry> {
    private int docid;
    private float score;

    private ScoreListEntry(int docid, float score) {
      this.docid = docid;
      this.score = score;
    }
    
    public void setScore(float s){
        this.score = s;
    }
    
    public int getDocId(){
    	return docid;
    }
    public float getScore(){
    	return score;
    }

	@Override
	public int compareTo(ScoreListEntry o) {
		if(o.score > this.score) return 1;
		else if(o.score == this.score){
			if(o.docid > this.docid) return -1;
			else return 1;
		}
		else return -1;
	}
	
	public String toString(){
		return "docId: " + docid + " score: " + score;
	}
  }

  List<ScoreListEntry> scores = new ArrayList<ScoreListEntry>();
  float defaultScore = 0;

  /**
   * Append a document score to a score list.
   */
  public void add(int docid, float score) {
    scores.add(new ScoreListEntry(docid, score));
  }

  public void add(int index, int docid, float score){
	scores.add(index,new ScoreListEntry(docid, score));
  }
  public int getDocid(int n) {
    return this.scores.get(n).docid;
  }

  public float getDocidScore(int n) {
    return this.scores.get(n).score;
  }
  
  public void setDocidScore(int n, float score){
	  this.scores.get(n).score = score;
  }

  public List<ScoreListEntry> getFinalScoreList(){
	  List<ScoreListEntry> result = new ArrayList<ScoreListEntry>(scores);
	  Collections.sort(result);
	  return result;
  }
  public String toString(){
	  StringBuilder str = new StringBuilder();
	  int max = 100;
	  int start = 0;
	  for(ScoreListEntry entry : scores){
		  if(start ++ < max)
		  str.append("docid: " + entry.docid + "; score: " + entry.score + "\n");
		  else break;
	  }
	  return str.toString();
  }
}
