package ca.uvic.seng330.assn3.controller;

import ca.uvic.seng330.assn3.model.AccessLevel;
import ca.uvic.seng330.assn3.model.Hub;
import ca.uvic.seng330.assn3.model.UserAccount;
import ca.uvic.seng330.assn3.model.devices.Camera;
import ca.uvic.seng330.assn3.model.devices.CameraFullException;
import ca.uvic.seng330.assn3.model.devices.Device;
import ca.uvic.seng330.assn3.model.devices.Status;
import ca.uvic.seng330.assn3.view.CameraSceneBuilder;
import ca.uvic.seng330.assn3.view.Client;
import ca.uvic.seng330.assn3.view.CreateDeviceBuilder;
import ca.uvic.seng330.assn3.view.HubSceneBuilder;
import ca.uvic.seng330.assn3.view.LightbulbSceneBuilder;
import ca.uvic.seng330.assn3.view.LoginSceneBuilder;
import ca.uvic.seng330.assn3.view.ManageDevicesBuilder;
import ca.uvic.seng330.assn3.view.ManageUsersBuilder;
import ca.uvic.seng330.assn3.view.SceneBuilder;
import ca.uvic.seng330.assn3.view.SmartPlugSceneBuilder;
import ca.uvic.seng330.assn3.view.ThermostatSceneBuilder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Stack;
import java.util.UUID;
import javafx.scene.control.Alert.AlertType;

public class Controller {
  // TODO: implement observable

  private final Hub hub;
  private final Client client;
  private UserAccount activeUser;
  private final Stack<ViewType> views;
  private final SceneBuilder loginBuilder;

  /*
   * @pre hub != null
   * @pre client != null
   */
  public Controller(Hub hub, Client client) {
    assert hub != null;
    assert client != null;

    this.loginBuilder = new LoginSceneBuilder(this, "Close");
    this.activeUser = null;
    this.client = client;
    this.client.setController(this);
    this.hub = hub;
    this.views = new Stack<ViewType>();

    this.hub.startup();
    this.client.getWindow().setOnCloseRequest(event -> exitApplication());

    // Load and display login screen
    client.setView(findBuilder(ViewType.LOGIN));
    // client.setView(loginBuilder);
  }

  private void exitApplication() {
    this.hub.shutdown();
  }

  public void update(Object arg) {
    // TODO
    // this is the mega-handler to be used to delegate action to
    // the appropriate function to update the view and/or model
    // once we've got this thing functional we can see if there is
    // an easy way to re-factor it into something less god-function-esque.

    // Will need to add some argument that tells us about the button that was pressed/
    // the radio item selected/the text entered in a field.

    // Set active user on whenever an account successfully logs in, and remove it
    // whenever they log out

    // Way better idea: have a controller for each style of view we have and hold one
    // of each in this main controller

    // For now we're just going to split up handlers and see how that works out. Later on
    // we can combine some, and/or package them into various classes
  }

  public void handleBackClick() {
    if (views.peek() == ViewType.LOGIN) {
      // close window
      exitApplication();
      client.close();
    } else if (views.peek() == ViewType.HUB_BASIC || views.peek() == ViewType.HUB_ADMIN) {
      // log out
      client.setView(loginBuilder);
      views.pop();
      this.activeUser = null;
    } else {
      views.pop();
      client.setTitle(views.peek().toString());

      // TODO generate appropriate builder based on the ViewType now on the top of the stack
      client.setView(findBuilder(views.pop()));
    }

    System.out.println("Back"); // Test
  }

