package application.model;

import java.util.ArrayList;

import application.Subject;

public class Stock extends Subject{

	String symbol;
	private ArrayList<DailyStockPrice> stock;
	
	public Stock(String symbol) {
		this.symbol = symbol;
		stock = new ArrayList<>();
	}
	
	public void add(DailyStockPrice dsp) {
		stock.add(dsp);
	}
	
	public DailyStockPrice GetDailyPrice(int i) {
		return stock.get(i);
	}
	
	public float HighestPriceOverAPeriod(int period) {
		float p = stock.get(0).GetHigh();
		for (int i = 1; i < period; i++) {
			if (stock.get(i).GetHigh() > p)
				p = (stock.get(i).GetHigh());
		}
		return p;
	}
	
	public float LowestPriceOverAPeriod(int period) {
		float p = stock.get(0).GetLow();
		for (int i = 1; i < period; i++) {
			if (stock.get(i).GetLow() < p)
				p = (stock.get(i).GetLow());
		}
		return p;
	}
	
	public void Print() {
		int size = stock.size();
		for (int i = 0; i < size; i++) {
			stock.get(i).Print();
		}
	}
	
}
