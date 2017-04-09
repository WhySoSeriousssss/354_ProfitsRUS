package application;

import application.model.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.model.Stock;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;

public class Main extends Application {
	private enum Month {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
	}
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private final int MARGIN = 100;
	
	private Stock stock;
		
	private float upperPrice = 1;
	private float lowerPrice = 1;
	final private int maxNumOfRecordsToDisplay = 50;
	
	private int interval = 1;
	
	private float spaceBetweenDates;
	private float spaceBetweenPrices;
	
	private Line[] SMACompare = new Line[maxNumOfRecordsToDisplay - 1]; //comparison line for finding indicators
	private int comparatorAverage = 1;        		// set average
	
	private Line[] SMA20 = new Line[maxNumOfRecordsToDisplay - 1];
	private Line[] SMA50 = new Line[maxNumOfRecordsToDisplay - 1];
	private Line[] SMA100 = new Line[maxNumOfRecordsToDisplay - 1];
	private Line[] SMA200 = new Line[maxNumOfRecordsToDisplay - 1];	
	
	private ArrayList<Polygon>[] indicatorPolygons = new ArrayList [4];
	private ArrayList<Text>[] indicatorText = new ArrayList [4];
	
	private float[] maCompare;
	private float[] ma1; 							//to check ma values
	private float[] ma2;
	private float[] ma3;
	private float[] ma4;
	
	private BorderPane root = new BorderPane(); // the pane of the main interface
	private Scene scene = new Scene(root, WIDTH + MARGIN, HEIGHT + MARGIN); // the scene of the main interface

	// the pop-up box to let the users enter the stock symbol they want to view
	public void selectStockBox() {
		// clear the root pane
		
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Select Stock");
		window.setWidth(250);
		window.setHeight(200);
		
		VBox layout = new VBox(10);
		
		Scene scene = new Scene(layout);
		
		Label label1 = new Label("Please Enter the stock symbol:");
		Label label3 = new Label();
		
		TextField stockSymbol = new TextField ();
				
		Button btn = new Button("Submit");
		btn.setOnAction((ActionEvent e) -> {
			if (stockSymbol.getText().equals(""))
				label3.setText("Invalid stock symbol");
			else {
				cleanScreen(root);
				InitializeStock(stockSymbol.getText());
				InitializeGrid(root);
				window.close();
			}
		});
		layout.getChildren().addAll(label1, stockSymbol, label3, btn);

		window.setScene(scene);
		window.show();
	}
	
	// clear the screen
	private void cleanScreen(BorderPane pane) {
		pane.getChildren().clear();
	}
	
	// Get the historical data of the stock according to users' inputs, and initialize the data related to the stock
	private void InitializeStock(String symbol) {
		GregorianCalendar start = new GregorianCalendar(2011, 0, 1);
		GregorianCalendar end = new GregorianCalendar(2017, 3, 6); // find a way to get the current date, instead of hardcoding it
		stock = StockFetcher.get(symbol, start, end);
		
		IntializePriceBounds();
	}
	
	private void IntializePriceBounds() {
		upperPrice = (float) Math.ceil(stock.HighestPriceOverAPeriod(maxNumOfRecordsToDisplay * interval));
		lowerPrice = (float) Math.floor(stock.LowestPriceOverAPeriod(maxNumOfRecordsToDisplay * interval));
		
		spaceBetweenDates = WIDTH / ((float)maxNumOfRecordsToDisplay + 1);
		spaceBetweenPrices = HEIGHT / (upperPrice - lowerPrice);
	}
	
