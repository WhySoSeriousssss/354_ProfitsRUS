package application;

import application.model.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	private int period = 50; // 20 days' prices will be displayed by default
	
	private float spaceBetweenDates;
	private float spaceBetweenPrices;
	
	private Line[] SMA20 = new Line[period-1];
	private Line[] SMA50 = new Line[period-1];
	private Line[] SMA100 = new Line[period-1];
	private Line[] SMA200 = new Line[period-1];
	
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
		GregorianCalendar start = new GregorianCalendar(2016, 1, 3);
		GregorianCalendar end = new GregorianCalendar(2017, 3, 6); // find a way to get the current date, instead of hardcoding it
		stock = StockFetcher.get(symbol, start, end);
		
		upperPrice = (float) Math.ceil(stock.HighestPriceOverAPeriod(period));
		lowerPrice = (float) Math.floor(stock.LowestPriceOverAPeriod(period));
		
		spaceBetweenDates = WIDTH / ((float)period + 1);
		spaceBetweenPrices = HEIGHT / (upperPrice - lowerPrice);
	}
	
	// Intialize the main interface, including setting toolbar, and paint the grid
	private void InitializeGrid(BorderPane root) {
		//
		Pane chart = new Pane();
		chart.setStyle("-fx-background-color: #000000;");
		root.setCenter(chart);
		
		InitializeSMA(20, chart);
		InitializeSMA(50, chart);
		InitializeSMA(100, chart);
		InitializeSMA(200, chart);
		
		// Toolbar
		ToggleButton addMA20 = new ToggleButton("MA(20)");
		ToggleButton addMA50 = new ToggleButton("MA(50)");
		ToggleButton addMA100 = new ToggleButton("MA(100)");
		ToggleButton addMA200 = new ToggleButton("MA(200)");
		
		Button selectStockBtn = new Button("Select Stock");
		selectStockBtn.setOnAction((ActionEvent e) -> {
			selectStockBox();
		});
		
		ToolBar toolBar1 = new ToolBar();
		toolBar1.getItems().addAll(new Separator(), addMA20, addMA50, addMA100, addMA200, new Separator(), selectStockBtn);
		root.setTop(toolBar1);
		
		// Toggle buttons actions
		addMA20.setOnAction((ActionEvent e) -> {
		    if (addMA20.isSelected()) {
		    	DrawSMA(20, chart);
		    }
		    else {
		    	EraseSMA(20, chart);
		    }
		});
		addMA50.setOnAction((ActionEvent e) -> {
		    if (addMA50.isSelected()) {
		    	DrawSMA(50, chart);
		    }
		    else {
		    	EraseSMA(50, chart);
		    }
		});
		addMA100.setOnAction((ActionEvent e) -> {
		    if (addMA100.isSelected()) {
		    	DrawSMA(100, chart);
		    }
		    else {
		    	EraseSMA(100, chart);
		    }
		});
		addMA200.setOnAction((ActionEvent e) -> {
		    if (addMA200.isSelected()) {
		    	DrawSMA(200, chart);
		    }
		    else {
		    	EraseSMA(200, chart);
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
        
        for (int i = 0; i < period; i++) {
        	// labels of day
        	Label label = new Label(String.valueOf(stock.GetDailyPrice(i).GetDate().get(Calendar.DAY_OF_MONTH)));
        	label.setTextFill(Color.YELLOW);
	        label.setPrefSize(100, 100);
	        label.setLayoutX(WIDTH - spaceBetweenDates * i - 20);
	        label.setLayoutY(HEIGHT - 40);
	        chart.getChildren().add(label);
	        
	        // labels of month
	        int month = stock.GetDailyPrice(i).GetDate().get(Calendar.MONTH);
	        if (month != base_month) {
	        	Label label_month = new Label(String.valueOf(Month.values()[base_month]));
	        	label_month.setTextFill(Color.YELLOW);
	        	label_month.setPrefSize(100, 100);
	        	label_month.setLayoutX(WIDTH - spaceBetweenDates * i);
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
	        	label_year.setLayoutX(WIDTH - spaceBetweenDates * i);
	        	label_year.setLayoutY(HEIGHT);
		        chart.getChildren().add(label_year);
		        base_year = year;
	        }
        }
        
        DrawChart(chart);

	}
	
	// Draw the price chart of the stock
	public void DrawChart(Pane root) {
		for (int i = 0; i < period; i++) {
			float high = stock.GetDailyPrice(i).GetHigh();
			float low = stock.GetDailyPrice(i).GetLow();
			float open = stock.GetDailyPrice(i).GetOpening();
			float close = stock.GetDailyPrice(i).GetClose();
			
			// Draw the "High-low" price line
			Line line = new Line();
			line.setStartX(WIDTH - (i + 1) * spaceBetweenDates);
			line.setStartY(HEIGHT * (upperPrice - high) / (upperPrice - lowerPrice));
			line.setEndX(WIDTH - (i + 1) * spaceBetweenDates);
			line.setEndY(HEIGHT * (upperPrice - low) / (upperPrice - lowerPrice));
			
			// Draw the "Open-close" price Rectangle
			Rectangle r = new Rectangle();
			r.setX(WIDTH - (i + 1) * spaceBetweenDates - 5.0f);
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
		float ma[] = new float[period];
		for (int i = 0; i < period; i++) {
			float sum = 0;
			for (int j = 0; j < n; j++) {
				sum += stock.GetDailyPrice(i + j).GetClose();
			}
			ma[i] = sum / n;
		}
		
		for (int i = 0; i < period - 1; i++) {
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
	
	// draw the sma on the price chart
	private void DrawSMA(int n, Pane root) {
		for (int i = 0; i < period - 1; i++) {
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
		for (int i = 0; i < period - 1; i++) {
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
	
	@Override
	public void start(Stage primaryStage) {
		
		try {							        
			primaryStage.setScene(scene);
			primaryStage.show();
			selectStockBox();
			LoginBox.display();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
	
		launch(args);
	}
}
