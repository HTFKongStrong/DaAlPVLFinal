package RCPSP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RunSchedule {

	public static void main(String[] args) throws FileNotFoundException {
//		Job[] jobs     = Job.read(new File("j1201_5.sm"));//best makespan=112
//		Resource[] res = Resource.read(new File("j1201_5.sm"));
//		Job[] jobs = Job.read(new File("j12046_8.sm"));
//		Resource[] res = Resource.read(new File("j12046_8.sm"));

//Ausgabe wird nicht mehr benötigt da eigene static methode
//		for(int i = 0; i < jobs.length; i++){
//			jobs[i].calculatePredecessors(jobs);
//		}

//		Job.calculatePredecessors(jobs);

//		Schedule s = new Schedule();
//		s.initializeJobList(jobs);
//		s.decodeJobList(jobs, res);

//		auslesen(jobs);
//		auslesen(res);

//Ab hier eigner Code
//		int[] jobListe = s.jobListe;
//		for (int i = 0; i < jobListe.length; i++) {
//			System.out.println(jobListe[i]);
//		}
//
//		System.out.println("Optimale Zeit:" + s.schedule[jobListe.length - 1]);

//		// ALLE FILES BERECHNEN
		int count = 0;
		File f = new File("instances");
		File[] fileArray = f.listFiles();
		String[] fileList = new String[fileArray.length];

		for (int i = 0; i < fileArray.length; i++) {
			fileList[i] = fileArray[i].toString();
		}

		for (int i = 0; i < fileList.length; i++) {
			long start = System.currentTimeMillis();

			Job[] jobs = Job.read(new File(fileList[i]));
			Resource[] res = Resource.read(new File(fileList[i]));

			Job.calculatePredecessors(jobs);

			Schedule s = new Schedule();
			s.initializeJobList(jobs);
			s.decodeJobList(jobs, res);
			//Test für Ausgabe
			s.ausgabe("C:\\Users\\Doom\\eclipse-workspace\\DaAlPVLFinal\\solutions\\",fileList[i]);
			
			int[] jobListe = s.jobListe;
//			for (int j = 0; j < jobListe.length; i++) {
//				System.out.println(jobListe[j]);
//			}
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;
			System.out.println("Filename: " + fileList[i]+" , "+ "Optimale Zeit:" + s.schedule[jobListe.length - 1] + ", Rechenzeit: " + timeElapsed +" ms");
			count++;
		}
		System.out.println("Gelöste Dateien: "+count);
//		//ENDE ALLE FILES BERECHNEN

	}

//Methooden zum Auslesen
	private static void auslesen(Job[] jobs) {
		int gesamtDauer = 0;
		for (int i = 0; i < jobs.length; i++) {
			gesamtDauer += jobs[i].dauer();

			System.out.print("Nummer: " + jobs[i].nummer() + "     |    ");
			System.out.print("Nachfolger: ");
			ArrayList<Integer> nachfolger = jobs[i].nachfolger();
			for (int j = 0; j < nachfolger.size(); j++) {
				System.out.print(" " + nachfolger.get(j) + " ");

			}
			System.out.print(" Vorgaenger: ");
			ArrayList<Integer> vorgaenger = jobs[i].vorgaenger();
			for (int j = 0; j < vorgaenger.size(); j++) {
				System.out.print(" " + vorgaenger.get(j) + " ");

			}
			System.out.print("     |    ");
			System.out.print("Dauer: " + jobs[i].dauer() + "     |    ");
			System.out.println("R1: " + jobs[i].verwendeteResource(0) + "  R2: " + jobs[i].verwendeteResource(1)
					+ "  R3: " + jobs[i].verwendeteResource(2) + "  R4: " + jobs[i].verwendeteResource(3));
		}
		System.out.println("T = " + gesamtDauer);
	}

	private static void auslesen(Resource[] resource) {
		for (int i = 0; i < resource.length; i++) {
			System.out.print("Resource: " + resource[i].nummer() + "     |    ");
			System.out.println("Verfügbarkeit: " + resource[i].maxVerfuegbarkeit());
		}
	}

}
