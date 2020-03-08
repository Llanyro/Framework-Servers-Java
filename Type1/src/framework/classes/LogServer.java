package framework.classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

/*
 * Clase para generar logs a traves de las peticiones dadas
 * */
public class LogServer {
	private static final String LOGS_ROOT_STR = "./Logs/";
	
	private String nombreFichero = null;
	private String threadName = null;
	
	public LogServer(String threadName) {
		this.threadName = threadName;
		LocalDateTime today = LocalDateTime.now();
		this.nombreFichero = (LogServer.LOGS_ROOT_STR + this.threadName + "_" + 
				today.toString() + ".txt").replace(':', '_');
	}
	public void agregarPeticion(HashMap<String, String> peticion) throws IOException {
	    BufferedWriter writer = 
	    		new BufferedWriter(new FileWriter(this.nombreFichero, true));
		
		if(peticion != null) {
			for (String linea: peticion.keySet()) {
		    	writer.write(linea + ": " + peticion.get(linea));	 
		    	writer.write("\n");
			}
			writer.write("\n");
		}
	    else
	    	writer.write("Peticion: null\n");
	    writer.close();
	}
	public void agregarRespuesta(String respuesta) throws IOException {
	    BufferedWriter writer = 
	    		new BufferedWriter(new FileWriter(this.nombreFichero, true));
		
	    if(respuesta != null) {
	    	writer.write(respuesta + "\n");
	    	writer.write("\n");
	    }
	    else
	    	writer.write("Respuesta: null\n");

	    writer.close();
	}
	public String getLogName() { return this.nombreFichero; }
}
