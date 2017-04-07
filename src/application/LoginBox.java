package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginBox {

	private static boolean verfyAccount(String username, String password) {
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
			if (verfyAccount(usnField.getText(), pwdField.getText()) == true) {
				window.close();
			}
			else {
				label3.setText("Invalid username or password");
			}
		});
		layout.getChildren().addAll(label1, usnField, label2, pwdField, label3, btn);
		
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				event.consume();
			}
		});
		window.setScene(scene);
		window.show();
	}
}
