/**
 * 
 */
package application;

import java.io.File;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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


	// TODO add attributes and methods as needed

	@FXML
	private ListView<String> titlesList;

	@FXML
	private MenuButton categoryMenu;

	@FXML
	private ImageView articleImg;

	@FXML
	private Label abstractText;


	public NewsReaderController() {
		// Uncomment next sentence to use data from server instead dummy data
		newsReaderModel.setDummyData(false);
		// Get text Label

	}

	private void getData() {
		// TODO retrieve data and update UI
		// The method newsReaderModel.retrieveData() can be used to retrieve data
		newsReaderModel.retrieveData();
		ObservableList<Article> articles = newsReaderModel.getArticles();
		for (int i = 0; i < articles.size(); i++) {
			this.titlesList.getItems().add(articles.get(i).getTitle());
		}
		
		this.articleImg.setImage(articles.get(0).getImageData());
		
		this.titlesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {    
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				for(int i = 0; i < articles.size(); i++) {			
					if(articles.get(i).getTitle() == newValue) {
						abstractText.setText(articles.get(i).getAbstractText());
						articleImg.setImage(articles.get(i).getImageData());
					}
				}
		    }
		});
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
		assert titlesList != null : "fx:id=\"titlesList\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert categoryMenu != null : "fx:id=\"categoryMenu\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert articleImg != null : "fx:id=\"articleImg\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert abstractText != null : "fx:id=\"abstractText\" was not injected: check your FXML file 'NewsReader.fxml'.";

	}

	// Auxiliary methods
	private interface InitUIData<T> {
		void initUIData(T loader);
	}
	

}
