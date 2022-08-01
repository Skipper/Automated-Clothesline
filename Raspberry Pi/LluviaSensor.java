package es.upm.dte.iot.devices.raspberry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import es.upm.dte.iot.devices.Device;
import es.upm.dte.iot.devices.LluviaObservation;
import es.upm.dte.iot.devices.TempHumObservation;
import es.upm.dte.iot.devices.ColorObservation;

public class LluviaSensor extends Device{
	private Log logger = LogFactory.getLog(LluviaSensor.class); 
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		TempHumObservation observation = null;
		LluviaObservation observation3 = null;
		ColorObservation observation4 = null;
		
		boolean salir = false;
		
		logger.trace("run : starting lluvia sensing.");
		do {
			try {
				Thread.sleep(30000);
				logger.trace("run : getting observation.");
				
				String measure_completa = "";
				
				observation = rpiDevices.readSi7021();
				String measure = new Gson().toJson(observation);
				logger.debug("run : observation -> temperature = "+observation.getTemperature()+"; humidity = "+observation.getHumidity());
				logger.debug("run : observation sent ->'"+measure+"'.");
				//platform.publishMeasures(measure);
				measure_completa = "\"temperature\" : "+observation.getTemperature()+", \"humidity\" : "+observation.getHumidity();
				
				observation3 = rpiDevices.getPrecipitacion();
				String measure3 = new Gson().toJson(observation3);
				logger.debug("run : observation 3 -> lluvia = "+observation3.getLluvia());
				logger.debug("run : observation 3 sent ->'"+measure3+"'.");
				//platform.publishMeasures(measure3);
				measure_completa = measure_completa+", \"lluvia\" : "+observation3.getLluvia();
				
				
				observation4 = rpiDevices.readColor();
				int clearC = observation4.getClear();
				String measure4 = "{"+measure_completa+", \"clear\":"+clearC+"}";
				
				//String measure = new Gson().toJson(observation);
				
				logger.debug("run : observation sent ->'"+measure4+"'.");
				platform.publishMeasures(measure4);
				
				logger.trace("run : message has been sent.");
				logger.trace("run : message 2 has been sent.");
			} catch ( InterruptedException e) {
				logger.trace("run : thread interrupted. Exiting.");
				salir = true;				
			} catch (Exception e) {
				salir = true;
				logger.trace( "run : [EXCEPTION] "+e.getLocalizedMessage());
				if ( logger.isDebugEnabled())
					e.printStackTrace();
			}
		} while(!salir);
		
	}

	@Override
	public void act(String actuation) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("LluviaSensor.act not implemented.");
	}

}
