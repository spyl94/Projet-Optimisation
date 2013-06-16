package efrei;

public class Work {
	
	public static int count = 0;
	public int id;
	private int uccount = 0;
	private int diskcount = 0;
	private int tapecount = 0;

	public Work() {
		id = count;
		count++;
	}
	
	public Work clone() {
	    Work w = new Work();
	    w.uccount = this.uccount;
	    w.diskcount = this.diskcount;
	    w.tapecount = this.tapecount;
	    return w;
	}
	
	public void reset() {
		uccount = 0;
		diskcount = 0;
		tapecount = 0;
	}

	public void addUccount() {
		this.uccount++;
	}
	public int getUccount() {
		return this.uccount;
	}

	public void addDiskcount() {
		this.diskcount++;
	}
	public int getDiskcount() {
		return this.diskcount;
	}

	public void addTapecount() {
		this.tapecount++;
	}
	public int getTapecount() {
		return this.tapecount;
	}
}
