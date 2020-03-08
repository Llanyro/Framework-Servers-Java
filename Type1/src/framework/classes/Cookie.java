package framework.classes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Cookie {
    private String key = null;
    private String value = null;
    private Date fechaExpiracion = null;
    private boolean seguro = false;
    private boolean httponly = false;
    private String dominio = null;
    private String path = null;

    // Getters
    public String getKey() { return this.key; }
    public String getValue() { return this.value; }
    public Date getFechaExpiracion() { return this.fechaExpiracion; }
    public boolean getSeguro() { return this.seguro; }
    public boolean getHTTPOnly() { return this.httponly; }
    public String getDominio() { return this.dominio; }    
    public String getPath() { return this.path; }
    
    // Setters
    public void setKey(String key) { this.key = key; }
    public void setValue(String value) { this.value = value; }
    public void setFechaExpiracion(Date fechaExpiracion) {
    	this.fechaExpiracion = fechaExpiracion;
    }
    public void setFechaExpiracion(int fechaExpiracion) {
    	this.fechaExpiracion = Date.from(Instant.now().plus(fechaExpiracion, ChronoUnit.DAYS));
    }
    public void setSeguro(boolean seguro) { this.seguro = seguro; }
    public void setHTTPOnly(boolean httponly) { this.httponly = httponly; }
    public void setDominio(String dominio) { this.dominio = dominio; }    
    public void setPath(String path) { this.path = path; }

    // Otros
    public boolean equals(Cookie c) { return (this.key == c.key && this.value == c.value); }
    public boolean equals(String key, String value) {
    	return (this.key.equals(key) && this.value == value);
    }
    public boolean equals(String key) { return this.key.equals(key); }
    
    // Funciones pulbicas
    public Cookie() {
    	this.key = null;
        this.value = null;
        this.fechaExpiracion = null;
        this.seguro = true;
        this.httponly = true;
        this.dominio = null;
        this.path = null;
    }
    public Cookie(String key, String value) {
    	this();
    	this.key = key;
        this.value = value;
    }
    public Cookie(String key, String value, int fechaExpiracion) {
    	this(key, value);
    	this.setFechaExpiracion(fechaExpiracion);
    }
    public void setCookie(String key, String value) {
    	this.key = key;
    	this.value = value;
    }
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(this.key != null && this.value != null) {
        	str.append(this.key);
        	str.append("=");
        	str.append(this.value);
        	
        	// Expiracion
        	if(this.fechaExpiracion != null) {
        		str.append("; expires=");
            	str.append(this.fechaExpiracion.toString());
        	}
        	// Dominio
        	if(this.dominio != null) {
        		str.append("; domain=");
            	str.append(this.dominio);
        	}
        	// Path
        	if(this.path != null) {
        		str.append("; path=");
            	str.append(this.path);
        	}
        	// Secure
        	if(this.seguro == true)
        		str.append("; secure");
        	// HttpOnly
        	if(this.httponly == true)
        		str.append("; HttpOnly");
        }
    	return str.toString();
    }
}