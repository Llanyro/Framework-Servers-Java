package framework.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import framework.templates.PlantillaServer;
// Esto puede que no debiese de estar aqui, o tal vez si?
import framework.classes.Cabecera;

public class EjecutaServicios {
	//#region Variables
	List<PlantillaServer> servicios = null;
	protected ServerSocket socketFD = null;
	protected boolean servidorIniciado = false;
	// #endregion
    //#region Getters
	public boolean getServidorIniciado() { return this.servidorIniciado; }
	//#endregion
	//#region Funciones publicas
	public EjecutaServicios(int puerto, List<PlantillaServer> listaServicios) {
        try {
            this.servidorIniciado = true;
    		this.socketFD = new ServerSocket(puerto);
    		this.servicios = listaServicios;
		}
    	catch (IOException e) {
    		this.servicios = null;
			this.servidorIniciado = false;
			this.servicios = null;
            e.printStackTrace();
		}
	}
	public void escucharPeticiones() {
		Socket socket = null;
		BufferedReader entrada = null;
	    // Escuchamos el puerto
	    try {
			socket = this.socketFD.accept();
			entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String primeraLinea = entrada.readLine();
			//System.out.println(primeraLinea);
			
			// Si la primera linea de peticion no es correcta, cerramos la conexion
			if(primeraLinea == null)
				throw new IOException();
			if(!primeraLinea.contains("HTTP/1.1"))
				socket.close();
			PlantillaServer serv = null;
			
			//System.out.println("Servidor llamado: " + pathServer);
			for (int i = 0; i < this.servicios.size(); i++) {
				if(primeraLinea.contains(this.servicios.get(i).getPathServer())) {
					serv = this.servicios.get(i).clonar();
					serv.inicializarObjetosRestantes(primeraLinea, socket, entrada);
					// Añadimos los parametros
					serv.start();
					i = this.servicios.size();
				}
			}
			
			if(serv == null) {
				// Descomentar para ver solicitudes que no entran en los servicios
				boolean continuar = true;
				System.out.println(primeraLinea);
				String buff = "";
				while(continuar) {
					try {
						buff = entrada.readLine();
						if(buff.length() > 0)
							System.out.println(buff);
						else
							continuar = false;
					}
					catch (IOException | NullPointerException e) { continuar = false; }
				}
				System.out.print("\n");
				// Creamos una cabecera de error:
				Cabecera cab = new Cabecera();
				cab.setResponse(404, "File not Found!");
				System.out.println(cab.toString());
				byte[] contenido = cab.toString().getBytes();
				BufferedOutputStream respuesta = new BufferedOutputStream(socket.getOutputStream());
				respuesta.write(contenido, 0, contenido.length);
				respuesta.flush();
				socket.close();
			}
	    }
    	catch (IOException e) { e.printStackTrace(); }
	}
    //#endregion
}
