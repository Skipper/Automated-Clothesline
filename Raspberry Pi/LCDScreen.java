package es.upm.dte.iot.devices.raspberry;

import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import es.upm.dte.iot.devices.Device;
import es.upm.dte.iot.devices.JSONSchema4LCDParams;
//import es.upm.dte.iot.devices.JSONSchema4ledRGBParams;
import es.upm.dte.iot.platform.ICommandListener;
import es.upm.dte.iot.platform.JSONSchema4Command;

public class LCDScreen extends Device implements ICommandListener{
	
	private final short RED_BY_DEFAULT = 255;
	private final short GREEN_BY_DEFAULT = 184;
	private final short BLUE_BY_DEFAULT = 100;
	private final short RED = 10;
	private final short GREEN_BOLD = 255;
	private final short BLUE = 10;
	private final short RED_BOLD = 255;
	private final short GREEN = 10;
	private final short BLUE_BOLD = 255;
	
	private final short MAX_COL = 16;
	
	private final String WRITE_ON_LCD_COMMAND = "writeMsg";
	
	private Log logger = LogFactory.getLog(LCDScreen.class); 
	
	private StringBuilder screen = null;
	
	private void initScreen() {
		screen = new StringBuilder();
		for ( int i = 0; i < MAX_COL * 2; i++)
			screen.append(' ');
		rpiDevices.setLCDText(screen.toString());
	}

	private void setLCDBackLight(short red, short green, short blue) {
		rpiDevices.setLCDBackLight( red, green, blue);
	}
	
	private void clearScreen() {
		String []lines = {" ", " "};
		writeOnscreen(lines);
	}
	
	private void writeOnscreen( String []lines ) {
		logger.trace("writeOnscreen : Composing message.");
		for ( int i = 0; i < 2; i++) {
			if (lines[i] == null ) {
				logger.debug("writeOnscreen : wrong json syntax: null element in 'lines' array (postion "+i+").");
				for ( int j = 0; j < MAX_COL; j++)
					screen.setCharAt(i*MAX_COL+j, ' ' );
			}else {
				String message;
				try {
					message = new String(lines[i].getBytes(),"UTF-8");
					screen.replace(i*MAX_COL, i*MAX_COL+message.length(), message);
					for ( int j = i * MAX_COL + message.length(); j < MAX_COL*(i+1);j++) {
						screen.setCharAt(j, ' ');
					}
				} catch (UnsupportedEncodingException e) {
					logger.error("writeOnScreen : string bad encoded: "+e.getLocalizedMessage());
					if ( logger.isErrorEnabled())
						e.printStackTrace();
				}
			}
		}
	}
	
	private void setRGB(short r, short g, short b) {
		// TODO Auto-generated method stub
		rpiDevices.setRGB(r, g, b);
	}

	@Override
	public void processCommand(JSONSchema4Command command) {
		if ( command == null )
			throw new IllegalArgumentException("LCDScreen.processCommand : null parameter.");
				
		logger.debug( "processCommand : new command -> "+command.toString());
		if ( command.getCommandID().equalsIgnoreCase(WRITE_ON_LCD_COMMAND)) {
			logger.trace("processCommand : write on LCD command.");
			act(command.getParamsAsJSON());
		}
		logger.trace("processCommand : command processed.");
	}
	
	@Override
	public void initialize(String filename) {
		logger.debug("initialize : initializing LCD screen.");
		super.initialize(filename);
		initScreen();
		setLCDBackLight( RED_BY_DEFAULT, GREEN_BY_DEFAULT, BLUE_BY_DEFAULT);
		setRGB((short)255, (short)255, (short)255);
	}


	@Override
	public void stop() {
		logger.debug("initialize : stopping LCD screen.");
		super.stop();
		clearScreen();
		rpiDevices.setLCDText(screen.toString());
		setLCDBackLight( RED_BY_DEFAULT, GREEN_BY_DEFAULT, BLUE_BY_DEFAULT);
		setRGB((short)0, (short)0, (short)0);
	}
	
	@Override
	public void act(String actuation) {
		
		logger.debug("act : new actuation -> "+actuation);
		try {
			JSONSchema4LCDParams newActuation = new Gson().fromJson(actuation, JSONSchema4LCDParams.class);
			String []state = newActuation.getState();
			String bgColour = newActuation.getBGColour();
			if ( bgColour == null ) {
				logger.warn("act : no background colour.");
				return;
			}
			if ( state == null ) {
				logger.warn("act : no elements in 'state' array.");
				return;
			}
			
			if ( state.length != 2 ) {
				logger.warn("act : wrong number of elements in 'state' array.");
				return;
			}
			
			writeOnscreen(state);
			
			if ( logger.isDebugEnabled()) {
				String message = screen.toString();
				logger.debug("act : Message ("+message.length()+" characters): '"+message+"'");
			}
			
			rpiDevices.setLCDText(screen.toString());
			
			
			logger.debug("act : the background color is : "+bgColour);
			
			if ( bgColour.compareToIgnoreCase("GREEN")==0) {
				setLCDBackLight(RED, GREEN_BOLD, BLUE);
				setRGB((short)0, (short)255, (short)0);
			}
			if ( bgColour.compareToIgnoreCase("RED")==0) {
				setLCDBackLight(RED_BOLD, GREEN, BLUE);
				setRGB((short)255, (short)0, (short)0);
				/*Cortamos ejecución una vez retraido?
				 * try        
				{
				    Thread.sleep(10000);
				    System.exit(0);
				} 
				catch(InterruptedException ex) 
				{
				    System.exit(0);
				}*/
			}
			if ( bgColour.compareToIgnoreCase("BLUE")==0) {
				setLCDBackLight(RED, GREEN, BLUE_BOLD);
				setRGB((short)0, (short)0, (short)255);
			}
			logger.trace("act : actuation completed.");
		}catch ( JsonSyntaxException e) {
			logger.error("act : wrong JSON syntax for actuation -> "+e.getLocalizedMessage());
			return;
		}catch ( NumberFormatException e) {
			logger.error("act : wrong LCD line.");
			return;
		}
	}

	@Override
	public void run() {
		boolean salir = false;
		platform.registerCommandListener(this);
		do {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.trace("run : thread interrupted. Exiting.");
				salir = true;
			}
		}while (!salir);
		platform.registerCommandListener(null);
	}
}
