package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.Gsonreports;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

	private List<Report> reports;
	private AtomicInteger total;
	private static Diary instance = new Diary();

	private Diary(){
		reports=new LinkedList<>();
		total = new AtomicInteger(0);
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return instance;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		synchronized (reports) {
			reports.add(reportToAdd);
		}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {
		Gsonreports gsonreports = new Gsonreports(reports, total.get());
		Gson gson = new Gson();
		try {
			FileWriter file = new FileWriter(filename);
			file.write(gson.toJson(gsonreports));
			file.close();
		} catch (IOException E) {
		}
	}


	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}


	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal(){
		total.getAndIncrement();

	}

}