	// Intialize the main interface, including setting toolbar, and paint the grid
	private void InitializeGrid(BorderPane root) {
		//
		Pane chart = new Pane();
		chart.setStyle("-fx-background-color: #000000;");
		root.setCenter(chart);
		
		InitializeSMA(comparatorAverage, chart);
		
		InitializeSMA(20, chart);
		InitializeSMA(50, chart);
		InitializeSMA(100, chart);
		InitializeSMA(200, chart);
		
		initIndicators();
		findIndicators();
		
		// Toolbar
		ToggleButton addMA20 = new ToggleButton("MA(20)");
		ToggleButton addMA50 = new ToggleButton("MA(50)");
		ToggleButton addMA100 = new ToggleButton("MA(100)");
		ToggleButton addMA200 = new ToggleButton("MA(200)");
		
		Button selectStockBtn = new Button("Select Stock");
		selectStockBtn.setOnAction((ActionEvent e) -> {
			selectStockBox();
		});
		
		// indicators
		ToggleButton addIndicatorsBtn = new ToggleButton("Add Indicators");
		addIndicatorsBtn.setOnAction((ActionEvent e) -> {
		    if (addIndicatorsBtn.isSelected()) {
		    	DrawSMA(comparatorAverage, chart);
		    	if(addMA20.isSelected()){
		    		DrawIND(0,chart);
		    	}
		    	if(addMA50.isSelected()){
		    		DrawIND(1,chart);
		    	}
		    	if(addMA100.isSelected()){
		    		DrawIND(2,chart);
		    	}
		    	if(addMA200.isSelected()){
		    		DrawIND(3,chart);
		    	}
		    }
		    else {
		    	EraseSMA(comparatorAverage, chart);
		    	EraseIND(0,chart);
		    	EraseIND(1,chart);
		    	EraseIND(2,chart);
		    	EraseIND(3,chart);
		    }
		});
		
		Label lb = new Label("Set the interval(days):");
		TextField tf = new TextField(Integer.toString(interval));
		Button bt = new Button("Change");
		bt.setOnAction((ActionEvent e) -> {
			if (isNumeric(tf.getText())) {
				interval = Integer.parseInt(tf.getText());
				IntializePriceBounds();
				cleanScreen(root);
				InitializeGrid(root);
			}
		});
		
		ToolBar toolBar1 = new ToolBar();
		toolBar1.getItems().addAll(new Separator(), addMA20, addMA50, addMA100, addMA200, new Separator(), selectStockBtn, new Separator(), addIndicatorsBtn, new Separator(), lb, tf, bt);
		root.setTop(toolBar1);
		
		// Toggle buttons actions
		addMA20.setOnAction((ActionEvent e) -> {
		    if (addMA20.isSelected()) {
		    	DrawSMA(20, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		DrawIND(0,chart);
		    	}
		    }
		    else {
		    	EraseSMA(20, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		EraseIND(0,chart);
		    	}
		    }
		});
		addMA50.setOnAction((ActionEvent e) -> {
		    if (addMA50.isSelected()) {
		    	DrawSMA(50, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		DrawIND(1,chart);
		    	}
		    }
		    else {
		    	EraseSMA(50, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		EraseIND(1,chart);
		    	}
		    }
		});
		addMA100.setOnAction((ActionEvent e) -> {
		    if (addMA100.isSelected()) {
		    	DrawSMA(100, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		DrawIND(2,chart);
		    	}
		    }
		    else {
		    	EraseSMA(100, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		EraseIND(2,chart);
		    	}
		    }
		});
		addMA200.setOnAction((ActionEvent e) -> {
		    if (addMA200.isSelected()) {
		    	DrawSMA(200, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		DrawIND(3,chart);
		    	}
		    }
		    else {
		    	EraseSMA(200, chart);
		    	if(addIndicatorsBtn.isSelected()){
		    		EraseIND(3,chart);
		    	}
		    }
		});
		
		
		
		// Draw the x axis, which is the date line
		Line x_axis = new Line();
        x_axis.setStartX(0.0f);
        x_axis.setStartY(HEIGHT);
        x_axis.setEndX(WIDTH);
        x_axis.setEndY(HEIGHT);
        x_axis.setStroke(Color.WHITE);
        chart.getChildren().add(x_axis);
	    
        // Draw the y axis, which is the price line
        Line y_axis = new Line();
        y_axis.setStartX(WIDTH);
        y_axis.setStartY(0.0f);
        y_axis.setEndX(WIDTH);
        y_axis.setEndY(HEIGHT);
        y_axis.setStroke(Color.WHITE);
        chart.getChildren().add(y_axis);
        
        // Paint the grid
        for (float i = WIDTH; i >= 0; i -= spaceBetweenDates) {
	        Line l = new Line();
	        l.setStartX(i);
	        l.setStartY(0.0f);
	        l.setEndX(i);
	        l.setEndY(HEIGHT);
	        l.setStroke(Color.WHITE);
	        l.setStrokeWidth(0.2);
	        chart.getChildren().add(l);
        }
        for (float i = 0; i < HEIGHT; i += spaceBetweenPrices) {
	        Line l = new Line();
	        l.setStartX(0.0f);
	        l.setStartY(i);
	        l.setEndX(WIDTH);
	        l.setEndY(i);
	        l.setStroke(Color.WHITE);
	        l.setStrokeWidth(0.2);
	        chart.getChildren().add(l);
        }

        // Draw the labels on the price axis
        for (int i = 0; i <= (upperPrice - lowerPrice); i++) {
        	Label label = new Label(String.valueOf(upperPrice - i));
	        label.setTextFill(Color.YELLOW);
	        label.setPrefSize(100, 100);
	        label.setLayoutX(WIDTH + 10);
	        label.setLayoutY(spaceBetweenPrices * i - 50);
	        chart.getChildren().add(label);
        }
        
        
        // Draw the labels on the date axis
        int base_month = stock.GetDailyPrice(0).GetDate().get(Calendar.MONTH);
        int base_year = stock.GetDailyPrice(0).GetDate().get(Calendar.YEAR);
        
        for (int i = 0; i < maxNumOfRecordsToDisplay * interval; i += interval) {
        	// labels of day
        	Label label = new Label(String.valueOf(stock.GetDailyPrice(i).GetDate().get(Calendar.DAY_OF_MONTH)));
        	label.setTextFill(Color.YELLOW);
	        label.setPrefSize(100, 100);
	        label.setLayoutX(WIDTH - spaceBetweenDates * i / interval - 20);
	        label.setLayoutY(HEIGHT - 40);
	        chart.getChildren().add(label);
	        
	        // labels of month
	        int month = stock.GetDailyPrice(i).GetDate().get(Calendar.MONTH);
	        if (month != base_month) {
	        	Label label_month = new Label(String.valueOf(Month.values()[base_month]));
	        	label_month.setTextFill(Color.YELLOW);
	        	label_month.setPrefSize(100, 100);
	        	label_month.setLayoutX(WIDTH - spaceBetweenDates * i / interval);
	        	label_month.setLayoutY(HEIGHT - 20);
		        chart.getChildren().add(label_month);
		        base_month = month;
	        }
	        
	        // labels of year
	        int year = stock.GetDailyPrice(i).GetDate().get(Calendar.YEAR);
	        if (year != base_year) {
	        	Label label_year = new Label(String.valueOf(base_year));
	        	label_year.setTextFill(Color.YELLOW);
	        	label_year.setPrefSize(100, 100);
	        	label_year.setLayoutX(WIDTH - spaceBetweenDates * i / interval);
	        	label_year.setLayoutY(HEIGHT);
		        chart.getChildren().add(label_year);
		        base_year = year;
	        }
        }
        
        DrawChart(chart);
	}
	
