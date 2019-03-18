import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WaveServer {
    static ServerSocket serverSocket;
    static Socket clientSocket;
    static BufferedReader in;
    static PrintWriter out;
    static int PORT=7000;
    static String DATAFILE="WAVESENSORDATA.txt";
    static List<String> dataStored=new ArrayList<>();
    public static void main(String[] args) throws IOException {

        WaveServer.serverSocket=new ServerSocket(WaveServer.PORT);
        System.out.println("Waiting For Client Connection");
        clientSocket=WaveServer.serverSocket.accept();
        WaveServer.in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        WaveServer.out=new PrintWriter(clientSocket.getOutputStream(),true);
        populateData();
        //start Listener Thread
        Thread listener=new Thread(new WaveDataListener());
        listener.start();

        int input=1;
        Scanner in=new Scanner(System.in);
        while(input<4)
        {
            System.out.println("1.Turn ON/OFF Wave Sensor");
            System.out.println("2.Change Sensitivity");
            System.out.println("3.View Data");
            System.out.println("4.Quit");
            input=Integer.parseInt(in.nextLine());
            if(input==1)
            {
                System.out.println("ON/OFF");
                String response=in.nextLine();
                Thread thread=new Thread(new SendConfigData("POWER "+response.toUpperCase()));
                thread.start();
                clear();
            }

            else if (input==2)
            {
                System.out.println("New Sensisity: ");
                String Sensitivity=in.nextLine();
                Thread thread=new Thread(new SendConfigData("SENSITIVITY "+Sensitivity));
                thread.start();
                clear();
            }
            else if (input==3)
            {
                viewData();
            }

            else {
                RecordData();
                WaveDataListener.done=true;
            }
        }
    }
    public static void populateData()
    {
        Scanner in= null;
        try {
            in = new Scanner(new File(WaveServer.DATAFILE));
            while(in.hasNextLine())
            {
//                System.out.println(in.nextLine());
                System.out.println(dataStored.add(in.nextLine()));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        in.close();
    }
    public static void viewData()
    {
        Scanner in= null;
        try {
            in = new Scanner(new File(WaveServer.DATAFILE));
            for (int i=0;i<dataStored.size();i++)
            {
                System.out.println(dataStored.get(i));
            }
            System.out.println();
			System.out.println();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        in.close();
    }
    public static void RecordData()
    {
        try {
            BufferedWriter writer=new BufferedWriter(new FileWriter(DATAFILE));
            for(int i=0;i<dataStored.size();i++) {
                writer.write(dataStored.get(i)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clear()
    {
        for(int i=0;i<100;i++)
        {
            System.out.println();
        }
    }
}
