module Blackjack {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.sql;
	
	opens project to javafx.fxml;
	exports project;
}