	// Draw the price chart of the stock
	public void DrawChart(Pane root) {
		for (int i = 0; i < maxNumOfRecordsToDisplay * interval; i+=interval) {
			float high = stock.GetDailyPrice(i).GetHigh();
			float low = stock.GetDailyPrice(i).GetLow();
			float open = stock.GetDailyPrice(i).GetOpening();
			float close = stock.GetDailyPrice(i).GetClose();
			
			// Draw the "High-low" price line
			Line line = new Line();
			line.setStartX(WIDTH - (i / interval + 1) * spaceBetweenDates);
			line.setStartY(HEIGHT * (upperPrice - high) / (upperPrice - lowerPrice));
			line.setEndX(WIDTH - (i / interval + 1) * spaceBetweenDates);
			line.setEndY(HEIGHT * (upperPrice - low) / (upperPrice - lowerPrice));
			
			// Draw the "Open-close" price Rectangle
			Rectangle r = new Rectangle();
			r.setX(WIDTH - (i / interval + 1) * spaceBetweenDates - 5.0f);
			r.setY(HEIGHT * (upperPrice - ((open > close)?open:close)) / (upperPrice - lowerPrice));
			r.setWidth(10.0f);
			r.setHeight(HEIGHT * Math.abs(open - close) / (upperPrice - lowerPrice));
			
			
			if (close < open) {
				line.setStroke(Color.RED);
				r.setFill(Color.RED);
			}
			else {
				line.setStroke(Color.GREEN);
				r.setFill(Color.GREEN);
			}
			
	        root.getChildren().add(line);
	        root.getChildren().add(r);
		}
	}

