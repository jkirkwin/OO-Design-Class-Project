package ca.uvic.seng330.assn3.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.uvic.seng330.assn3.controller.Controller;
import ca.uvic.seng330.assn3.model.AccessLevel;
import ca.uvic.seng330.assn3.model.Hub;
import ca.uvic.seng330.assn3.model.UserAccount;
import ca.uvic.seng330.assn3.startup.Startup;
import java.lang.reflect.InvocationTargetException;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class TestLogin extends IOTApplicationTest {

  @Test
  public void testAdminLogin() throws InterruptedException {
    new UserAccount(hub, AccessLevel.ADMIN, "user", "pw");
    clickOn("#username");
    write("user");
    clickOn("#password");
    write("pw");
    clickOn("Login");
    assertEquals(client.getWindow().getTitle(), "HUB_ADMIN");
  }

  @Test
  public void testBasicLogin() {
    new UserAccount(hub, AccessLevel.BASIC, "user", "pw");
    clickOn("#username");
    write("user");
    clickOn("#password");
    write("pw");
    clickOn("Login");
    assertEquals(client.getWindow().getTitle(), "HUB_BASIC");
  }

  @Test
  public void testNewAdminUser() {
    clickOn("#username");
    write("admin");
    clickOn("#password");
    write("1");
    clickOn("#new_admin");
    clickOn("OK");
    assertTrue(hub.isUser("admin"));
    UserAccount u = hub.getUser("admin", "1");
    assertTrue(u.isAdmin());
    assertTrue(hub.isRegisteredUserAccount(u.getIdentifier()));
  }

  @Test
  public void testNewBasicUser() {
    GUITestUtilities.makeBasicUser(this, "user1", "password1");
    assertTrue(hub.isUser("user1"));
    UserAccount u = hub.getUser("user1", "password1");
    assertFalse(u.isAdmin());
    assertTrue(hub.isRegisteredUserAccount(u.getIdentifier()));
  }
}
