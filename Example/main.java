package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import framework.server.EjecutaServicios;
import framework.templates.PlantillaServer;
import gestores.GestorCorreo;
import server.threads.ServidorInterfazAPI;
import test.Lector;

public class MainServer {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String[] s = (new String(Lector.getInstancia().leerFichero("C:\\FileServer\\eh.pem"))).split("\r\n");
		GestorCorreo.getInstancia().setPassYCorreo(s[1], s[0]);
		
		List<PlantillaServer> listServers = new ArrayList<PlantillaServer>();
		listServers.add(new ServidorInterfazAPI("Server Interfaz for PAWR", "/API/", new String(Lector.getInstancia().leerFichero("C:\\FileServer\\api.pem"))));
				
		EjecutaServicios serv = new EjecutaServicios(9090, listServers);
		
		if(serv.getServidorIniciado())
			while(true)
				serv.escucharPeticiones();
		
		System.out.println("Saliendo...");
	}
}
