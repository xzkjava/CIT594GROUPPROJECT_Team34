package edu.upenn.cit594.ui;

import java.util.Scanner;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.CSVProcessor;
import edu.upenn.cit594.processor.JSONProcessor;
import edu.upenn.cit594.processor.Processor;

public class CommandLineUserInterface {
	private Processor processor;
	
	private Scanner in;
	
	private Logger logger;
	
	
	public CommandLineUserInterface(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName, Logger logger) {
		
		processor = createProcessor (parkingFileType, parkingFileName,propertyFileName, populationFileName, logger);
		
		in = new Scanner(System.in);
		
		this.logger = logger;

	}
	
	
	public void respondToUserInput() {
		
		String input;
		
		displayPrompts();
		
		while(in.hasNextLine()) {
			input = in.nextLine();
			if(!isUserInputAllDigits(input)) {
				System.out.println("ERROR: The input should be only numbers");
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
					logger.log(String.valueOf(System.currentTimeMillis()) + " User selected Choice:" + typedChoice);
				
					processUserCommand(typedChoice);
				
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}		
				
			}
			
		}
		
		in.close();
	}
	
	public Processor createProcessor(String parkingFileType, String parkingFileName, String propertyFileName, String populationFileName, Logger logger) {
		
		if(parkingFileType.equals("csv")){
			return new CSVProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName, logger);
		}
		else if(parkingFileType.equals("json")) {
			return new JSONProcessor(parkingFileType, parkingFileName, propertyFileName, populationFileName, logger);
		}
		
		return null;
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
	
	public boolean isUserInputAllDigits(String input) {
		
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
				System.exit(0);
			case 1:
				processor.calculateTotalPopulation();
				displayPrompts();
				break;
			case 2:
				processor.calculateFinePerCapita();
				displayPrompts();
				break;
			case 3:
				zipcode = askForZipCode();
				processor.calculateMarketValuePerProperty(zipcode);
				displayPrompts();
				break;	
			case 4:
				zipcode = askForZipCode();
				processor.calculateLivableAreaPerProperty(zipcode);
				displayPrompts();
				break;
			case 5:
				zipcode = askForZipCode();
				processor.calculateMarketValuePerCapita(zipcode);
				displayPrompts();
				break;
			case 6:
				//TODO
				displayPrompts();
				break;
		}
	}
	
	public String askForZipCode() {
		System.out.println("Please enter a valid ZIP Code:");
		String zipcode = "";
		while(in.hasNext()) {
			zipcode = in.next();
			if(validateZipcode(zipcode)) {
				break;
			}
			
			System.out.println("ERROR: The zip code entered is not valid.\nPlease enter a valid ZIP Code:");
		}
		
		//need to ask instructor if the invalid zipcode needs to be logged.
		logger.log(String.valueOf(System.currentTimeMillis()) + " User selected ZIP Code:" + zipcode);
		
		return zipcode;
		
	}
	
	//This method is called in askForZipcode()
	private boolean validateZipcode(String zipcode) {
		//TODO
		return false;	
	}
}
