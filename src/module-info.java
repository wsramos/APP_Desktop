module APP_Desktop {
	requires javafx.controls;
	requires javafx.fxml;
	
	exports gui;
	opens gui to javafx.fxml;
	
	opens application to javafx.graphics, javafx.fxml;
}
