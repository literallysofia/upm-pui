/**
 * 
 */
package application;

import java.io.IOException;

import application.news.Article;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {
	// TODO add attributes and methods as needed
	private User usr;
	private Article article;

	@FXML
	private Button backButton;

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; // Not logged user
		}
		// TODO Update UI information
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;
		// TODO complete this method
	}

	@FXML
	void initialize() {
		assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// TODO go back to the other scene
			}
		});
	}
}
