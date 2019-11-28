package application;

import java.io.IOException;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import serverConection.ConnectionManager;

public class LoginController {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();

	private User loggedUsr = null;
	
	private String username;
	private String password;
	
	@FXML
	private Button loginButton;
	@FXML
	private TextField userField;
	@FXML
	private TextField passwordField;

	public LoginController() {

		// Uncomment next sentence to use data from server instead dummy data
		loginModel.setDummyData(false);
	}

	User getLoggedUsr() {
		return loggedUsr;

	}

	void setConnectionManager(ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
	
	@FXML
	void initialize() {
		assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				
				try {				
					 username = userField.getText().toString();
			         password = passwordField.getText().toString();
			         
			         User usr;		         
			         usr = loginModel.validateUser(username, password);
			         if(usr == null)
			        	 System.out.print("credentials do not match");
			         else {
			        	
			        	loggedUsr = usr;
						FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
						loader.load();
						NewsReaderController controller = loader.<NewsReaderController>getController();
						loginButton.getScene().setRoot(loader.getRoot());
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
				
				

			}
		});
	}
	
}