/**
 * 
 */
package application;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleEditController {

	private ConnectionManager connection;
	private ArticleEditModel editingArticle;
	private User usr;
	private Scene mainScene;

	@FXML
	private Text pageTitle;

	@FXML
	private ImageView articleImage;

	@FXML
	private TextField articleTitle; // required

	@FXML
	private TextField articleSubtitle;

	@FXML
	private MenuButton articleCategory; // required

	@FXML
	private TextArea articleAbstractText;

	@FXML
	private TextArea articleBodyText;

	@FXML
	private HTMLEditor articleAbstractHTML;

	@FXML
	private HTMLEditor articleBodyHTML;

	@FXML
	private Button sendBackButton;
	
	@FXML
	private MenuButton categoryMenu;

	void setMainScene(Scene scene) {
		this.mainScene = scene;
	}

	void setCategories(ObservableList<Categories> categories) {
		for (int i = 0; i < categories.size(); i++) {
			MenuItem menuItem = new MenuItem(categories.get(i).toString());
			this.categoryMenu.getItems().add(menuItem);
			menuItem.setId(categories.get(i).toString());
		}
	}

	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
					// TODO Update image on UI
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Send and article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if the article has been saved
	 */
	private boolean send() {
		String titleText = null; // TODO Get article title
		Categories category = null; // TODO Get article cateory
		if (titleText == null || category == null || titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory",
					ButtonType.OK);
			alert.showAndWait();
			return false;
		}
//TODO prepare and send using connection.saveArticle( ...)

		return true;
	}

	/**
	 * This method is used to set the connection manager which is needed to save a
	 * news
	 * 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		this.sendBackButton.setDisable(false);
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		// TODO Update UI and controls

	}

	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.editingArticle = (article != null) ? new ArticleEditModel(usr, article) : new ArticleEditModel(usr);
		this.pageTitle.setText("Edit Article");
		this.articleImage.setImage(article.getImageData());
		this.articleTitle.setText(article.getTitle());
		this.articleSubtitle.setText(article.getSubtitle());
		this.articleAbstractText.setText(article.getAbstractText());
		this.articleBodyText.setText(article.getBodyText());
		this.articleAbstractHTML.setHtmlText(article.getAbstractText());
		this.articleBodyHTML.setHtmlText(article.getBodyText());
	}

	/**
	 * Save an article to a file in a json format Article must have a title
	 */
	private void write() {
		// TODO Consolidate all changes
		this.editingArticle.commit();
		// Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
		String fileName = "saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		assert articleTitle != null : "fx:id=\"articleTitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleSubtitle != null : "fx:id=\"articleSubtitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleCategory != null : "fx:id=\"articleCategory\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert pageTitle != null : "fx:id=\"pageTitle\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleImage != null : "fx:id=\"articleImage\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleAbstractText != null : "fx:id=\"articleAbstractText\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleBodyText != null : "fx:id=\"articleBodyText\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleAbstractHTML != null : "fx:id=\"articleAbstractHTML\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert articleBodyHTML != null : "fx:id=\"articleBodyHTML\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
		assert categoryMenu != null : "fx:id=\"categoryMenu\" was not injected: check your FXML file 'ArticleDetails.fxml'.";
	}

	@FXML
	void backAction(ActionEvent e) {
		Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		primaryStage.setScene(mainScene);
	}

	@FXML
	void sendBackAction(ActionEvent e) {
		Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		primaryStage.setScene(mainScene);
	}

	@FXML
	void saveFileAction(ActionEvent e) {
	}

	@FXML
	void switchTypeAction(ActionEvent e) {
		if (this.articleAbstractText.isVisible()) {
			this.articleAbstractHTML.setVisible(true);
			this.articleAbstractText.setVisible(false);
		} else if (this.articleAbstractHTML.isVisible()) {
			this.articleAbstractHTML.setVisible(false);
			this.articleAbstractText.setVisible(true);
		} else if (this.articleBodyText.isVisible()) {
			this.articleBodyHTML.setVisible(true);
			this.articleBodyText.setVisible(false);
		} else {
			this.articleBodyHTML.setVisible(false);
			this.articleBodyText.setVisible(true);
		}
	}

	@FXML
	void switchContentAction(ActionEvent e) {
		if (this.articleAbstractText.isVisible()) {
			this.articleAbstractText.setVisible(false);
			this.articleBodyText.setVisible(true);
		} else if (this.articleBodyText.isVisible()) {
			this.articleAbstractText.setVisible(true);
			this.articleBodyText.setVisible(false);
		} else if (this.articleAbstractHTML.isVisible()) {
			this.articleAbstractHTML.setVisible(false);
			this.articleBodyHTML.setVisible(true);
		} else {
			this.articleAbstractHTML.setVisible(true);
			this.articleBodyHTML.setVisible(false);
		}
	}
}
