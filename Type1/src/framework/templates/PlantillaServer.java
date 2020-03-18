package framework.templates;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
//import java.net.URLDecoder;
//import java.nio.charset.Charset;
import java.util.HashMap;

import framework.classes.Cabecera;
import framework.classes.LogServer;
import framework.classes.ContentType;
import tester.Tester;

public abstract class PlantillaServer extends Thread {
    //#region Variables
	//#region Defines
	protected static final String GET = "GET";
	protected static final String POST = "POST";
	protected static final String DELETE = "DELETE";
	protected static final String UTF_8 = "utf8";
	
	protected static final int SWITCHING_PROTOCOLS = 101;
	protected static final int OK = 200;
	protected static final int BAD_REQUEST = 400;
	protected static final int FORBIDDEN = 403;
	protected static final int NOT_FOUND = 404;
	protected static final int METODO_INVALIDO = 405;
	protected static final int UPDATE_CONFLICT = 409;
	protected static final int SOY_UNA_TETERA = 418;
	protected static final int SERVER_ERROR = 500;

	
	protected static final String CARACTERES_CONTENIDO_POST = "Content-Length";
	
    protected static final String PETICION = "Peticion";
    protected static final String COOKIE = "Cookie";
    protected static final String PARAMETROS_PETICION = "Parametros Peticion";
    protected static final String VERSION = "Version";
    
