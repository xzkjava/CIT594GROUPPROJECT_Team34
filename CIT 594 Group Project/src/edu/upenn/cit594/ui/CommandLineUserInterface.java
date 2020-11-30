package edu.upenn.cit594.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Map.Entry;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.CSVProcessor;
import edu.upenn.cit594.processor.JSONProcessor;
import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.processor.ProcessorFactory;

public class CommandLineUserInterface {
	private Processor processor;
	
	private BufferedReader in;
	
	private Logger logger;
	
	
	public CommandLineUserInterface(Processor processor) {
		
		this.processor = processor;
		
		in = new BufferedReader(new InputStreamReader(System.in));
		
		logger = Logger.getInstance();

	}
	
	
	public void respondToUserInput() {
		
		String input = "";
		
		displayPrompts();
		
		try {
			while((input = in.readLine()) != null) {
				
				if(input.isEmpty()) {
					System.out.println("ERROR: The choice typed in is an empty string. Please enter a choice.");
					System.exit(1);
				}
				else if (!isAllDigits(input)) {
					System.out.println("ERROR: The choice should be numbers only");
					System.exit(1);
				}
				else {
					try{
						int typedChoice = Integer.parseInt(input);
						
						if(typedChoice < 0 || typedChoice > 6) {
							System.out.println("ERROR: the choice should be between 0 and 6, inclusive");
							System.exit(1);
						}
						//needs to ask instructor if an invalid choice needs to be logged.
						logger.log(String.valueOf(System.currentTimeMillis()) + " User selected Choice:" + typedChoice + "\n");
					
						processUserCommand(typedChoice);
					
					}catch(NumberFormatException e) {
						e.printStackTrace();
					}		
					
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//need to add prompt 6
	public void displayPrompts() {
		System.out.println("Enter 0 to exit.\n" 
				+ "Enter 1 to show the total population for all ZIP Codes.\n"
				+ "Enter 2 to show the total fines per capita for each ZIP Code.\n"
				+ "Enter 3 to show the average market value for residences in a specified ZIP Code.\n"
				+ "Enter 4 to show the total average livable area for residences in a specified ZIP Code.\n"
				+ "Enter 5 to show the total residential market value per capita for a specified ZIP Code.\n");
	}
	
	public boolean isAllDigits(String input) {
		
		for(int i = 0; i < input.length(); i++) {
			if(!Character.isDigit(input.charAt(0))) {
				return false;
			}
		}
		
		return true;
	}
	
	public void processUserCommand(int choice) {
		
		String zipcode = "";
		
		switch(choice) {
			case 0:
				System.out.println("0 has been entered. Program terminated.");
				System.exit(0);
			case 1:
				int totalPopulation = processor.calculatePopulation();
				System.out.println(totalPopulation) ;
				displayPrompts();
				break;
			case 2:
				SortedMap<String, Double> finesPerZip = processor.calculateFinePerCapita();
				Iterator<Entry<String, Double>> it = finesPerZip.entrySet().iterator();
				DecimalFormat df = new DecimalFormat("0.0000");
				df.setRoundingMode(RoundingMode.FLOOR);
				while (it.hasNext()) {
					Entry<String, Double> next = it.next();
					System.out.println(next.getKey() + ": " + df.format(next.getValue()));
				}
				displayPrompts();
				break;
			case 3:
				zipcode = askForZipCode();
				System.out.println(processor.calculateValuePerProperty(zipcode));
				displayPrompts();
				break;	
			case 4:
				zipcode = askForZipCode();
				System.out.println(processor.calculateAreaPerProperty(zipcode));
				displayPrompts();
				break;
			case 5:
				zipcode = askForZipCode();
				System.out.println(processor.calculateValuePerCapita(zipcode));
				displayPrompts();
				break;
			case 6:
				//TODO
				displayPrompts();
				break;
		}
	}
	
	public String askForZipCode() {
		System.out.println("Please enter a ZIP Code:");
		String zipcode = "";
		boolean keepAsking = true;
		
		do{
			try{
				zipcode = in.readLine();
				if(zipcode != null) {
					if(!isAllDigits(zipcode)) {
						System.out.println("ERROR: the zipcode entered should be all digits.\nPlease enter a zipcode.");
						
					}
					else {
						keepAsking = false;
					}
			    }
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}while(keepAsking);
			
		//need to ask instructor if the invalid zipcode needs to be logged.
		logger.log(String.valueOf(System.currentTimeMillis()) + " User selected ZIP Code:" + zipcode + "\n");
		
		return zipcode;
		
	}
	
	
}