  /*
   * @pre view != null
   */
  private SceneBuilder findBuilder(ViewType view) {
    assert view != null;
    // TODO generate the appropriate SceneBuilder based on for the ViewType
    views.push(view);
    client.setTitle(view.toString());
    switch (view) {
      case LOGIN:
        return loginBuilder;

      case CREATE_DEVICE:
        return new CreateDeviceBuilder(this, "Back");

      case HUB_ADMIN:
        return new HubSceneBuilder(this, "Log Out", true);

      case HUB_BASIC:
        return new HubSceneBuilder(this, "Log Out", false);

      case MANAGE_DEVICES:
        return new ManageDevicesBuilder(this, "Back");

      case MANAGE_NOTIFICATIONS:
        // TODO
        break;

      case MANAGE_USERS:
        return new ManageUsersBuilder(this, "Back");

      case SELECT_NOTIFICATIONS:
        // TODO
        break;

      default:
        System.out.println("No case in controller.findBuilder() for viewType " + view);
        break;
    }
    return null;
  }

  /*
   * @pre username != null
   * @pre password != null
   */
  public void handleLoginClick(String username, String password) {
    assert username != null;
    assert password != null;
    if (hub.isUser(username)) {
      System.out.println("Logged in"); // Testing
      this.activeUser = hub.getUser(username, password);
      if (this.activeUser.isAdmin()) {
        client.setView(findBuilder(ViewType.HUB_ADMIN));
      } else {
        client.setView(findBuilder(ViewType.HUB_BASIC));
      }
    } else {
      client.alertUser(
          AlertType.INFORMATION,
          "Failure",
          "Account does not exist",
          "Username \"" + username + "\" is not in use. Please try a different one.");
    }
  }

  /*
   * @pre username != null
   * @pre password != null
   */
  public void handleNewUser(String username, String password) {
    assert username != null;
    assert password != null;
    if (acceptableInputs(username, password)) {
      if (!hub.isUser(username)) {
        hub.register(new UserAccount(this.hub, AccessLevel.BASIC, username, password));
        client.alertUser(AlertType.INFORMATION, "Success", "Success", "User Account Created.");
      } else {
        client.alertUser(
            AlertType.INFORMATION,
            "Failure",
            "Username Unavailable",
            "Username \"" + username + "\" is already in use. Please try a different one.");
      }
    }
  }

  /*
   * @pre username != null
   * @pre password != null
   */
  public void handleNewAdmin(String username, String password) {
    assert username != null;
    assert password != null;
    if (acceptableInputs(username, password)) {
      if (!hub.isUser(username)) {
        hub.register(new UserAccount(this.hub, AccessLevel.ADMIN, username, password));
        client.alertUser(AlertType.INFORMATION, "Success", "Success", "Admin Account Created.");
      } else {
        client.alertUser(
            AlertType.INFORMATION,
            "Failure",
            "Username Unavailable",
            "Username \"" + username + "\" is already in use. Please try a different one.");
      }
    }
  }

  /*
   * @pre username != null
   * @pre password != null
   */
  private boolean acceptableInputs(String username, String password) {
    assert username != null;
    assert password != null;
    if (username.toLowerCase().equals("username") || username.isEmpty()) {
      client.alertUser(
          AlertType.INFORMATION,
          "Failure",
          "Username Unaccepted",
          "Username is not allowed to be \"username\" or left blank. Please try again");
      return false;
    } else if (password.toLowerCase() == "password" || password.isEmpty()) {
      client.alertUser(
          AlertType.INFORMATION,
          "Failure",
          "Password Unaccepted",
          "Password is not allowed to be \"password\" or left blank. Please try again");
      return false;
    }
    return true;
  }

  public void handleAdminManageUsersClick() {
    System.out.println("Manage Users");
    client.setView(findBuilder(ViewType.MANAGE_USERS));
  }

  public void handleAdminManageDevicesClick() {
    System.out.println("Manage Devices");
    client.setView(findBuilder(ViewType.MANAGE_DEVICES));
  }

  public void handleAdminManageNotificationsClick() {
    System.out.println("Manage Notifications");
  }

  public ArrayList<UUID> getDeviceIDList() {
    ArrayList<UUID> refined = new ArrayList<UUID>();
    for (UUID id : hub.getIDList(true)) {
      if (!hub.getBlackList(activeUser).contains(id)) {
        refined.add(id);
      }
    }
    return refined;
  }

