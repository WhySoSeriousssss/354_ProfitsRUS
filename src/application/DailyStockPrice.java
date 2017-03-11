
package application;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DailyStockPrice {

	private Date date;
	private float Opening;
	private float High;
	private float Low;
	private float Close;
	private float AdjClose;
	
	private int Volumn;
	
	public DailyStockPrice(String date, float open, float high, float low, float close, int volumn, float adjclose) {	

		// Convert String to type "Date"
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		try {
			this.date = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.Opening = open;
		this.High = high;
		this.Low = low;
		this.Close = close;
		this.AdjClose = adjclose;
		this.Volumn = volumn;
	}
	
	public Date GetDate() {
		return this.date;
	}
	
	public float GetOpening() {
		return this.Opening;
	}
	
	public float GetHigh() {
		return this.High;
	}
	
	public float GetLow() {
		return this.Low;
	}
	
	public float GetClose() {
		return this.Close;
	}
	
	public float GetAdjClose() {
		return this.AdjClose;
	}
	
	public int GetVolumn() {
		return this.Volumn;
	}
	
	public void Print() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		System.out.println(dateFormat.format(date) + " " + Opening + " " + High + " " + Low + " " + Close + " " + Volumn +  " " + AdjClose);
	}
}
