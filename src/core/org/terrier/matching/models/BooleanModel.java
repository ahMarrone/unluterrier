package org.terrier.matching.models;

import org.terrier.matching.Model;
import java.util.List;
import java.util.ArrayList;

public class BooleanModel{

	public static List<Integer> doAND(List<Integer> t1, List<Integer> t2){
		List<Integer> r = new ArrayList(); // resultset
		// Sort by length. Minor first
		for (Integer doc : t1) {
			if (t2.contains(doc)){
				r.add(doc);
			}
		}
		return r;
	}

	public static List<Integer> doOR(List<Integer> t1, List<Integer> t2){
		List<Integer> r = new ArrayList();
		r.add(1);
		return r;
	}

}