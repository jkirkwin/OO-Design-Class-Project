package ca.uvic.seng330.assn3.model.devices;

import ca.uvic.seng330.assn3.model.Hub;
import ca.uvic.seng330.assn3.model.devices.Temperature.TemperatureOutofBoundsException;

public class Thermostat extends Device {

  private static final Temperature MIN_CELSIUS = new Temperature(-10.0, Unit.CELSIUS);
  private static final Temperature MAX_CELSIUS = new Temperature(40.0, Unit.CELSIUS);
  private static final Temperature MIN_FAHRENHEIT = new Temperature(0.0, Unit.FAHRENHEIT);
  private static final Temperature MAX_FAHRENHEIT = new Temperature(110.0, Unit.FAHRENHEIT);

  private Temperature temp;

  public Thermostat(Hub hub) {
    super(hub);
    this.temp = null;
//    getHub().log("Created new Thermostat", Level.INFO, getIdentifier());
  }

  /**
   * @pre pTemp != null
   * @param pTemp
   * @return
   */
  private boolean isValidTemp(Temperature pTemp) {
    assert pTemp != null;
    double pMagnitude = pTemp.getMagnitude();
    switch (pTemp.getUnit()) {
      case CELSIUS:
        return MIN_CELSIUS.getMagnitude() <= pMagnitude && pMagnitude <= MAX_CELSIUS.getMagnitude();
      case FAHRENHEIT:
        return MIN_FAHRENHEIT.getMagnitude() <= pMagnitude
            && pMagnitude <= MAX_FAHRENHEIT.getMagnitude();
      default:
        return false;
    }
  }

  public Temperature getTemp() {
    return this.temp.clone();
  }

  public Thermostat clone() {
    Thermostat aClone = (Thermostat) super.clone();
    aClone.temp = (Temperature) this.temp.clone();
    return aClone;
  }

  /** @pre pTemp != null */
  public void setTemp(Temperature temp) throws TemperatureOutofBoundsException {
    assert temp != null;
//    getHub().log("Setting temp to " + pTemp, Level.INFO, getIdentifier());
    if (!isValidTemp(temp)) {
//      getHub().log("invalid temperature given", Level.ERROR, getIdentifier());
      throw (temp.new TemperatureOutofBoundsException());
    }
    this.temp = temp.clone();
  }
}