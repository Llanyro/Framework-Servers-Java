package tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import framework.templates.PlantillaWebSocket;
import framework.templates.PlantillaServer;
import framework.classes.*;

import java.util.Base64;

//https://websockets.readthedocs.io/en/stable/intro.html
public class WebSocketChat extends PlantillaWebSocket {
	public WebSocketChat(String nombreServidor, String pathServer, String api_key) {
		super(nombreServidor, pathServer, api_key);
        this.cabecera.setAcessControlAllowOrigin("*");
        this.cabecera.setServerName(this.nombreServidor);
        this.cerrarConexion = false;
	}
	@Override
	protected void resolverDelete() throws IOException {
		this.badRequest(ContentType.TEXT, "Not implemented", null);
	}
	@Override
	protected void resolverGet() throws IOException {
		switch(this.peticion.get(PlantillaServer.PARAMETROS_PETICION)) {
			case "/Chat": {
				String seckey = this.peticion.get(PlantillaWebSocket.SEC_WEBSOCKET_KEY);
				String sha1 = "";
				
				
				/*seckey.concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
				
				
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-1");
			        digest.reset();
			        digest.update(seckey.getBytes("utf8"));
			        sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
				}
				catch (Exception e){
					e.printStackTrace();
					sha1 = null;
				}*/
				
				try {
					sha1 = Base64.getEncoder().
					encodeToString(MessageDigest.getInstance("SHA-1").
					digest(
					(seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").
					getBytes("UTF-8")));
				} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
					e.printStackTrace();
					sha1 = null;
				}
				
				
				if(sha1 == null)
					this.serverError(ContentType.DEFAULT, "Internal error!", null);
				else
					this.acceptWebsocketConnection(sha1);
			}
			break;
		}
	}
	@Override
	protected void resolverPost() throws IOException {
		this.badRequest(ContentType.TEXT, "Not implemented", null);
	}
	@Override
	public PlantillaServer clonar() {
		return new WebSocketChat(this.nombreServidor, this.pathServer, this.api_key);
	}
}
