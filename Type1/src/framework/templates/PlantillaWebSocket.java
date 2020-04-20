package framework.templates;

import java.io.IOException;

import framework.classes.ContentType;
import framework.templates.PlantillaServer;

/*
 * Variacion del clasico servidor HTTP
 * */
public abstract class PlantillaWebSocket extends PlantillaServer {
	public PlantillaWebSocket(String nombreServidor, String pathServer, String api_key) {
		super(nombreServidor, pathServer, api_key);
	}
	//#region Variables
	// #region Defines
	protected static final String MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	protected static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
	protected static final String SHA_1 = "SHA-1";
	protected static final String UPGRADE_CONNECTION = "upgrade";
	protected static final String UPGRADE_TYPE = "websocket";
	protected static final String WEBSOCKET_EXTENSION = "Sec-WebSocket-Extensions";
	
	// #endregion
    // #endregion
	protected void acceptWebsocketConnection(String key) throws IOException {		
		this.cabecera.addCabeceraExtra("Sec-WebSocket-Accept: " + key);
		this.cabecera.addCabeceraExtra("Sec-WebSocket-Extensions: permessage-deflate");
		this.cabecera.setConnection(PlantillaWebSocket.UPGRADE_CONNECTION);
		this.cabecera.setUpgrade(PlantillaWebSocket.UPGRADE_TYPE);
        this.easyCabecera(PlantillaServer.SWITCHING_PROTOCOLS, "Switching Protocols", ContentType.DEFAULT, null);
	}
	
	
	
	
	
}
