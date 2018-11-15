package ca.uvic.seng330.assn3.controller;

import ca.uvic.seng330.assn3.model.Hub;
import ca.uvic.seng330.assn3.model.devices.Temperature;
import ca.uvic.seng330.assn3.model.devices.Temperature.TemperatureOutofBoundsException;
import ca.uvic.seng330.assn3.model.devices.Temperature.Unit;
import ca.uvic.seng330.assn3.model.devices.Thermostat;
import ca.uvic.seng330.assn3.view.Client;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

public class ThermostatController extends BasicDeviceController {

  public ThermostatController(Hub hub, Client client) {
    super(hub, client);
    // TODO Auto-generated constructor stub
  }

  public ArrayList<Unit> getThermostatDegreeTypes() {
    ArrayList<Unit> degreeType = new ArrayList<Unit>();
    EnumSet.allOf(Unit.class).forEach(type -> degreeType.add(type));
    return degreeType;
  }

  public void setThermostatTemp(UUID id, double magnitude, Object degreeType) {
    assert id != null;
    assert degreeType != null;
    try {
      ((Thermostat) hub.getDevice(id)).setTemp(new Temperature(magnitude, (Unit) degreeType));
    } catch (TemperatureOutofBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    deviceViewSwitch(id);
  }

  public double getThermostatTempMag(UUID id) {
    assert id != null;
    return ((Thermostat) hub.getDevice(id)).getTempMag();
  }

  public String getThermostatTempType(UUID id) {
    assert id != null;
    return String.valueOf(((Thermostat) hub.getDevice(id)).getTempType());
  }

  public void changeThermostatDegreeType(UUID id) {
    Thermostat thermostat = ((Thermostat) hub.getDevice(id));
    // TODO: handle temp out of bound exceptions
    // TODO: set to max or min acceptable?
    try {
      thermostat.convertTemp();
    } catch (TemperatureOutofBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    deviceViewSwitch(id);
  }

  public void constructTemp(UUID id, String newTempMag, Unit degree) {
    try {
      setThermostatTemp(id, Double.parseDouble(newTempMag), degree);
    } catch (NumberFormatException e) {
      // TODO: alert to missing textfield
    }
  }
}
