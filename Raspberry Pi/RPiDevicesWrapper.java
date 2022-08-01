package es.upm.dte.iot.devices.raspberry;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.upm.dte.iot.devices.TempHumObservation;
import es.upm.dte.iot.devices.ColorObservation;
import es.upm.dte.iot.devices.LluviaObservation;

public class RPiDevicesWrapper {
	
	private static RPiDevicesWrapper rpiDevices = null;
	private static Log logger = LogFactory.getLog(RPiDevicesWrapper.class); 
	private final static String LIB_NAME_KEY = "library.name";
	private final static String LEDR_KEY = "pinLEDR";
	private final static String LEDB_KEY = "pinLEDB";
	private final static String LEDG_KEY = "pinLEDG";
	private final static String BUTTON_KEY = "pinBUTTON";

	
	public static RPiDevicesWrapper initialize(String libConfigName ) {
		
		Configuration properties = null;
		int pinLEDR = 0, pinLEDG = 0, pinLEDB = 0, pinBUTTON = 0;
		
		if ( libConfigName == null )
			throw new IllegalArgumentException("RPiDevicesWrapper.initialize: null parameter.");
		
		if ( rpiDevices != null )
			throw new IllegalStateException("RPiDevicesWrapper.initialize: already initialized." );
		
		try {
			properties = new Configurations().properties(libConfigName);
			try {
				System.loadLibrary(properties.getString(LIB_NAME_KEY, ""));
			}catch (UnsatisfiedLinkError | SecurityException e) {
				logger.warn("initialize : impossible to load native library.");
				if ( logger.isDebugEnabled()) {
					e.printStackTrace();
				}
				return null;
			}
			pinLEDR = properties.getInt(LEDR_KEY, 0);
			pinLEDG = properties.getInt(LEDG_KEY, 0);
			pinLEDB = properties.getInt(LEDB_KEY, 0);
			pinBUTTON = properties.getInt(BUTTON_KEY, 0);
		}catch( ConfigurationException e) {
			throw new IllegalArgumentException( "RPiDevicesWrapper.initialize: error when loading library configuration '"+libConfigName+"': "+e.getLocalizedMessage());
		}
		
		if ( pinLEDB < 0 || pinLEDB > 16 ||
			 pinLEDG < 0 || pinLEDG > 16 ||
			 pinLEDR < 0 || pinLEDR > 16 ||
			 pinBUTTON < 0 || pinBUTTON > 32) {
			throw new IllegalArgumentException("RPiDevicesWrapper.initialize: configuration parameters are wrong.");
		}
		
		if ( rpiDevices == null ) {
			rpiDevices = new RPiDevicesWrapper(pinLEDR, pinLEDG, pinLEDB, pinBUTTON);
		}
		return rpiDevices;
	}
	
	public static RPiDevicesWrapper getInstance(){
		if ( rpiDevices == null ) {
			logger.warn("getInstance : no access to library with native code.");
			return null;
		}
		return rpiDevices;
	}
	private RPiDevicesWrapper(int pinLEDR, int pinLEDG, int pinLEDB, int pinBUTTON) {
		logger.debug("Intializing RPiDevicesWraapper.");
		initResources(pinLEDR, pinLEDG, pinLEDB, pinBUTTON);
		logger.debug("RPiDevicesWraapper initialized.");
	}
	
	private native void initResources(int pinLEDR, int pinLEDG, int pinLEDB, int pinBUTTON);
	public native TempHumObservation readFakeTempRH();
	public native TempHumObservation readSi7021();
	public native void setLCDBackLight(short red, short green, short blue);
	public native void setLCDText(String text);
	public native void setRGB( short red, short green, short blue );
	public native ColorObservation readColor();
	public native LluviaObservation getPrecipitacion();
}
