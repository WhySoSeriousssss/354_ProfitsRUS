package application;

import application.model.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
	private Date startDate; // Starting date is the last day of the existing record
	private int period = 19; // Default time period of a stock to be displayed is 10 days
	
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

	
	@Override
	public void start(Stage primaryStage) {
		try {
			InitializeStock();
			
			Pane root = new Pane();
			Scene scene = new Scene(root, WIDTH + MARGIN, HEIGHT + MARGIN);
			scene.setFill(Color.BLACK);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// Draw the x axis, which is the date line
			Line x_axis = new Line();
	        x_axis.setStartX(0.0f);
	        x_axis.setStartY(HEIGHT);
	        x_axis.setEndX(WIDTH);
	        x_axis.setEndY(HEIGHT);
	        x_axis.setStroke(Color.WHITE);
	        root.getChildren().add(x_axis);
		    
	        // Draw the y axis, which is the price line
	        Line y_axis = new Line();
	        y_axis.setStartX(WIDTH);
	        y_axis.setStartY(0.0f);
	        y_axis.setEndX(WIDTH);
	        y_axis.setEndY(HEIGHT);
	        y_axis.setStroke(Color.WHITE);
	        root.getChildren().add(y_axis);
	        
	        // Paint the grid
	        for (float i = WIDTH; i >= 0; i -= spaceBetweenDates) {
		        Line l = new Line();
		        l.setStartX(i);
		        l.setStartY(0.0f);
		        l.setEndX(i);
		        l.setEndY(HEIGHT);
		        l.setStroke(Color.WHITE);
		        l.setStrokeWidth(0.3);
		        root.getChildren().add(l);
	        }
	        for (float i = 0; i < HEIGHT; i += spaceBetweenPrices) {
		        Line l = new Line();
		        l.setStartX(0.0f);
		        l.setStartY(i);
		        l.setEndX(WIDTH);
		        l.setEndY(i);
		        l.setStroke(Color.WHITE);
		        l.setStrokeWidth(0.3);
		        root.getChildren().add(l);
	        }

	        /*
	        Label label = new Label("hello world");
	        label.setTextFill(Color.YELLOW);
	        label.setPrefSize(100, 100);
	        root.getChildren().add(label);
	        */
	        
	        DrawChart(root);
	        
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
