package bgu.spl.mics.application;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        GsonObj gsonObj = gson.fromJson(new FileReader(args[0]), GsonObj.class);
        Squad.getInstance().load(gsonObj.squad);
        GsonObj.Service service = gsonObj.services;

        int numM = service.M;
        int moneypenny = service.Moneypenny;
        GsonObj.Service.GsonIntelligence[] intelligences = service.intelligence;

        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < numM; i++) {
            Subscriber M = new M(i + 1);
            Thread t = new Thread(M);
            threads.add(t);
            t.start();
        }

        for (int i = 0; i < moneypenny; i++) {
            Subscriber moneypenney = new Moneypenny(i + 1);
            Thread t = new Thread(moneypenney);
            threads.add(t);
            t.start();
        }

        for (int i = 0; i < intelligences.length; i++) {
            Subscriber intelligence = new Intelligence(intelligences[i].missions);
            Thread t = new Thread(intelligence);
            threads.add(t);
            t.start();
        }

        Inventory.getInstance().load(gsonObj.inventory);
        Subscriber q = new Q();
        Thread threadQ = new Thread(q);
        threadQ.start();
        threads.add(threadQ);

        TimeService timeService = new TimeService(service.time);
        Thread thread1 = new Thread(timeService);
        thread1.start();
        threads.add(thread1);


        while (threadQ.isAlive()) {

        }
        for (Thread t : threads) {
            if (t.isAlive()) {
                t.interrupt();
            }
        }

        try {
            for (Thread t : threads) {
                t.join();
                System.out.println(t + " joined");
            }
        } catch (InterruptedException e) {
        }
        for (Report report : Diary.getInstance().getReports()) {
            System.out.println(report.getMissionName());
        }
        System.out.println(Diary.getInstance().getTotal());

        Inventory.getInstance().printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);


        System.out.println("finish successfully");


    }

}

