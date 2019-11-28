package application;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;

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
	@FXML
	private Text loginError;
	
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
			         if(usr == null) {
			        	 System.out.print("credentials do not match");
			         	 loginError.setText("These credentials do not match");
			         }else {		        	
			        	loggedUsr = usr;						
						Properties prop = new Properties();
						prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
						prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");
						FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
						loader.load();
						NewsReaderController controller = loader.<NewsReaderController>getController();			
						ConnectionManager connection = new ConnectionManager(prop);
						connection.setAnonymousAPIKey("DEV_TEAM_48392");
						controller.setConnectionManager(connection);
						controller.setUsr(loggedUsr);
						loginButton.getScene().setRoot(loader.getRoot());
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}catch (AuthenticationError e2) {
					Logger.getGlobal().log(Level.SEVERE, "Error in loging process");
					e2.printStackTrace();	
				}	
			}
		});
	}
	
}