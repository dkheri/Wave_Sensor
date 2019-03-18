public class SendConfigData implements Runnable {

    String Data;

    SendConfigData(String Data)
    {
        this.Data=Data;
    }

    @Override
    public void run() {
           WaveServer.out.println(Data);
           WaveServer.out.flush();
    }
}
