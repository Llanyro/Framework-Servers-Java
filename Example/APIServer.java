package server.threads;

import java.io.IOException;

import org.json.simple.JSONObject;

import framework.classes.ContentType;
import framework.templates.PlantillaServer;
import gestores.GestorCorreo;
import gestores.GestorInformacion;
import gestores.GestorMascota;
import gestores.GestorUsuario;
import gestores.Atributo;

public class ServidorInterfazAPI extends PlantillaServer {
	private void switchfunc(int val) {
		switch(val) {
			case 0:
				this.cabecera.setResponse(PlantillaServer.UPDATE_CONFLICT, "Update conflict. No changes");
				break;
			case 1:
				this.cabecera.setResponse(PlantillaServer.OK, "OK");
				break;
			case -1:
				this.cabecera.setResponse(PlantillaServer.BAD_REQUEST, "Bad request. Maybe something is missing.");
				break;
			default:
				this.cabecera.setResponse(PlantillaServer.SERVER_ERROR, "Server error. Please, contact server admin to fiz that.");
				System.out.println("La base de datos ha dado error en UN UPDATE");
				break;
		}
	}
	public ServidorInterfazAPI(String nombreServidor, String pathServer, String api_key) {
		super(nombreServidor, pathServer, api_key);
        this.cabecera.setAcessControlAllowOrigin("*");
        this.cabecera.setServerName(this.nombreServidor);
	}
	@Override
	protected void resolverDelete() throws IOException {
		switch (this.peticion.get(PARAMETROS_PETICION)) {
			default:
				this.badRequest(ContentType.DEFAULT, "Funcionalidad inexistente", null);
				break;
		}
	}
	@Override
	protected void resolverGet() throws IOException {
		switch (this.peticion.get(PARAMETROS_PETICION)) {
			case "/getChats": {
				String id_persona = null;
				
				id_persona = this.peticion.get("id_persona");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else {
					String msg = GestorUsuario.getInstancia().getChatsUsuarioJSON(id_persona).toJSONString();
					this.ok(ContentType.JSON, "OK", msg);
				}
			}
			break;
			case "/getPosiblesChats": {
				String id_persona = null;
				
				id_persona = this.peticion.get("id_persona");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else {
					String msg = GestorUsuario.getInstancia().getChatsSinEmpezarJSON(id_persona).toJSONString();
					this.ok(ContentType.JSON, "OK", msg);
				}
			}
			break;
			case "/sendVerificarioMail": {
				String id_persona = null;
				String uuid_url = null;
				
				id_persona = this.peticion.get("id_persona");
				uuid_url = this.peticion.get("uuid_url");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(uuid_url == null)
					this.badRequest(ContentType.DEFAULT, "uuid_url: null", null);
				else {
					String email = GestorUsuario.getInstancia().getAtributeById(id_persona, Atributo.Correo);
					if(email == null)
						this.serverError(ContentType.DEFAULT, "Error al buscar email para verificacion", null);
					else {
						boolean result = GestorCorreo.getInstancia().enviarVerificacion(email, GestorUsuario.getInstancia().getAtributeById(id_persona, Atributo.Usuario), uuid_url);
						this.ok(ContentType.JSON, "OK", "{\"result\":" + result + "}" );
					}
				}
			}
			break;
			case "/checkPassword": {
				String id_persona = null;
				String pass = null;
	
				id_persona = this.peticion.get("id_persona");
				pass = this.peticion.get("pass");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(pass == null)
					this.badRequest(ContentType.DEFAULT, "pass: null", null);
				else {
					String res = "{\"result\":" + GestorUsuario.getInstancia().comprobarIDpass(id_persona, pass) + "}";
					this.ok(ContentType.JSON, "OK", res);
				}
			}
			break;
			case "/checkUsername": {
				String usuario = null;
	
				usuario = this.peticion.get("usuario");
				
				if(usuario == null)
					this.badRequest(ContentType.DEFAULT, "usuario: null", null);
				else
					this.ok(ContentType.JSON, "OK", "{\"result\":" + !GestorUsuario.getInstancia().comprobarAtributoPerfil(usuario, Atributo.Usuario) + "}");
			}
			break;
			case "/checkEmail": {
				String email = null;
				
				email = this.peticion.get("email");
				
				if(email == null)
					this.badRequest(ContentType.DEFAULT, "email: null", null);
				else
					this.ok(ContentType.JSON, "OK", "{\"result\":" + !GestorUsuario.getInstancia().comprobarAtributoPerfil(email, Atributo.Correo) + "}");
			}
			break;
			case "/checkTelefono": {
				String telefono = null;
				
				telefono = this.peticion.get("telefono");
				
				if(telefono == null)
					this.badRequest(ContentType.DEFAULT, "telefono: null", null);
				else
					this.ok(ContentType.JSON, "OK", "{\"result\":" + !GestorUsuario.getInstancia().comprobarAtributoPerfil(telefono, Atributo.Telefono) + "}");
			}
			break;
			case "/getListaMatches": {
				String id_persona = null;
				String id_mascota = null;
	
				id_persona = this.peticion.get("id_persona");
				id_mascota = this.peticion.get("id_mascota");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(id_mascota == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
				else
					this.ok(ContentType.JSON, "OK", GestorMascota.getInstancia().getListaMascotasParaMatchsJSON(id_mascota, id_persona).toJSONString());
			}
			break;
			case "/ping":
				this.devolverPing();
			break;
			case "/getRazas": {
	            String especie = null;
				
				especie = this.peticion.get("especie");
	
				if(especie == null)
					this.badRequest(ContentType.DEFAULT, "especie: null", null);
				else {
					this.ok(ContentType.JSON, "OK", GestorInformacion.getInstancia().getRazas(especie).toJSONString());
				}
	        }
	        break;
	        case "/getEspecies": {
	        	this.ok(ContentType.JSON, "OK", GestorInformacion.getInstancia().getEspecies().toJSONString());
	        }
	        break;
			case "/getPerfil": {
				String id_persona = null;
				
				id_persona = this.peticion.get("id_persona");
	
	            if(id_persona == null)
	            	this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
	            else {
	            	JSONObject perfil = GestorUsuario.getInstancia().getPerfilPropietarioJSON(id_persona);
	            	if(perfil == null)
	            		this.badRequest(ContentType.DEFAULT, "User dont exist", null);
	            	else
	            		this.ok(ContentType.JSON, "OK", perfil.toJSONString());
	            }
			}
			break;
			case "/getMascotas": {
				String id_persona = null;
				
				id_persona = this.peticion.get("id_persona");
	
	            if(id_persona == null)
	            	this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
	            else {
	            	JSONObject mascotas = GestorMascota.getInstancia().getMascotas(id_persona);
	            	if(mascotas == null)
	            		this.serverError(ContentType.DEFAULT, "Internal server Error", null);
	            	else
	            		this.ok(ContentType.JSON, "OK", mascotas.toJSONString());
	            }
			}
			break;
			case "/getMascota": {
				String id_persona = null;
				String id_mascota = null;
	
				id_persona = this.peticion.get("id_persona");
				id_mascota = this.peticion.get("id_mascota");
	
	            if(id_persona == null)
	            	this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
	            else if(id_mascota == null)
	            	this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
	            else {
	            	JSONObject mascota = GestorMascota.getInstancia().getPerfilMascotaJSON(id_mascota, id_persona);
	            	if(mascota == null)
	            		this.badRequest(ContentType.DEFAULT, "Mascota dont exist", null);
	            	else
	            		this.ok(ContentType.JSON, "OK", mascota.toJSONString());	
	            }
			}
			break;
			default:
				this.badRequest(ContentType.DEFAULT, "Funcionalidad inexistente", null);
				break;
		}
	}
	@Override
	protected void resolverPost() throws IOException {
			switch(this.peticion.get(PlantillaServer.PARAMETROS_PETICION)) {
			case "/sendEmail": {
				String id_emisor = null;
				String id_receptor = null;
				String mensaje = null;
				String date = null;
				
				id_emisor = this.peticion.get("id_emisor");
				id_receptor = this.peticion.get("id_receptor");
				mensaje = this.peticion.get("mensaje");
				date = this.peticion.get("date");
				
				if(id_emisor == null)
					this.badRequest(ContentType.DEFAULT, "id_emisor: null", null);
				else if(id_receptor == null)
					this.badRequest(ContentType.DEFAULT, "id_receptor: null", null);
				else if(mensaje == null)
					this.badRequest(ContentType.DEFAULT, "mensaje: null", null);
				else if(date == null)
					this.badRequest(ContentType.DEFAULT, "date: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().enviarMensaje(id_emisor, id_receptor, mensaje, date));
					this.enviarCabecera();
				}
			}
			break;
			case "/likematch":{
				String id_persona = null;
				String id_mascota = null;
				String id_mascota_2 = null;
				int decision = -1;
				
				id_persona = this.peticion.get("id_persona");
				id_mascota = this.peticion.get("id_mascota");
				id_mascota_2 = this.peticion.get("id_mascota_2");
				decision = Integer.parseInt(this.peticion.get("decision"));				
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(id_mascota == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
				else if(id_mascota_2 == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota_2: null", null);
				else if(decision == -1)
					this.badRequest(ContentType.DEFAULT, "decision: null", null);
				else if(decision != 0 &&  decision != 1)
					this.badRequest(ContentType.DEFAULT, "decision: not valid number", null);
				else {
					this.switchfunc(GestorMascota.getInstancia().setDecisionMascota(id_mascota, id_persona, id_mascota_2, decision));
					this.enviarCabecera();
				}
			}
			break;
			case "/deleteMascota": {
				String id_persona = null;
				String id_mascota = null;
				
				id_persona = this.peticion.get("id_persona");
				id_mascota = this.peticion.get("id_mascota");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if (id_mascota == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
				else {
					this.switchfunc(GestorMascota.getInstancia().borrarMascota(id_mascota, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/deleteUsuario": {
				String id_persona = null;
				String pass = null;
				
				id_persona = this.peticion.get("id_persona");
				pass = this.peticion.get("pass");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if (pass == null)
					this.badRequest(ContentType.DEFAULT, "pass: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().borrarUsuario(id_persona, pass));
					this.enviarCabecera();
				}
			}
			break;
			case "/setVerificado": {
				String verificado = null;
				String id_persona = null;
	
				id_persona = this.peticion.get("id_persona");
				verificado = this.peticion.get("verificado");
					
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(verificado == null)
					this.badRequest(ContentType.DEFAULT, "verificado: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().setVerificar(verificado, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/setPremium": {
				String premium = null;
				String id_persona = null;
	
				id_persona = this.peticion.get("id_persona");
				premium = this.peticion.get("premium");
					
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(premium == null)
					this.badRequest(ContentType.DEFAULT, "premium: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().setPremium(premium, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/setImagenUsuario": {
				String id_persona = null;
				String img = null;
				
				id_persona = this.peticion.get("id_persona");
				img = this.peticion.get("img");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(img == null)
					this.badRequest(ContentType.DEFAULT, "img: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().setUuid(img, null, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/setImagenMascota": {
				String id_persona = null;
				String img = null;
				String id = null;
				
				id_persona = this.peticion.get("id_persona");
				img = this.peticion.get("img");
				id = this.peticion.get("id_mascota");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(img == null)
					this.badRequest(ContentType.DEFAULT, "img: null", null);
				else if(id == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().setUuid(img, id, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/updateMascota": {
				String id_persona = null;
				String id_mascota = null;
				
				id_persona = this.peticion.get("id_persona");
				id_mascota = this.peticion.get("id_mascota");
	
				if(id_mascota == null)
					this.badRequest(ContentType.DEFAULT, "id_mascota: null", null);
				else if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else {
					this.switchfunc(GestorMascota.getInstancia().modificarPerfilMascota(this.peticion));
					this.enviarCabecera();
				}
			}
			break;
			case "/updatePerfil": {
				String id_persona = null;
				
				id_persona = this.peticion.get("id_persona");
	
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().modificarPerfilUsuario(this.peticion));
					this.enviarCabecera();
				}
			}
			break;
			case "/setMascota": {
				String id_persona = null;

				id_persona = this.peticion.get("id_persona");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else {
					this.switchfunc(GestorMascota.getInstancia().agregarMascota(this.peticion));
					this.enviarCabecera();
				}
			}
			break;
			case "/signin":{
				String jsonresultado = null;
				String usuario = null;
				
				usuario = this.peticion.get("usuario");
				
				if(usuario == null)
					this.badRequest(ContentType.DEFAULT, "usuario: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().registrarse(this.peticion));
					// Si se ha creado la cuenta
					if(this.cabecera.getResponseCode() == PlantillaServer.OK)
						jsonresultado = "{\"usuario\":\"" + GestorUsuario.getInstancia().getIdPropietario(usuario) + "\"}";
					else
						jsonresultado = "{\"usuario\":\"-1\"}";
					this.cabecera.setContenido(jsonresultado);
					this.cabecera.setContentType(this.getContentType(ContentType.JSON));
					this.enviarCabecera();
				}
			}
			break;
			case "/login": {
				String jsonresultado = null;
				String usuario = null;
				String pass = null;
	
				usuario = this.peticion.get("usuario");
				pass = this.peticion.get("pass");
				
				if(usuario == null)
					this.badRequest(ContentType.DEFAULT, "usuario: null", null);
				else if(pass == null)
					this.badRequest(ContentType.DEFAULT, "pass: null", null);
				else {
					if(GestorUsuario.getInstancia().LogIn(this.peticion)) {
						jsonresultado = "{\"usuario\":\"" + GestorUsuario.getInstancia().getIdPropietario(usuario) + "\"}";
						this.ok(ContentType.JSON, "OK", jsonresultado);
					}
					else  {
						this.cabecera.setContenido("{\"usuario\":\"-1\"}");
						this.cabecera.setContentType(this.getContentType(ContentType.JSON));
						this.enviarCabecera();	
					}
				}
			}
			break;
			case "/changePassword":{
				String id_persona = null;
				String pass = null;
				String newpass = null;
				
				id_persona = this.peticion.get("id_persona");
				pass = this.peticion.get("pass");
				newpass = this.peticion.get("newpass");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(pass == null)
					this.badRequest(ContentType.DEFAULT, "pass: null", null);
				else if(newpass == null)
					this.badRequest(ContentType.DEFAULT, "newpass: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().cambiarPass(pass, newpass, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			case "/changeLocation":{
				String id_persona = null;
				String direccion = null;
				
				id_persona = this.peticion.get("id_persona");
				direccion = this.peticion.get("direccion");
				
				if(id_persona == null)
					this.badRequest(ContentType.DEFAULT, "id_persona: null", null);
				else if(direccion == null)
					this.badRequest(ContentType.DEFAULT, "direccion: null", null);
				else {
					this.switchfunc(GestorUsuario.getInstancia().setDireccion(direccion, id_persona));
					this.enviarCabecera();
				}
			}
			break;
			default:
				this.badRequest(ContentType.DEFAULT, "Funcionalidad inexistente", null);
				break;
		}
	}
	@Override
	public PlantillaServer clonar() { return new ServidorInterfazAPI(this.nombreServidor, this.pathServer, this.api_key); }
}
