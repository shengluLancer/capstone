/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;

public class QryopTerm extends Qryop {

  private String term;

  /* Constructors */
  public QryopTerm(String t) {
	int indexOfField = t.indexOf(".");
	/* Default field if none is specified */
	if(indexOfField == -1){
		this.field = "body";
		this.term = t;
	}
	else{
		this.term = t.substring(0, indexOfField);
		this.field = t.substring(indexOfField + 1, t.length());
	}  
    this.name = t;
    this.weight = 1;
  }
  
  public QryopTerm(String t, double weight) {
	int indexOfField = t.indexOf(".");
	/* Default field if none is specified */
	if(indexOfField == -1){
		this.field = "body";
		this.term = t;
	}
	else{
		this.term = t.substring(0, indexOfField);
		this.field = t.substring(indexOfField + 1, t.length());
	}  
    this.name = t;
    this.weight = weight;
  }


  public String getTerm(){
	  return term;
  }
  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {
    QryResult result = new QryResult();
    result.invertedList = new InvList(this.term, this.field);
    return result;
  }
}
