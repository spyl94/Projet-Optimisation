package efrei;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Controller {

	private ConcurrentLinkedQueue<Work> start; // f0
	private ConcurrentLinkedQueue<Work> uc; // f1
	private ConcurrentLinkedQueue<Work> disk; // f2
	private ConcurrentLinkedQueue<Work> diskbis; // f2bis
	private ConcurrentLinkedQueue<Work> tape; // f3
	private ArrayList<Work> finish;
	private int doneCount;
	private Thread t;
	private Thread t2;
	private Thread t2bis;
	private Thread t3;
	private Thread t4;
	private boolean bis = false;
	private double ucuse = 0;
	private double diskuse = 0;
	private double diskbisuse = 0;
	private double tapeuse = 0;
	
	public boolean isBis(){return bis;}

	public Controller() {
		this.start = new ConcurrentLinkedQueue<Work>();
		this.uc = new ConcurrentLinkedQueue<Work>();
		this.disk = new ConcurrentLinkedQueue<Work>();
		this.diskbis = new ConcurrentLinkedQueue<Work>();
		this.tape = new ConcurrentLinkedQueue<Work>();
		this.finish = new ArrayList<Work>();
		
		this.doneCount = 0;
		for (int i = 0; i < FileParser.ordre; i++) start.add(new Work());
		System.out.println("Ajouter le disque D2 ? : (O/N)");
		Scanner in = new Scanner(System.in);
		if(in.next().equals("O")) this.bis = true;
	}
	
	private double exp(double lambda)
	{
		Random rand =  new Random();
		double result = - lambda * Math.log (1 - rand.nextDouble());
		if ((long) (result *1000) > 0) return result; // avoid 0
		return exp(lambda);
	}
	
	private long poisson() {
		  double L = Math.exp(-FileParser.lambda);
		  double p = 1.0;
		  int k = 0;
		  do {
		    k++;
		    p *= Math.random();
		  } while (p > L);
		  return (k - 1);
	}

	public static void main(String[] args) throws InterruptedException {
		FileParser f = new FileParser(args);
		Controller c = new Controller();
		long start = System.nanoTime();
		c.initStartFifo();
		c.initDiskFifo();
		if(c.isBis()) c.initDiskBisFifo();
		c.initTapeFifo();
		c.userate();
		c.startSimulation();
		System.out.println("L'éxécution a durée: " + (long) ((System.nanoTime() - start) / 1000000000) + " secondes.");
		System.out.println("Le débit est de "+ FileParser.count / ((double) ((System.nanoTime() - start) / 1000000000)) + " travaux par secondes.");
		c.endResults();
		
	}
	
	public void endResults()
	{
		int avguc = 0;
		int avgdisk = 0;
		int avgtape = 0;
		for(Work w : finish) {
			avguc += w.getUccount();
			avgdisk += w.getDiskcount();
			avgtape += w.getTapecount();
		}
		avguc /= finish.size();
		avgdisk /=  finish.size();
		avgtape /=  finish.size();
		System.out.println("Passage moyen dans uc:" + avguc);
		System.out.println("Passage moyen dans disk:" + avgdisk);
		System.out.println("Passage moyen dans tape:" + avgtape);
	}
	
	public void userate() {
		t4 = new Thread("UseRateThread") {
			public void run() {
				int i = 0;
				while (doneCount < FileParser.count) {
					try {
						if(!uc.isEmpty()) ucuse++;
						if(!disk.isEmpty()) diskuse++;
						if(bis && !diskbis.isEmpty()) diskbisuse++;
						if(!tape.isEmpty()) tapeuse++;
						try {
							i++;
							Thread.sleep(1); // 1 ms d'attente entre chaque vérification
						} catch (InterruptedException e) {}
					} catch (java.util.NoSuchElementException e) { //si file vide
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					}
				}
				ucuse /= i;
				System.out.println("Taux d'utilisation de la file uc:" + ucuse);
				diskuse /= i;
				System.out.println("Taux d'utilisation de la file disk D1:" + diskuse);
				if(bis) {
					diskbisuse /= i;
					System.out.println("Taux d'utilisation de la file disk D2:" + diskbisuse);
				}
				tapeuse /= i;
				System.out.println("Taux d'utilisation de la file bande magnétique:" + tapeuse);
			}
		};
		t4.start();
	}

	public void initStartFifo() {
		t = new Thread("StartFifoThread") {
			public void run() {
				while (doneCount < FileParser.count) {
					try {
						long add = poisson(); // poisson génère le nombre de tâches à ajouter
						System.out.println("Ajout dans f1 de: " + add + " tâche(s).");
						for(int i = 0; i < add; i++) uc.add(start.remove());
						
						try {
							Thread.sleep(1000); // 1 s d'attente entre chaque poisson
						} catch (InterruptedException e) {}
					} catch (java.util.NoSuchElementException e) { //si file vide
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					}
				}
			}
		};
		t.start();
	}

	public void initDiskFifo() {
		t2 = new Thread("DiskFifoThread") {
			public void run() {
					while (doneCount < FileParser.count) {
						try {
						Work w = disk.remove();
						w.addDiskcount();
						long wait = (long) (exp(FileParser.disktime) * 1000);
						System.out.println("Lecture du disque par " + w.id + " durée: " + wait + "ms.");
						try {
							Thread.sleep(wait);
						} catch (InterruptedException e) {}
						uc.add(w);
					} catch (java.util.NoSuchElementException e) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					}
				}
			}
		};
		t2.start();
	}
	
	public void initDiskBisFifo() {
		t2bis = new Thread("DiskFifoBisThread") {
			public void run() {
					while (doneCount < FileParser.count) {
						try {
						Work w = diskbis.remove();
						w.addDiskcount();
						long wait = (long) (exp(FileParser.disktime) * 1000);
						System.out.println("Lecture du disque D2 par " + w.id + " durée: " + wait + "ms.");
						try {
							Thread.sleep(wait);
						} catch (InterruptedException e) {}
						uc.add(w);
					} catch (java.util.NoSuchElementException e) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					}
				}
			}
		};
		t2bis.start();
	}

	public void initTapeFifo() {
		t3 = new Thread("TapeFifoThread") {
			public void run() {
				while (doneCount < FileParser.count) {
					try {
						Work w = tape.remove();
						w.addTapecount();
						long wait = (long) (exp(FileParser.tapetime) * 1000);
						System.out.println("Lecture des bandes par " + w.id + " durée: " + wait + "ms.");
						try {
							Thread.sleep(wait);
						} catch (InterruptedException e) {}
						uc.add(w);
					} catch (java.util.NoSuchElementException e) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					}
				}
			}
		};
		t3.start();
	}

	public void startSimulation() throws InterruptedException {
			while (doneCount < FileParser.count) {
				try {
					Work w = uc.remove();
					w.addUccount();
					System.out.println("Nombre de travaux terminé(s): " + doneCount + " / " + FileParser.count);
					long wait = (long) (exp(FileParser.uctime) * 1000);
					System.out.println("Execution du travail " + w.id + " durée: " + wait + "ms.");
					Thread.sleep(wait);
	
					if (Math.random() <= FileParser.p1) {
						System.out.println("Le travail " + w.id + " est terminé !");
						doneCount++;
						finish.add(w.clone()); //stock une copie du travail fini pour les résultats de fin
						w.reset();
						start.add(w); // on la remet dans la file de départ : recyclage
					} else {
						if (Math.random() <= FileParser.p2) {
							if (bis && Math.random() <= FileParser.diskusage) {
								System.out.println("Le travail " + w.id + " a demandé à écrire le disque D2.");
								diskbis.add(w); // on ajoute dans la file du disque
							} else {
								System.out.println("Le travail " + w.id + " a demandé à écrire le disque D1.");
								disk.add(w); // on ajoute dans la file du disque
							}
						} else {
							System.out.println("Le travail " + w.id + " a demandé l'accès à une unité de bandes magnétiques.");
							tape.add(w); // on ajoute dans la file de bandes
						}
					}
				} catch (java.util.NoSuchElementException e) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {}
				}
			}
	}

}
