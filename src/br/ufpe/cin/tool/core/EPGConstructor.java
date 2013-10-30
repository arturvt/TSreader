package br.ufpe.cin.tool.core;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufpe.cin.tool.mpegts.EPGValues;

public class EPGConstructor {
	
	static HashMap<String, Integer> qntOFProgram = new HashMap<String, Integer>();
	
	public static void readEPGList(ArrayList<EPGValues> list) {
		for (EPGValues item:list) {
			if (!qntOFProgram.containsKey(item.getName())) {
				qntOFProgram.put(item.getName(), 0);
			} 
			qntOFProgram.put(item.getName(), qntOFProgram.get(item.getName())+1);
		}
		System.out.println("Occurrences...");
		for (String keys:qntOFProgram.keySet()) {
			System.out.println("Program: "+keys+" - " + qntOFProgram.get(keys));
		}
		
	}
}
