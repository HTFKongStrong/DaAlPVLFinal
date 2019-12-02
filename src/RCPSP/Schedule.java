package RCPSP;
//Klasse in Schedule umbennenen
// nimm nicht den kleinsten/kürzesten sondern irgend einen mit math.random
// Crossover 
//123456 --> halbieren zu 123
//135246
//erste hälfte 123- > zweite hälfte 135246 von links nach rechts durchgehn und alle die fehlen einfügen
//123546

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Schedule {
	int[] jobListe;
	int[] schedule;

	public void initializeJobList(Job[] jobs) {

		ArrayList<Job> eligibleJobs = new ArrayList<Job>();

		jobListe = new int[jobs.length];

		// 1. Job to jobListe
		int count = 0;
		jobListe[count] = jobs[0].nummer();
		count++;

		ArrayList<Integer> nachfolgerAkt = jobs[0].nachfolger();

		for (int i = 0; i < nachfolgerAkt.size(); i++) {
			eligibleJobs.add(Job.getJob(jobs, nachfolgerAkt.get(i)));
		}

		while (count != jobs.length) {

//h1		heuristic[count]

			Job min = eligibleJobs.get(0);

			int minDauer = eligibleJobs.get(0).dauer();
			for (int i = 0; i < eligibleJobs.size(); i++) {
				if (eligibleJobs.get(i).dauer < minDauer) {
					minDauer = eligibleJobs.get(i).dauer;
					min = eligibleJobs.get(i);
				}
			}

			jobListe[count] = min.nummer;

			count++;
			eligibleJobs.remove(min);

			nachfolgerAkt = min.nachfolger();

			for (int i = 0; i < nachfolgerAkt.size(); i++) {
				Job aktuellerNachfolgerJob = Job.getJob(jobs, nachfolgerAkt.get(i));
				ArrayList<Integer> vorgaengerAkt = aktuellerNachfolgerJob.vorgaenger;
				boolean alleVorgaenger = true;
				for (int j = 0; j < vorgaengerAkt.size(); j++) {
					boolean found = false;
					for (int k = 0; k < jobListe.length; k++) {
						if (jobListe[k] == vorgaengerAkt.get(j)) {
							found = true;
						}
					}
					if (!found) {
						alleVorgaenger = false;
						break;
					}

				}
				if (alleVorgaenger) {
					eligibleJobs.add(Job.getJob(jobs, nachfolgerAkt.get(i)));
				}
			}
		}
	}
	
	
	public void gaSchedule(Job[] jobs) {
		//genetischer Algotithums hier programmiren
		
	}

	public void decodeJobList(Job[] jobs, Resource[] res) {
		// calculate the starting times of the jobs in the order of jobListe

		schedule = new int[jobListe.length];

		// calculate the maximum possible makespan "maxDauer" of the project
		int maxDuration = 0;// alt shift R
		for (int i = 0; i < jobs.length; i++) {
			maxDuration += jobs[i].dauer;
		}

		int[][] resourcenTableau = new int[res.length][maxDuration];

		for (int i = 0; i < resourcenTableau.length; i++) {
			for (int j = 0; j < resourcenTableau[i].length; j++) {
				resourcenTableau[i][j] = res[i].maxVerfuegbarkeit();
			}
		}

		for (int i = 0; i < jobListe.length; i++) {

			int nr = jobListe[i];

			Job j = Job.getJob(jobs, nr);

			int p1 = earliestPossibleStarttime(j, jobs);
			int p2 = starttime(j, p1, resourcenTableau);
			actualizeResources(j, resourcenTableau, p2);

			schedule[i] = p2;
		}

	}

	public int earliestPossibleStarttime(Job j, Job[] jobs) {
		// berechneFruehesteStartzeit
		// Der Zeitpunkt, nachdem alle Vorg�nger abgearbeitet sind.
		ArrayList<Integer> vorgaenger = j.vorgaenger;
		// Startzeit des sp�testen Vorg�ngers + dauer!
		int fruehestenStart = 0;
		for (int i = 0; i < vorgaenger.size(); i++) {
			Job vorg = Job.getJob(jobs, vorgaenger.get(i));
			for (int k = 0; k < jobListe.length; k++) {
				if (jobListe[k] == vorg.nummer) {
					if ((schedule[k] + vorg.dauer()) > fruehestenStart)
						fruehestenStart = schedule[k] + vorg.dauer();
				}
			}
		}
		return fruehestenStart;
	}

	public void actualizeResources(Job j, int[][] resTab, int start) {

		int[] verwendeteResourcen = j.verwendeteResourcen;
		for (int k = 0; k < resTab.length; k++) {
			for (int i = start; i < (start + j.dauer); i++) {
				resTab[k][i] -= verwendeteResourcen[k];
			}
		}
	}

	public int starttime(Job j, int p1, int[][] resTab) {
		// Pr�fen, ob ab diesem Zeitpunkt gen�gend resourcen f�r die Dauer des Jobs
		// vorhanden sind
		int[] verwendeteResourcen = j.verwendeteResourcen;
		boolean genug = true;
		int count = 0;
		do {
			genug = true;
			if (count != 0) {
				p1++;
			}
			for (int k = 0; k < resTab.length; k++) {
				for (int i = p1; i < (p1 + j.dauer); i++) {
					if (resTab[k][i] < verwendeteResourcen[k]) {
						genug = false;
					}
				}
			}
			count++;
		} while (!genug);
		return p1;
	}

//	Diese Methode kann in die Klasse Schedule integriert werden, um einen Schedule auszugeben

	public void ausgabe(String directory, String ausgabeName) {
		try {
			//String path = "C:\\Users\\Doom\\eclipse-workspace\\DaAlPVLFinal\\solutions\\";
			String file = ausgabeName.replace("instances\\", "").replace(".sm", "");
			PrintWriter pu = new PrintWriter(new FileWriter(directory +file+ ".sol"));
			for (int i = 0; i < jobListe.length; i++) {
				pu.println(jobListe[i] + " " + schedule[i]);
			}
			pu.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}