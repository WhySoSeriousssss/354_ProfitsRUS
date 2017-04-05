package application.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class StockFetcher {

	public static Stock get(String symbol, GregorianCalendar start, GregorianCalendar end) {
		
		Stock stock = new Stock(symbol);
		
		String url = "http://chart.finance.yahoo.com/table.csv?s=" + symbol + 
				"&a=" + start.get(Calendar.MONTH) +
				"&b=" + start.get(Calendar.DAY_OF_MONTH) +
				"&c=" + start.get(Calendar.YEAR) +
				"&d=" + end.get(Calendar.MONTH) +
				"&e=" + end.get(Calendar.DAY_OF_MONTH) +
				"&f=" + end.get(Calendar.YEAR) +
				"&g=d&ignore=.csv";
		
		try {
			URL query = new URL(url);
			URLConnection data = query.openConnection();
			Scanner input = new Scanner(data.getInputStream());
			
			if(input.hasNext())
				input.nextLine(); // Skip the header line
			
			
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String record[] = line.split(",");

				DailyStockPrice dsp = new DailyStockPrice(record[0], Float.parseFloat(record[1]), Float.parseFloat(record[2]), 
						Float.parseFloat(record[3]), Float.parseFloat(record[4]), 
						Integer.parseInt(record[5]), Float.parseFloat(record[6]));

				stock.add(dsp);
			}
			
			input.close();
		}
		
		catch(Exception e) {
			System.err.println(e);
		}
		
		return stock;

	}
	
	
}
