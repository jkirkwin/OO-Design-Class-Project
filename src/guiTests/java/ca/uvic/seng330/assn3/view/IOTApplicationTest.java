package ca.uvic.seng330.assn3.view;

import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.testfx.framework.junit.ApplicationTest;

import ca.uvic.seng330.assn3.controller.Controller;
import ca.uvic.seng330.assn3.model.Hub;
import ca.uvic.seng330.assn3.startup.Startup;
import javafx.stage.Stage;

public class IOTApplicationTest extends ApplicationTest {
  
  Hub hub;
  Controller controller;
  Client client;
  Startup app;
  
  @SuppressWarnings("unused")
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.app = new Startup();
    this.app.start(primaryStage);
    this.hub = (Hub) GUITestUtilities.getAccessibleField(app, "hub").get(app);
    this.client = (Client) GUITestUtilities.getAccessibleField(app, "client").get(app);
    this.controller = (Controller) GUITestUtilities.getAccessibleField(app, "controller").get(app);
  }
  
  
  //   TODO Set up a testing directory so that the state of the model is not deleted when these
  // tests run
  @Before
  @After
  public void clearState() {
    try {
      GUITestUtilities.deleteState();
    } catch (NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
