import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		String solPath = "solutions";
		String insPath = "instances";
		DirectoryWalker solWalker = new DirectoryWalker(new File(solPath), false);

		for (File solFile : solWalker) {
			String solFileName = solFile.getName().replace(".sol", "");
			DirectoryWalker insWalker = new DirectoryWalker(new File(insPath), false);
			// System.out.println(solFile.getName());
			boolean fileVorhanden = false;
			for (File insFile : insWalker) {
				long start = System.currentTimeMillis();

				Schedule s = new Schedule();
				int[] jobListe;

				String insFileName = insFile.getName().replace(".sm", "");
				if (solFileName.equals(insFileName)) {
					// System.out.println(insFileName);

					Job[] jobs = Job.read(insFile);
					Resource[] res = Resource.read(insFile);
					for (int i = 0; i < jobs.length; i++) {
						jobs[i].calculatePredecessors(jobs);
					}
					validierung(solFile, jobs, res);
					fileVorhanden = true;
					s.initializeJobList(jobs);
					s.decodeJobList(jobs, res);
					jobListe = s.jobListe;
					long finish = System.currentTimeMillis();
					long timeElapsed = finish - start;
					System.out.println("Optimale Zeit: " + s.schedule[jobListe.length - 1] 
							+ "; Rechenzeit: " + timeElapsed+" ms");
					break;
				}

			}
			if (!fileVorhanden) {
				System.out.println("Es existiert keine Instanz zur Loesung");
			}
		}
		System.out.println("alle files validiert");
	}

	public static void validierung(File solFile, Job[] jobs, Resource[] res) throws FileNotFoundException {
		int[] startZeiten = new int[jobs.length]; // index j repraesentiert Job mit Nummer j; start[j] ist die Startzeit
													// von Job mit Nummer j
		boolean[] verplant = new boolean[jobs.length];
		boolean valid = true;
		Scanner scanner = new Scanner(solFile);

		while (scanner.hasNext()) {
			int jobNr = scanner.nextInt();
			int start = scanner.nextInt();
			// System.out.println(jobNr + " ");
			if (jobNr > jobs.length || jobNr < 1) {
				System.out.println(solFile.getName() + " : Jobanzahl fehlerhaft");
				valid = false;
				break;
			} else {
				startZeiten[jobNr - 1] = start;
				verplant[jobNr - 1] = true;
			}
		}

		/*
		 * Alle Jobs geplant
		 */
		for (int j = 0; j < verplant.length; j++) {
			if (!verplant[j]) {
				System.out.println(solFile.getName() + " : Job " + (j + 1) + " nicht verplant!");
			}
		}

		/*
		 * Kapazitaetsrestriktionen pruefen
		 */
		if (valid) {
			int maxDuration = 0;
			for (int i = 0; i < jobs.length; i++) {
				maxDuration += jobs[i].dauer;
			}
			int[][] resourcenTableau = new int[res.length][maxDuration];
			for (int i = 0; i < resourcenTableau.length; i++) {
				for (int j = 0; j < resourcenTableau[i].length; j++) {
					resourcenTableau[i][j] = res[i].maxVerfuegbarkeit();
				}
			}
			for (int j = 0; j < jobs.length; j++) {
				Job job = jobs[j];
				int start = startZeiten[job.nummer - 1];

				if (start < 0 || start + job.dauer >= maxDuration) {
					System.out.println(solFile.getName() + " : Nicht im Zeithorizont");
					valid = false;
					break;
				} else {
					int[] verwendeteResourcen = job.verwendeteResourcen;
					for (int k = 0; k < resourcenTableau.length; k++) {
						for (int i = start; i < (start + job.dauer); i++) {
							resourcenTableau[k][i] -= verwendeteResourcen[k];
							if (resourcenTableau[k][i] < 0) {
								System.out.println(solFile.getName() + " : Kapazitaten ueberschritten: " + "Res: " + k
										+ " Zeit: " + i + " Verletzung: " + resourcenTableau[k][i]);
								valid = false;
								break;
							}
						}
					}
				}
			}
		}

		/*
		 * Zeitabhaengigkeitsrestriktionen pruefen
		 */
		for (int j = 0; j < jobs.length; j++) {
			Job job = jobs[j];
			int start = startZeiten[job.nummer - 1];
			for (int v = 0; v < job.vorgaenger.size(); v++) {
				int vJobNr = job.vorgaenger.get(v);
				int vStart = startZeiten[vJobNr - 1];
				Job vorg = Job.getJob(jobs, vJobNr);
				if (vStart + vorg.dauer > start) {
					System.out.println(solFile.getName() + " : Job : " + job.nummer + " nicht hinter Vorgaenger "
							+ vJobNr + " geplant");
					valid = false;
				}
			}
		}

		if (!valid) {
			System.out.println(solFile.getName() + " ist NICHT ZULAESSIG! ");
		}

		scanner.close();
	}
}