	// Initially set all SMAs

	public void InitializeSMA(int n, Pane root) {		
		float ma[] = new float[maxNumOfRecordsToDisplay];
		for (int i = 0; i < maxNumOfRecordsToDisplay; i++) {
			float sum = 0;
			for (int j = 0; j < n; j++) {
				sum += stock.GetDailyPrice(i * interval + j).GetClose();
			}
			ma[i] = sum / n;
		}		
		
		if(n==comparatorAverage){
			maCompare = ma;
		}else{
			switch(n){
			case 20:
				ma1 = ma;
				break;
			case 50:
				ma2 = ma;
				break;
			case 100:
				ma3 = ma;
				break;
			case 200:
				ma4 = ma;
				break;		
			}
		}
			
		for (int i = 0; i < maxNumOfRecordsToDisplay - 1; i++) {
      if(n == comparatorAverage){
				SMACompare[i] = new Line();
				SMACompare[i].setStartX(WIDTH - (i + 1) * spaceBetweenDates);
				SMACompare[i].setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
				SMACompare[i].setEndX(WIDTH - (i + 2) * spaceBetweenDates);
				SMACompare[i].setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				SMACompare[i].setStroke(Color.YELLOW);
				continue;
			}
			switch(n) {
			case 20:
				SMA20[i] = new Line();
				SMA20[i].setStartX(WIDTH - (i + 1) * spaceBetweenDates);
				SMA20[i].setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
				SMA20[i].setEndX(WIDTH - (i + 2) * spaceBetweenDates);
				SMA20[i].setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				SMA20[i].setStroke(Color.YELLOW);
				break;
			case 50:
				SMA50[i] = new Line();
				SMA50[i].setStartX(WIDTH - (i + 1) * spaceBetweenDates);
				SMA50[i].setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
				SMA50[i].setEndX(WIDTH - (i + 2) * spaceBetweenDates);
				SMA50[i].setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				SMA50[i].setStroke(Color.YELLOW);
				break;
			case 100:
				SMA100[i] = new Line();
				SMA100[i].setStartX(WIDTH - (i + 1) * spaceBetweenDates);
				SMA100[i].setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
				SMA100[i].setEndX(WIDTH - (i + 2) * spaceBetweenDates);
				SMA100[i].setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				SMA100[i].setStroke(Color.YELLOW);
				break;
			case 200:
				SMA200[i] = new Line();
				SMA200[i].setStartX(WIDTH - (i + 1) * spaceBetweenDates);
				SMA200[i].setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
				SMA200[i].setEndX(WIDTH - (i + 2) * spaceBetweenDates);
				SMA200[i].setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				SMA200[i].setStroke(Color.YELLOW);
				break;
			}					
		}
	}
	
	//init indicator polygons arrays
	private void initIndicators(){
		for(int i=0;i<4;i++){
			indicatorPolygons[i]= new ArrayList <Polygon>();
			indicatorText[i] = new ArrayList <Text>();
		}
	}
	
	//find where sma meets comparator
	private void findIndicators (){
		findIndicatorHelper(maCompare, ma1, 0);
		findIndicatorHelper(maCompare, ma2, 1);
		findIndicatorHelper(maCompare, ma3, 2);
		findIndicatorHelper(maCompare, ma4, 3);
	}
	
