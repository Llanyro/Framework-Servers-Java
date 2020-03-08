package tests;

import java.io.IOException;
import framework.templates.PlantillaServer;
import framework.classes.*;

public class ServidorInterfaz extends PlantillaServer {
	/*
	 * Constructor
	 * */
	public ServidorInterfaz(String nombreServidor, String pathServer, String api_key) {
		super(nombreServidor, pathServer, api_key);
        this.cabecera.setAcessControlAllowOrigin("*");
        this.cabecera.setServerName(this.nombreServidor);
	}
	@Override
	protected void resolverDelete() throws IOException {
		this.badRequest(ContentType.TEXT, "Not implemented", null);
	}
	@Override
	protected void resolverGet() throws IOException {
		this.ok(ContentType.TEXT, "YAY it works!!!", "Server Interface V1");
	}
	@Override
	protected void resolverPost() throws IOException {
		this.badRequest(ContentType.TEXT, "Not implemented", null);
	}
	/*
	 * Funcion de clonado de la clase(Para el uso en EjecutaServicios)
	 * */
	@Override
	public PlantillaServer clonar() {
		return new ServidorInterfaz(this.nombreServidor, this.pathServer, this.api_key);
	}
}
