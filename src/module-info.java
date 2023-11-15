/**
 * 
 */
/**
 * 
 */
module BlackJack {
	requires java.desktop;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    exports FinalProject; //required for javafx @FXML
    opens FinalProject; //required for javafx @FXML
}