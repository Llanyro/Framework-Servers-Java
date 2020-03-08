package tests;

import java.io.IOException;
import framework.templates.PlantillaServer;
import framework.classes.*;

public class ProxyHttp extends PlantillaServer{
	public ProxyHttp(String nombreServidor, String pathServer, String api_key) {
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
		this.ok(ContentType.TEXT, "YAY it works!!!", "Estas en el proxy HTTP");
	}
	@Override
	protected void resolverPost() throws IOException {
		this.badRequest(ContentType.TEXT, "Not implemented", null);
	}
	@Override
	public PlantillaServer clonar() {
		return new ProxyHttp(this.nombreServidor, this.pathServer, this.api_key);
	}
}
