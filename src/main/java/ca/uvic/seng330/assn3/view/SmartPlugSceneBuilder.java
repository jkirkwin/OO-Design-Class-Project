package ca.uvic.seng330.assn3.view;

import ca.uvic.seng330.assn3.controller.Controller;
import java.util.UUID;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class SmartPlugSceneBuilder extends DeviceSceneBuilder {

  UUID deviceID;

  public SmartPlugSceneBuilder(Controller controller, String backText, UUID id) {
    super(controller, backText);
    this.deviceID = id;
  }

  @Override
  protected Node buildSpecifics() {
    HBox basics = basicBuild(deviceID);

    return basics;
  }
}