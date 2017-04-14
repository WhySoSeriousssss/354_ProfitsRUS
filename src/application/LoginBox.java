package application;

import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginBox {

	private static boolean verifyAccount(String username, String password) {
		try(BufferedReader reader = new BufferedReader(new FileReader("account.txt"))) {
			String tuple;
			while((tuple = reader.readLine()) != null) {
				String[] account = tuple.split(",");
				if (username.equals(account[0])) {
					if (password.equals(account[1])) {
						return true;
					}
					else
						return false;
				}
				else
					continue;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private static void addAccount(String username, String password) {
		BufferedWriter writer = null;
        try {

            writer = new BufferedWriter(new FileWriter("account.txt",true));
            writer.write("\n"+username+","+password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}
	
	private static void registerBox() {
		// clear the root pane
		
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Register Account");
		window.setWidth(250);
		window.setHeight(250);
		
		VBox layout = new VBox(10);
		
		Scene scene = new Scene(layout);
		
		Label label1 = new Label("Please Enter your name:");
		Label label2 = new Label("Please Enter your password:");
		Label label3 = new Label();
		TextField usnField = new TextField ();
		TextField psField = new TextField ();


		
				
		Button btn = new Button("Submit");
		btn.setOnAction((ActionEvent e) -> {

			if (usnField.getText().equals("") || psField.getText().equals(""))
				label3.setText("Empty fields, please fill the required information");
			else {
				addAccount(usnField.getText(),psField.getText());
				window.close();
			}
		});
		
		layout.getChildren().addAll(label1, usnField,label2,psField, label3, btn);

		window.setScene(scene);
		window.show();
	}

	
	public static void display() {
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Log in");
		window.setWidth(250);
		window.setHeight(250);
		
		VBox layout = new VBox(10);
		
		Scene scene = new Scene(layout);
		
		Label label1 = new Label("User Name:");
		Label label2 = new Label("Password:");
		Label label3 = new Label();
		
		TextField usnField = new TextField ();
		
		PasswordField pwdField = new PasswordField();
		
		Button btn = new Button("Log in");
		btn.setOnAction((ActionEvent e) -> {
			if (verifyAccount(usnField.getText(), pwdField.getText()) == true) {
				window.close();
			}
			else {
				label3.setText("Invalid username or password");
			}
		});
		Button regBtn = new Button("Register");
		regBtn.setOnAction((ActionEvent e) -> {
			registerBox();
		});
		TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
		tileButtons.setHgap(10.0);
		tileButtons.getChildren().addAll(btn, regBtn);
		layout.getChildren().addAll(label1, usnField, label2, pwdField, label3, tileButtons);
		
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				event.consume();
				window.close();
			}
		});
		window.setScene(scene);
		window.show();
	}
}
