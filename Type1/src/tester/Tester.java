package tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tester {
	//Singleton
	private static Tester instancia = null;
	public static Tester getInstancia() {
		if (instancia==null)
			instancia = new Tester();
		return instancia;
	}
	private Tester() {}
	
	// Otros
	private String[] argReparado(String[] arg) {
		String[] resultado = new String[arg.length - 1];
		for (int i = 1; i < arg.length - 1; i++) {
			String[] spl = arg[i].split(" ");
			resultado[i - 1] = spl[0];
			if(spl.length > 1 && spl[1].length() > 0)
				resultado[i - 1] += " " + spl[1];
		}
		resultado[arg.length - 2] = arg[arg.length - 1];
		return resultado;
	}
	
	
	// Funciones internas
	private String ejecutarCommand(String command, String[] arg) {
		String resultado = null;
		switch(command) {
			case "string":
				resultado = this.generarString(arg);
				break;
			case "value":
				resultado = this.generarNum(arg);
				break;
		}
		return resultado;
	}
	private String generarString(String[] arg) {
		boolean mayus = false;
		boolean minus = false;
		boolean nums = false;
		int n = 20;
		for (int i = 0; i < arg.length; i++) {
			if(arg[i].equals("minus"))
				minus = true;
			else if(arg[i].equals("mayus"))
				mayus = true;
			else if(arg[i].equals("nums"))
				nums = true;
			else if(arg[i].startsWith("n "))
				n = Integer.parseInt(arg[i].split(" ")[1]);
			else
				System.out.println("Error(generarString) en: " + arg[i]);
		}
		return this.getAlphaNumericString(n, mayus, minus, nums);
	}
	private String generarNum(String[] arg) {
		boolean flotante = false;	// Si el numero es flotante
		int inicio = 0;				// Inicio
		int fin = 0;				// Fin

		for (int i = 0; i < arg.length; i++) {
			if(arg[i].equals("d"))
				flotante = true;
			else if(arg[i].startsWith("i "))
				inicio = Integer.parseInt(arg[i].split(" ")[1]);
			else if(arg[i].startsWith("f "))
				fin = Integer.parseInt(arg[i].split(" ")[1]);
			else
				System.out.println("Error(generarNum) en: " + arg[i]);
		}
		String resultado = null;
		if(flotante)
			resultado = Double.toString(this.getNumberRandom(inicio, fin));
		else
			resultado = Integer.toString((int)this.getNumberRandom(inicio, fin));
		return resultado;
	}
	
	
	// Funciones publicas
	public double getNumberRandom(int inicio, int fin) {
		double result = 0.0;
		
		if(inicio == 0 && fin == 0)
			result = this.range(0, Integer.MAX_VALUE);
		else if(inicio == 0)
			result = this.range(0, fin);
		else if(fin == 0)
			result = this.range(inicio, Integer.MAX_VALUE);
		else
			result = this.range(inicio, fin);
		
		return result;
	}
	public String getAlphaNumericString(int n, boolean mayus, boolean minus, boolean nums) {
		String caracteres = "";
		if(mayus)
			caracteres += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if(minus)
			caracteres += "abcdefghijklmnopqrstuvxyz";		
		if(nums)
			caracteres += "0123456789";
		StringBuilder sb = new StringBuilder(n);
		
		if(caracteres.length() > 0)
			for (int i = 0; i < n; i++) { 
	            int index = (int)(caracteres.length() * Math.random());
	            sb.append(caracteres.charAt(index));
	        }

        return sb.toString();
    }
	public double range(double inicio, double fin) {
		return (Math.random() * fin) + inicio;
	}
	public double range(double fin) {
		return (Math.random() * fin);
	}
	
	
	// Gestor de commands
	public String ejecutarCommand(String command) {
		String[] commandSplit = command.split(" ", 2);
		String action = commandSplit[0];
		String[] arg = this.argReparado(commandSplit[1].split("-"));
		return this.ejecutarCommand(action, arg);
	}
	public List<String> ejecutarCommands(List<String> commands) {
		List<String> resultado = new ArrayList<String>();
		for (int i = 0; i < commands.size(); i++) {
			resultado.add(this.ejecutarCommand(commands.get(i)));
		}
		return resultado;
	}
	public HashMap<String, String> ejecutarCommands(HashMap<String, String> commandsAndKeys) {
		HashMap<String, String> result = new HashMap<String, String>();
		for (String key: commandsAndKeys.keySet()){
			result.put(key, this.ejecutarCommand(commandsAndKeys.get(key)));
		}
		return result;
	}
	
	// Otras funciones publicas
	public void imprmirDiccionario(HashMap<String, String> map) {
		for(String key: map.keySet())
			System.out.println(key + ": " + map.get(key));
		System.out.print('\n');
	}
	public void imprmirLista(List<String> list) {
		for(int i = 0; i < list.size(); i++)
			System.out.println(i +": " + list.get(i));
	}
	
	
}
