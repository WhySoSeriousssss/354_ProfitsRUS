
package application.model;


import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyStockPrice {

	private GregorianCalendar date;
	private float Opening;
	private float High;
	private float Low;
	private float Close;
	private float AdjClose;
	
	private int Volumn;
	
	public DailyStockPrice(String strDate, float open, float high, float low, float close, int volumn, float adjclose) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse(strDate);
		date = new GregorianCalendar();
		date.setTime(d);

		this.Opening = open;
		this.High = high;
		this.Low = low;
		this.Close = close;
		this.AdjClose = adjclose;
		this.Volumn = volumn;
	}
	
	public GregorianCalendar GetDate() {
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
		System.out.println(date.toString() + " " + Opening + " " + High + " " + Low + " " + Close + " " + Volumn +  " " + AdjClose);
	}
}
