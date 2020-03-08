package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import framework.templates.PlantillaServer;
import framework.server.EjecutaServicios;

public class OtroMain {
	public static byte[] leerFichero(String nombreFichero) throws FileNotFoundException, IOException {
		// Abrimos el fichero que queremos enviar
		File file = new File(new File("C:\\"), nombreFichero);
		int fileLength = (int) file.length();
		
		FileInputStream fileIn = null;
		byte[] contenido = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(contenido);
		}
		finally {
			if (fileIn != null) 
				fileIn.close();
		}
		return contenido;
	}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<PlantillaServer> listServers = new ArrayList<PlantillaServer>();
		listServers.add(new ServidorInterfaz("Server Interfaz for PAWR", "/API/", null));
		listServers.add(new WebSocketChat("Ser 2", "/WebSocket/", null));
		listServers.add(new ProxyHttp("Proxy", "http://", null));
				
		EjecutaServicios serv = new EjecutaServicios(8080, listServers);
		
		if(serv.getServidorIniciado())
			while(true)
				serv.escucharPeticiones();
		
		System.out.println("Saliendo...");
	}
}
