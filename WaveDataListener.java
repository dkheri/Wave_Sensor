import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WaveDataListener implements Runnable {

    private BufferedWriter writer;
    private SimpleDateFormat formatter;
    static boolean done=false;
    public WaveDataListener(){
        try {
            writer=new BufferedWriter(new FileWriter(WaveServer.DATAFILE));
            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        String input;
        while (true) {
            try {
                input = WaveServer.in.readLine();
                if (input != null) {
                    RecordData(input);
                }
                if(done)
                {
                    return;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void RecordData(String Input) throws IOException {
        Date date=new Date();
        String datedInput="Date: "+formatter.format(date)+" Wave Lasted For: "+Input;
//        System.out.println(datedInput);
        WaveServer.dataStored.add(datedInput);
    }



}