	protected static final String API_KEY2 = "Api_Key";
	protected static final String API_KEY = "api_key";
	protected static final String PARAMETROS_URL = "Parametros-url";
	protected static final String CONTENIDO = "Contenido";
	// #endregion
	// #region Obejtos de resolucion de solicitudes
	protected Socket clientSocket = null;
	protected BufferedReader entrada = null;
	protected BufferedOutputStream respuesta = null;
	protected Cabecera cabecera = new Cabecera();
	// #endregion
    // #region Normales
	protected LogServer log = null;
	protected String pathServer = null;
	protected String api_key = null;
	protected String nombreServidor = null;
	protected boolean cerrarConexion = true;
	protected HashMap<String, String> peticion = new HashMap<String, String>();
	protected HashMap<String, String> cookieList = new HashMap<String, String>();
	//#endregion
    //#endregion
    //#region Constructores y getters
    public PlantillaServer(
        String nombreServidor,
        String pathServer,
        String api_key) {
		if(pathServer == null || nombreServidor == null)
			throw new NullPointerException();
		this.pathServer = pathServer;
		this.api_key = api_key;
		this.nombreServidor = nombreServidor;
	}
	public void inicializarObjetosRestantes(
        String primeraLinea,
        Socket clientSocket,
        BufferedReader entrada) throws IOException {
		this.clientSocket = clientSocket;
		this.entrada = entrada;
		this.respuesta = new BufferedOutputStream(this.clientSocket.getOutputStream());
		this.agregarPrimeraLiena(primeraLinea);
	}
	public String getPathServer() { return this.pathServer; }
    //#endregion
    //#region Funciones abstractas
	protected abstract void resolverDelete() throws IOException;
	protected abstract void resolverGet() throws IOException;
	protected abstract void resolverPost() throws IOException;
	public abstract PlantillaServer clonar();
	//#endregion
	//#region Herramientas
	//#region Private
	/*
	 * Analiza la primera parte de la peticion del cliente
	 * En caso de tener la apikey en la cabecera (Solo en GET) la guarda ya directamente
	 * */
	private void agregarPrimeraLiena(String primeraLinea) throws NumberFormatException, IOException {
        String[] primerSplit = primeraLinea.split(" ");
        // Añadimos el GET, POST o lo que sea
        this.peticion.put(PlantillaServer.PETICION, primerSplit[0]);
        this.peticion.put(PlantillaServer.VERSION, primerSplit[2]);

        if(primerSplit[0].equalsIgnoreCase("GET")) {
            // Significa que tiene parametros de entrada
            if(primerSplit[1].contains("?")) {
                String[] parametros = primerSplit[1].split("\\?");
                // Dividimos la direccion del servidor y la acccion a realizar
                String[] urlDividida = parametros[0].split("/");
                if(urlDividida.length < 3)
                    this.peticion.put(PlantillaServer.PARAMETROS_PETICION, parametros[0]);
                else {
                    StringBuffer res = new StringBuffer();
                    for(int i = 2; i < urlDividida.length; i++) {
                        res.append("/" + urlDividida[i]);
                    }
                    this.peticion.put(PlantillaServer.PARAMETROS_PETICION, res.toString());
                }
                // Añadimos los parametos
                this.guardarParametrosPeticion(parametros[1]);
            }
            else {
                // Dividimos la direccion del servidor y la acccion a realizar
                String[] urlDividida = primerSplit[1].split("/");
                if(urlDividida.length < 3)
                    this.peticion.put(PlantillaServer.PARAMETROS_PETICION, primerSplit[1]);
                else {
                    StringBuffer res = new StringBuffer();
                    for(int i = 2; i < urlDividida.length; i++) {
                        res.append("/" + urlDividida[i]);
                    }
                    this.peticion.put(PlantillaServer.PARAMETROS_PETICION, res.toString());
                }
            }
        }
        else {
            // Dividimos la direccion del servidor y la acccion a realizar
            String[] urlDividida = primerSplit[1].split("/");
            if(urlDividida.length < 3)
                this.peticion.put(PlantillaServer.PARAMETROS_PETICION, primerSplit[1]);
            else {
                StringBuffer res = new StringBuffer();
                for(int i = 2; i < urlDividida.length; i++) {
                    res.append("/" + urlDividida[i]);
                }
                this.peticion.put(PlantillaServer.PARAMETROS_PETICION, res.toString());
            }
        }
    }
    private void guardarParametrosPeticion(String param) {
    	System.out.println(param);
        // Añadimos los parametos
        String[] parametrosGet = param.split("&");
        for(int i = 0; i < parametrosGet.length; i++) {
            String[] val = parametrosGet[i].split("=");
            if(val.length == 2)
            	if(val[0].length() > 0 && val[1].length() > 0)
            		this.peticion.put(val[0], val[1]);
        }
    }
	/*
	 * Lee el resto de la peticion enviada por el cliente
	 * */
	private void leerPeticionCliente() {
		String buff = "";
		boolean continuar = true;
		while(continuar) {
			try {
				buff = this.entrada.readLine();
				if(buff.length() > 0) {
					String[] spliteado = buff.split(": ");
					if(spliteado.length == 2)
						this.peticion.put(spliteado[0], spliteado[1]);
					else
						System.out.println( "Error parametro: " + buff);
				}
				else continuar = false;
			}
			catch (IOException | NullPointerException e) { continuar = false; }
		}
		
		String cookieString = this.peticion.get(PlantillaServer.COOKIE);
		if (cookieString != null) {
			String[] cookies = cookieString.split("; ");
			String[] cookie = null;
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i].split("=");
				if(cookie.length == 2)
					this.cookieList.put(cookie[0], cookie[1]);
			}
			this.peticion.remove(PlantillaServer.COOKIE);
		}
	}
	private void leerPost() throws IOException {
        String car = this.peticion.get(PlantillaServer.CARACTERES_CONTENIDO_POST);
        if (car != null){
            int num = Integer.parseInt(car);
            StringBuilder post = new StringBuilder();
            for (int i = 0; i < num; i++) {
                post.append((char)this.entrada.read());
            }
            if(post.length() > 0)
                this.guardarParametrosPeticion(post.toString());
        }
    }
    /*
	 * Envia datos en forma de bytes al cliente
	 * Fuerza la llegada de bytes
	 * */
	private void enviarBytes(byte[] contenido) throws IOException {
		this.respuesta.write(contenido, 0, contenido.length);
		this.respuesta.flush();
	}
	private void seleccionPeticion() throws IOException {
		String tokens = this.peticion.get(PlantillaServer.PETICION);
		if(tokens == null)
			this.badRequest(ContentType.TEXT, "No GET or similar included", null);
		else {
			switch (tokens) {
				case PlantillaServer.GET:
					this.resolverGet();
					break;
				case PlantillaServer.POST:
					this.resolverPost();
					break;
				case PlantillaServer.DELETE:
					this.resolverDelete();
					break;
			}
		}
	}
	//#endregion
	//#region Protected
	protected String getContentType(ContentType type) {
		String resultado = "";
		switch (type) {
            case JSON:
                resultado = "application/json";
                break;
            case DEFAULT:
            case TEXT:
                resultado = "text/html";
                break;
            default:
                resultado = "*";
                break;
		}
		return resultado;
	}
	protected void cerrarConexion() throws IOException { this.respuesta.close(); }
    protected void enviarCabecera() throws IOException {
        String cab = this.cabecera.toString();
        System.out.println(cab);
        //System.out.print('\n');
        this.log.agregarRespuesta(cab);
        this.enviarBytes(cab.getBytes());
    }
    //#endregion
    //#endregion
    //#region Respuestas predefinidas
    protected void easyCabecera(int code, String reason, ContentType type, String contenido) throws IOException {
        this.cabecera.setResponse(code, reason);
        this.cabecera.setContentType(this.getContentType(type));
        this.cabecera.setContenido(contenido);
        this.enviarCabecera();
    }
	protected void noApi() throws IOException {
        this.badRequest(ContentType.JSON, "No Clave", "{\"clave\":null}");
	}
	protected void switchProtocols(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.SWITCHING_PROTOCOLS, reason, type, contenido);
	}
	protected void badRequest(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.BAD_REQUEST, reason, type, contenido);
	}
	protected void serverError(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.SERVER_ERROR, reason, type, contenido);
	}
	protected void notFound(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.NOT_FOUND, reason, type, contenido);
    }
	protected void updateConflict(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.UPDATE_CONFLICT, reason, type, contenido);
    }
	protected void ok(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.OK, reason, type, contenido);
    }
	protected void soyUnaTetera(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.SOY_UNA_TETERA, reason, type, contenido);
    }
	protected void metodoInvalido(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.METODO_INVALIDO, reason, type, contenido);
    }
	protected void forbidden(ContentType type, String reason, String contenido) throws IOException {
        this.easyCabecera(PlantillaServer.FORBIDDEN, reason, type, contenido);
    }
	protected void devolverPing() throws IOException {
        this.easyCabecera(PlantillaServer.SOY_UNA_TETERA, "PING", ContentType.TEXT, "Server is up!");
    }
	//#endregion
	protected void resolverSolicitud() throws IOException {
		this.leerPeticionCliente();
        this.leerPost();
        Tester.getInstancia().imprmirDiccionario(this.peticion);
        Tester.getInstancia().imprmirDiccionario(this.cookieList);
		this.log.agregarPeticion(this.peticion);
		
		String api = this.peticion.get(PlantillaServer.API_KEY);
		
		if(this.api_key != null) {
			if(api == null)
				this.noApi();
			else if(!this.api_key.equals(api))
				this.badRequest(ContentType.TEXT, "WTF did you tried?", null);
			else
				this.seleccionPeticion();
		}
		else
			this.seleccionPeticion();
		if(this.cerrarConexion)
			this.cerrarConexion();
	}
	@Override
	public void run() {
		this.log = new LogServer(Thread.currentThread().getName());
		try {
			this.resolverSolicitud();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
