module ui {
  requires cognition.core;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;

  requires transitive com.google.gson;
  requires java.net.http;

  requires de.jensd.fx.glyphs.fontawesome;

  opens ui to javafx.graphics, javafx.fxml;

}