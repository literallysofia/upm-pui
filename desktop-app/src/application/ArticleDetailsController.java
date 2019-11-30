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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {

	private User usr;
	private Article article;
	private Scene mainScene;

	@FXML
	private Button backButton;
	@FXML
	private Text articleTitle;
	@FXML
	private Text articleSubtitle;
	@FXML
	private Text articleCategory;
	@FXML
	private ImageView articleImage;
	@FXML
	private Text articleBody;

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

		this.articleTitle.setText(article.getTitle());
		this.articleSubtitle.setText(article.getSubtitle());
		this.articleCategory.setText(article.getCategory());
		this.articleImage.setImage(article.getImageData());
		this.articleBody.setText(article.getBodyText());
	}

	void setMainScene(Scene scene) {
		this.mainScene = scene;
	}

	@FXML
	void initialize() {
		assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
				primaryStage.setScene(mainScene);
			}
		});
	}
}