	private void findIndicatorHelper (float[] comp, float [] ma, int n){
		for(int i = 1; i< period-1; i++){
			if(ma[i-1]>comp[i-1]&&ma[i]<comp[i]){
				double p1 = ((double)(WIDTH - (i ) * spaceBetweenDates));
				double p2 = ((double)(WIDTH - (i + 1) * spaceBetweenDates));
				double p3 = (p1+p2)/2;
				double y0 = (HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
				double y1 = y0-55;
				double y2 = y0-25;
				System.out.println("SELL");
				Polygon pol = new Polygon();
				pol.getPoints().addAll(new Double[]{
				p1, y1,
				p2, y1,
				p3, y2,
				});
				pol.setFill(Color.LIGHTBLUE);
				Text label = new Text();
				label.setX(p1-2);
				label.setY(y2-10);
				label.setText("SELL");
				label.setFill(Color.CHARTREUSE);
				label.setFont(Font.font(null, FontWeight.BOLD, 12));
				indicatorPolygons[n].add(pol);
				indicatorText[n].add(label);
			}
				if(ma[i-1]<comp[i-1]&&ma[i]>comp[i]){
					System.out.println("SELL");
					double p1 = ((double)(WIDTH - (i) * spaceBetweenDates));
					double p2 = ((double)(WIDTH - (i + 1) * spaceBetweenDates));
					double p3 = (p1+p2)/2;
					double y0 = (HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
					double y1 = y0+45;
					double y2 = y0+10;
					System.out.println("BUY");
					Polygon pol = new Polygon();
					pol.getPoints().addAll(new Double[]{
					p1, y1,
					p2, y1,
					p3, y2,
					});
					pol.setFill(Color.YELLOW);
					Text label = new Text();
					label.setX(p1-2);
					label.setY(y2+20);
					label.setText("BUY");
					label.setFill(Color.CHARTREUSE);
					label.setFont(Font.font(null, FontWeight.BOLD, 12));
					indicatorPolygons[n].add(pol);
					indicatorText[n].add(label);
			}
		}
	}
	
	// draw the sma on the price chart
	private void DrawSMA(int n, Pane root) {

		for (int i = 0; i < maxNumOfRecordsToDisplay - 1; i++) {
      if(n == comparatorAverage){
				root.getChildren().add(SMACompare[i]);
				continue;
			}
			switch(n) {
			case 20:
				root.getChildren().add(SMA20[i]);
				break;
			case 50:
				root.getChildren().add(SMA50[i]);
				break;
			case 100:
				root.getChildren().add(SMA100[i]);
				break;
			case 200:
				root.getChildren().add(SMA200[i]);
				break;
			}
		}
	}
	
	// remove the sma from the chart
	public void EraseSMA(int n, Pane root) {
		for (int i = 0; i < maxNumOfRecordsToDisplay - 1; i++) {
      if(n == comparatorAverage){
				root.getChildren().remove(SMACompare[i]);
				continue;
			}
			switch(n) {
			case 20:
				root.getChildren().remove(SMA20[i]);
				break;
			case 50:
				root.getChildren().remove(SMA50[i]);
				break;
			case 100:
				root.getChildren().remove(SMA100[i]);
				break;
			case 200:
				root.getChildren().remove(SMA200[i]);
				break;
			}
		}
	}
	
	// draw the indicators on the price chart
		private void DrawIND(int n, Pane root) {
			if(indicatorPolygons[n].isEmpty()){
				System.out.println("No indicators found.");
			}else{
				for(Polygon pol : indicatorPolygons[n]){
					root.getChildren().add(pol);
				}
				for(Text txt : indicatorText[n]){
					root.getChildren().add(txt);
				}
			}
		}
		
		// remove the indicators from the chart
		public void EraseIND(int n, Pane root) {
			if(indicatorPolygons[n].isEmpty()){
				System.out.println("No indicators found.");
			}else{
				for(Polygon pol : indicatorPolygons[n]){
					root.getChildren().remove(pol);
				}
				for(Text txt : indicatorText[n]){
					root.getChildren().remove(txt);
				}
			}
		}
	private boolean isNumeric(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;	
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		try {							        
			primaryStage.setScene(scene);
			primaryStage.show();
			selectStockBox();
	//		LoginBox.display();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
	
		launch(args);
	}
}
