package ca.uvic.seng330.assn3.view;

import ca.uvic.seng330.assn3.controller.Controller;
import java.util.Observable;

public abstract class Client extends Observable {

  public Client(Controller controller) {
    this.addObserver(controller);
  }
}
