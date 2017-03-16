package application;

import application.model.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.model.Stock;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Main extends Application {
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private final int MARGIN = 100;
	
	private Stock stock = new Stock();
		
	private float upperPrice = 1;
	private float lowerPrice = 1;
	private int period = 20; // 20 days' prices will be displayed by default
	
	private float spaceBetweenDates;
	private float spaceBetweenPrices;
	
	private void InitializeStock() {
		upperPrice = (float) Math.ceil(stock.HighestPriceOverAPeriod(period));
		lowerPrice = (float) Math.floor(stock.LowestPriceOverAPeriod(period));
		
		spaceBetweenDates = WIDTH / (period + 1);
		spaceBetweenPrices = HEIGHT / (upperPrice - lowerPrice);
		
		System.out.println("high is " + upperPrice + " low is " + lowerPrice);
	}
	
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
				line.setStroke(Color.GREEN);
				r.setFill(Color.GREEN);
			}
			else {
				line.setStroke(Color.RED);
				r.setFill(Color.RED);
			}
			
	        root.getChildren().add(line);
	        root.getChildren().add(r);
		}
	}

	public void DrawSMA(int n, Pane root) {
		float ma[] = new float[period];
		for (int i = 0; i < period; i++) {
			float sum = 0;
			for (int j = 0; j < n; j++) {
				sum += stock.GetDailyPrice(i + j).GetClose();
			}
			ma[i] = sum / n;
		}
		
		for (int i = 0; i < period - 1; i++) {
			Line line = new Line();
			line.setStartX(WIDTH - (i + 1) * spaceBetweenDates);
			line.setStartY(HEIGHT * (upperPrice - ma[i]) / (upperPrice - lowerPrice));
			line.setEndX(WIDTH - (i + 2) * spaceBetweenDates);
			line.setEndY(HEIGHT * (upperPrice - ma[i + 1]) / (upperPrice - lowerPrice));
			line.setStroke(Color.YELLOW);
			root.getChildren().add(line);
		}
	}
	
	public void EraseSMA(int n, Pane root) {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			InitializeStock();
			
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, WIDTH + MARGIN, HEIGHT + MARGIN);
	//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//
			Pane chart = new Pane();
			chart.setStyle("-fx-background-color: #000000;");
			root.setCenter(chart);
			
			// Toolbar
			ToggleButton addMA20 = new ToggleButton("MA(20)");
			ToggleButton addMA50 = new ToggleButton("MA(50)");
			ToggleButton addMA100 = new ToggleButton("MA(100)");
			ToggleButton addMA200 = new ToggleButton("MA(200)");
			
			ToolBar toolBar1 = new ToolBar();
			toolBar1.getItems().addAll(new Separator(), addMA20, addMA50, addMA100, addMA200, new Separator());
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
		        l.setStrokeWidth(0.3);
		        chart.getChildren().add(l);
	        }
	        for (float i = 0; i < HEIGHT; i += spaceBetweenPrices) {
		        Line l = new Line();
		        l.setStartX(0.0f);
		        l.setStartY(i);
		        l.setEndX(WIDTH);
		        l.setEndY(i);
		        l.setStroke(Color.WHITE);
		        l.setStrokeWidth(0.3);
		        chart.getChildren().add(l);
	        }

	        /*
	        Label label = new Label("hello world");
	        label.setTextFill(Color.YELLOW);
	        label.setPrefSize(100, 100);
	        root.getChildren().add(label);
	        */
	        
	        DrawChart(chart);
	        
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {

		launch(args);
	}
}
