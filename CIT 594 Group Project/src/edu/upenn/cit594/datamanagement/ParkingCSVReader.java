package edu.upenn.cit594.datamanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.logging.Logger;

public class ParkingCSVReader extends ParkingViolationReader {
	private BufferedReader buffReader;
	
	public ParkingCSVReader(String fileName) {
		
		super(fileName);
		
		buffReader = new BufferedReader(fileReader);
		
	}

	@Override
	public HashMap<String, List<ParkingViolation>> parseParkingViolations() {
		
		HashMap<String, List<ParkingViolation>> ret = new HashMap<>();
		
		String line = "";
		
		try {
			while ((line = buffReader.readLine()) != null) {
				
				String[] wordsInLine = line.split(",", -1);
				
				int fine = 0;
				
				try {
					fine = Integer.parseInt(wordsInLine[1]);
				
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
				String state = wordsInLine[4];
				
				String zipcode = wordsInLine[6];
				
				
				ParkingViolation violationToAdd = new ParkingViolation(fine, state, zipcode);
				
				if(!ret.containsKey(zipcode)) {
					
					ArrayList<ParkingViolation> violations = new ArrayList<>();
					violations.add(violationToAdd);
					ret.put(zipcode, violations);
				}
				else {
					ret.get(zipcode).add(violationToAdd);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error parsing line '" + line + "'.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error parsing line '" + line + "'.");
			e.printStackTrace();
		}
		
		
		return ret;
	
	}

}
