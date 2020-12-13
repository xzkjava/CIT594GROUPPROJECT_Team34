package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.upenn.cit594.data.ParkingViolation;

public class ParkingJSONReader extends ParkingViolationReader{

	public ParkingJSONReader(String fileName) {
		super(fileName);
		
	}

	@Override
	public HashMap<String, List<ParkingViolation>> parseParkingViolations() {

		HashMap<String, List<ParkingViolation>> ret = new HashMap<>();
		
		JSONParser parser = new JSONParser();
		
		try {
			
			JSONArray violations = (JSONArray)parser.parse(fileReader);
			
			Iterator itr = violations.iterator();
		   
			while (itr.hasNext()) {
		    	
				JSONObject violation = (JSONObject) itr.next();
		    	 
				// extract state, zipcode, and fine from JSONObject
				
		    	int fine = 0;
		    	
		    	String fineStr = violation.get("fine").toString();
		    	
		    	if(!fineStr.matches("^[+-]?\\d+$")) {
		    		continue;
		    	}
		    	
		    	try {
		    		 fine = Integer.parseInt(violation.get("fine").toString());
		    	} catch(NumberFormatException e) {
		    		 e.printStackTrace();
		    	}
		    	
		    	String  state = violation.get("state").toString();
		    	 
		    	String zipcode = violation.get("zip_code").toString();
		    	
		    	// create new ParkingViolation with extracted information
		    	 
		    	ParkingViolation violationToAdd = new ParkingViolation(fine, state, zipcode);
		    	
		    	// add this ParkingViolation to ParkingViolations with this given zipcode
		    	if (!ret.containsKey(zipcode)) {
		    		 
		    		 ArrayList<ParkingViolation> violationList = new ArrayList<>();
		    		 violationList.add(violationToAdd);
		    		 ret.put(zipcode, violationList);
		    		 
		    	} else {
		    		 // no other violations with this zipcode, create new map for zipcode
		    		 ret.get(zipcode).add(violationToAdd);
		    		 
		    	 }
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		return ret;		// return parsed result
	
	
	}

}
