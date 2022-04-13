module APP_Desktop {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	
	exports gui;
	opens gui to javafx.fxml;
	opens model.entities to javafx.graphics, javafx.fxml, javafx.base;
	opens application to javafx.graphics, javafx.fxml;
}
