package application;

import java.io.FileReader;
import java.util.ArrayList;
import com.opencsv.CSVReader;

public class Stock extends Subject{

	int id;
	private ArrayList<DailyStockPrice> stock = new ArrayList<>();
	
	public Stock() {
		try {
			CSVReader reader = new CSVReader(new FileReader("sample.csv"));
			String[] nextLine;
			reader.readNext();
			while ((nextLine = reader.readNext()) != null) {
				stock.add(new DailyStockPrice(nextLine[0], Float.parseFloat(nextLine[1]), Float.parseFloat(nextLine[2]), 
						Float.parseFloat(nextLine[3]), Float.parseFloat(nextLine[4]), 
						Integer.parseInt(nextLine[5]), Float.parseFloat(nextLine[6])));
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void Print() {
		int size = stock.size();
		for (int i = 0; i < size; i++) {
			stock.get(i).Print();
		}
	}
	
}
