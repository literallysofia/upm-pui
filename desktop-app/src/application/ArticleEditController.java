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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
	private NewsReaderController mainController;

	@FXML
	private Text pageTitle;

	@FXML
	private ImageView articleImage;

	@FXML
	private TextField articleTitle; // required

	@FXML
	private TextField articleSubtitle;

	@FXML
	private Text articleCategory; // required

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

	void setMainController(NewsReaderController c) {
		this.mainController = c;
	}

	void setCategories() {
		for (Categories category : Categories.values()) {
			MenuItem menuItem = new MenuItem(category.toString());
			menuItem.setId(category.toString());
			menuItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					articleCategory.setText(menuItem.getId());
				}
			});
			this.categoryMenu.getItems().add(menuItem);
		}
	}

	@FXML
	void onImageClicked(ActionEvent event) {

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
				articleImage.setImage(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Send and article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if the article has been saved
	 */
	private boolean send() {
		String titleText = this.articleTitle.getText();
		Categories category = Categories.valueOf(this.articleCategory.getText().toUpperCase());
		if (titleText == null || category == null || titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory",
					ButtonType.OK);
			alert.showAndWait();
			return false;
		}

		String abstractText;
		String bodyText;

		if (this.articleAbstractText.isVisible())
			abstractText = this.articleAbstractText.getText();
		else
			abstractText = this.articleAbstractHTML.getHtmlText();

		if (this.articleBodyText.isVisible())
			bodyText = this.articleBodyText.getText();
		else
			bodyText = this.articleBodyHTML.getHtmlText();

		this.editingArticle.titleProperty().set(titleText);
		this.editingArticle.subtitleProperty().set(this.articleSubtitle.getText());
		this.editingArticle.abstractTextProperty().set(abstractText);
		this.editingArticle.bodyTextProperty().set(bodyText);
		this.editingArticle.setCategory(category);

		try {
			this.editingArticle.commit();
			connection.saveArticle(getArticle());
		} catch (ServerCommunicationError e) {
			e.printStackTrace();
		}

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
		if (article != null) {
			this.pageTitle.setText("Edit Article");
			this.articleImage.setImage(article.getImageData());
			this.articleTitle.setText(article.getTitle());
			this.articleSubtitle.setText(article.getSubtitle());
			this.articleCategory.setText(article.getCategory());
			this.articleAbstractText.setText(article.getAbstractText());
			this.articleBodyText.setText(article.getBodyText());
			this.articleAbstractHTML.setHtmlText(article.getAbstractText());
			this.articleBodyHTML.setHtmlText(article.getBodyText());
		}
		this.setCategories();
	}

	/**
	 * Save an article to a file in a json format Article must have a title
	 */
	private void write() {

		String titleText = this.articleTitle.getText();

		if (titleText == null || titleText.equals("")) {
			Alert alert = new Alert(AlertType.INFORMATION,
					"You need to enter a title to save the article as a file.");
			alert.showAndWait();
		} else {
			Categories category = Categories.valueOf(this.articleCategory.getText().toUpperCase());
			String abstractText;
			String bodyText;

			if (this.articleAbstractText.isVisible())
				abstractText = this.articleAbstractText.getText();
			else
				abstractText = this.articleAbstractHTML.getHtmlText();

			if (this.articleBodyText.isVisible())
				bodyText = this.articleBodyText.getText();
			else
				bodyText = this.articleBodyHTML.getHtmlText();

			this.editingArticle.titleProperty().set(titleText);
			this.editingArticle.subtitleProperty().set(this.articleSubtitle.getText());
			this.editingArticle.abstractTextProperty().set(abstractText);
			this.editingArticle.bodyTextProperty().set(bodyText);
			this.editingArticle.setCategory(category);

			this.editingArticle.commit();

			// Removes special characters not allowed for filenames
			String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
			String fileName = "saveNews//" + name + ".news";
			JsonObject data = JsonArticle.articleToJson(this.getArticle());
			try (FileWriter file = new FileWriter(fileName)) {
				file.write(data.toString());
				file.flush();
				Alert alert = new Alert(AlertType.INFORMATION, "Your article is saved.");
				alert.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		this.editingArticle.discardChanges();
		Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		primaryStage.setScene(mainScene);
	}

	@FXML
	void sendBackAction(ActionEvent e) {
		if (this.send()) {
			Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			this.mainController.updateScene();
			primaryStage.setScene(mainScene);
		}
		return;
	}

	@FXML
	void saveFileAction(ActionEvent e) {
		System.out.println("saving...");
		write();
		System.out.println("article draft saved!");
	}

	@FXML
	void switchTypeAction(ActionEvent e) {
		if (this.articleAbstractText.isVisible()) {
			this.articleAbstractHTML.setVisible(true);
			this.articleAbstractText.setVisible(false);
			this.saveAbstract(false);
		} else if (this.articleAbstractHTML.isVisible()) {
			this.articleAbstractHTML.setVisible(false);
			this.articleAbstractText.setVisible(true);
			this.saveAbstract(true);
		} else if (this.articleBodyText.isVisible()) {
			this.articleBodyHTML.setVisible(true);
			this.articleBodyText.setVisible(false);
			this.saveBody(false);
		} else {
			this.articleBodyHTML.setVisible(false);
			this.articleBodyText.setVisible(true);
			this.saveBody(true);
		}
	}

	@FXML
	void switchContentAction(ActionEvent e) {
		if (this.articleAbstractText.isVisible()) {
			this.articleAbstractText.setVisible(false);
			this.articleBodyText.setVisible(true);
			this.saveAbstract(false);
		} else if (this.articleBodyText.isVisible()) {
			this.articleAbstractText.setVisible(true);
			this.articleBodyText.setVisible(false);
			this.saveBody(false);
		} else if (this.articleAbstractHTML.isVisible()) {
			this.articleAbstractHTML.setVisible(false);
			this.articleBodyHTML.setVisible(true);
			this.saveAbstract(true);
		} else {
			this.articleAbstractHTML.setVisible(true);
			this.articleBodyHTML.setVisible(false);
			this.saveBody(true);
		}
	}

	private void saveAbstract(boolean isHTML) {
		if (isHTML)
			this.articleAbstractText.setText(this.articleAbstractHTML.getHtmlText());
		else
			this.articleAbstractHTML.setHtmlText(this.articleAbstractText.getText());
	}

	private void saveBody(boolean isHTML) {
		if (isHTML)
			this.articleBodyText.setText(this.articleBodyHTML.getHtmlText());
		else
			this.articleBodyHTML.setHtmlText(this.articleBodyText.getText());
	}
}