  /*
   * Returns a list of all *NON-ADMIN* users's UUIDs
   */
  public ArrayList<UUID> getAccountIDList() {
    ArrayList<UUID> refined = new ArrayList<UUID>();
    for (UUID id : hub.getIDList(false)) {
      refined.add(id);
    }
    return refined;
  }

  /*
   * @pre uuid != null
   */
  public String getLabel(UUID uuid) {
    assert uuid != null;
    return hub.getLabel(uuid);
  }

  /*
   * @pre uuid != null
   */
  public void handleDeviceViewClick(UUID uuid) {
    assert uuid != null;
    // TODO: review use of import of Device

    views.push(ViewType.DEVICE_VIEW);
    client.setTitle(ViewType.DEVICE_VIEW.toString());
    deviceViewSwitch(uuid);
  }

  /*
   * Allows the skipping of views.push() etc...
   */
  private void deviceViewSwitch(UUID uuid) {
    switch (DeviceType.valueOf(hub.getDevice(uuid).getClass().getSimpleName().toUpperCase())) {
      case CAMERA:
        client.setView(new CameraSceneBuilder(this, "Back", uuid));
        System.out.println("Camera View");
        break;
      case LIGHTBULB:
        client.setView(new LightbulbSceneBuilder(this, "Back", uuid));
        System.out.println("Lightbulb View");
        break;
      case SMARTPLUG:
        client.setView(new SmartPlugSceneBuilder(this, "Back", uuid));
        System.out.println("SmartPlug View");
        break;
      case THERMOSTAT:
        client.setView(new ThermostatSceneBuilder(this, "Back", uuid));
        System.out.println("Thermostat View");
        break;
    }
  }

  /*
   * @pre uuid != null
   */
  public void handleKillerClick(UUID uuid) {
    assert uuid != null;
    // TODO: add confirmation alert
    hub.unregister(uuid);
  }

  public void handleCreateDeviceClick() {
    client.setView(findBuilder(ViewType.CREATE_DEVICE));
  }

  public ArrayList<DeviceType> getDeviceTypes() {
    ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>();
    EnumSet.allOf(DeviceType.class).forEach(devType -> deviceTypes.add(devType));
    return deviceTypes;
  }

  /*
   * @pre newDevice != null
   */
  public void handleNewDeviceClick(
      DeviceType newDevice, boolean startingState, String customLabel) {
    assert newDevice != null;
    assert customLabel != null;
    hub.makeNewDevice(newDevice, startingState, customLabel);
  }

  public void toggleDevice(UUID id) {
    Device curr = hub.getDevice(id);
    if (curr.getStatus() == Status.ON) {
      curr.setStatus(Status.OFF);
    } else if (curr.getStatus() == Status.OFF) {
      curr.setStatus(Status.ON);
    } else {
      // TODO: alert that device is broken.
    }
    deviceViewSwitch(id);
  }

  public String getStatus(UUID id) {
    return hub.getDevice(id).getStatus().toString();
  }

  // ==================camera specific=====================//
  public boolean getCameraRecording(UUID id) {
    assert id != null;
    // TODO: review importing devices.camera
    return ((Camera) hub.getDevice(id)).isRecording();
  }

  public void setCameraRecording(UUID id) {
    assert id != null;
    try {
      ((Camera) hub.getDevice(id)).record();
    } catch (CameraFullException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    deviceViewSwitch(id);
  }

  public int getCurrCameraDiskSize(UUID id) {
    return ((Camera) hub.getDevice(id)).currentDiskSize();
  }

  public int getMaxCameraDiskSize(UUID id) {
    return ((Camera) hub.getDevice(id)).maxDiskSize();
  }

  public void emptyCameraDiskSize(UUID id) {
    ((Camera) hub.getDevice(id)).emptyDisk();
    deviceViewSwitch(id);
  }
  // ==================camera specific=====================//
}
