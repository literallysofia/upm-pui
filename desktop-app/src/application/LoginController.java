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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;

public class LoginController {

	private LoginModel loginModel = new LoginModel();
	private Scene mainScene;
	private NewsReaderController mainController;

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
		loginModel.setDummyData(false);
	}

	User getLoggedUsr() {
		return loggedUsr;

	}

	void setConnectionManager(ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}

	void setMainScene(Scene scene) {
		this.mainScene = scene;
	}

	void setMainController(NewsReaderController c) {
		this.mainController = c;
	}

	@FXML
	void initialize() {
		assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
	}

	@FXML
	void loginAction(ActionEvent e) {
		username = userField.getText().toString();
		password = passwordField.getText().toString();

		User usr = loginModel.validateUser(username, password);
		if (usr == null) {
			System.out.print("credentials do not match");
			loginError.setText("These credentials do not match");
		} else {
			loggedUsr = usr;
			Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			mainController.setUsr(loggedUsr);
			primaryStage.setScene(mainScene);
		}
	}

	@FXML
	void cancelAction(ActionEvent e) {
		Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		primaryStage.setScene(mainScene);
	}
}