/**
 * 
 */
package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;

	@FXML
	private Button loginButton;
	@FXML
	private Button newArticleButton;
	@FXML
	private Button loadArticleButton;
	@FXML
	private ListView<String> headlineList;
	@FXML
	private MenuButton categoryMenu;
	@FXML
	private ImageView articleImage;
	@FXML
	private Text articleAbstract;
	@FXML
	private Button articleReadMore;
	@FXML
	private Button articleEdit;

	@FXML
	private Button articleDelete;
	
//	@FXML 
//	private MenuItem all;
//	
//	@FXML 
//	private MenuItem economy;
//	
//	@FXML 
//	private MenuItem international;
//	
//	@FXML 
//	private MenuItem national;
//	
//	@FXML 
//	private MenuItem sports;
//
//	@FXML 
//	private MenuItem technology;
	
	public NewsReaderController() {
		newsReaderModel.setDummyData(false);
	}

	private void getData() {
		newsReaderModel.retrieveData();

		ObservableList<Categories> categories = newsReaderModel.getCategories();
		for (int i = 0; i < categories.size(); i++) {
			MenuItem menuItem = new MenuItem(categories.get(i).toString());
			this.categoryMenu.getItems().add(menuItem);
			menuItem.setId(categories.get(i).toString());
		}

		ObservableList<Article> articles = newsReaderModel.getArticles();
		for (int i = 0; i < articles.size(); i++) {
			this.headlineList.getItems().add(articles.get(i).getTitle());
		}

		this.headlineList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				for (int i = 0; i < articles.size(); i++) {
					if (articles.get(i).getTitle() == newValue) {
						Article article = articles.get(i);
						articleAbstract.setText(article.getAbstractText());
						articleImage.setImage(article.getImageData());
						articleReadMore.setDisable(false);
						articleReadMore.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								try {
									FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
									loader.load();
									ArticleDetailsController controller = loader.<ArticleDetailsController>getController();
									controller.setArticle(article);
									articleReadMore.getScene().setRoot(loader.getRoot());
								} catch (IOException e1) {
									e1.printStackTrace();
								}	
						    }
						});
					}
				}
			}
		});
	}
	
	//Needed for filtered data in headlineList
    private FilteredList<String> filteredHeadlines;
	
	@FXML
	void onCategoriesPressed(ActionEvent event) {
		ObservableList<MenuItem> categories = this.categoryMenu.getItems();
		for (int i = 0; i < categories.size(); i++) {
			String menuItem = categories.get(i).toString();
			categories.get(i).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					filteredHeadlines.setPredicate(category -> category.equals(menuItem));
				}
			});
		}
//		for (int i = 0; i < this.headlineList.getItems().size(); i++) {
//			
//		}
	}


	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager(ConnectionManager connection) {
		this.newsReaderModel.setDummyData(false); // System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {

		this.usr = usr;
		// Reload articles
		this.getData();
		// TODO Update UI
	}

	@FXML
	void initialize() {
		assert headlineList != null : "fx:id=\"headlineList\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert categoryMenu != null : "fx:id=\"categoryMenu\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleImage != null : "fx:id=\"articleImage\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleAbstract != null : "fx:id=\"articleAbstract\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleReadMore != null : "fx:id=\"articleReadMore\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleEdit != null : "fx:id=\"articleEdit\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleDelete != null : "fx:id=\"articleDelete\" was not injected: check your FXML file 'NewsReader.fxml'.";

	}

	@FXML
	void openLogin(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
			loader.load();
			loginButton.getScene().setRoot(loader.getRoot());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Auxiliary methods
	private interface InitUIData<T> {
		void initUIData(T loader);
	}

}
