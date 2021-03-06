package client.models.connection;

import shared.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * DiscoverySender class is used by clients to discover storage devices
 */
public class DiscoverySender implements Runnable {

    private static final Object lock = new Object();
    private String broadcastIP;
    private ArrayList<String> arrayListIP = new ArrayList<>(10);
    private boolean done = false;
    private Thread thread = null;

    /**
     * Constructor for the DiscoverySender object
     *
     * @param baseIP is the base IP of the network
     */
    public DiscoverySender(String baseIP) {
        String[] temp = baseIP.split("\\.");
        this.broadcastIP = temp[0] + "." + temp[1] + "." + temp[2] + ".255";
    }

    /**
     * Method used to start the thread for the discovery operations
     */
    public void start() {
        /*
        Check if the thread is running
         */
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Run method used to discover storage devices
     */
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = new byte[1];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getByName(this.broadcastIP), Constants.getInstance().UDP_DISCOVERY_PORT);

            socket.send(packet); // send a broadcast message for all devices
            socket.setSoTimeout((int) (Constants.getInstance().WAIT_PERIOD * 1000));

            /*
            Infinite loop to receive response from all possible devices
             */
            //noinspection InfiniteLoopStatement
            while (true) {
                packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet); // receive replays from devices for the broad cast massage

                InetAddress address = packet.getAddress(); // get the IP of the replaying device
                arrayListIP.add(address.toString().substring(1));
            }
        } catch (Exception e) {
            this.done = true;
            System.out.println("DiscoverySender - time Out");
        }
    }

    public ArrayList<String> getResults() {
        /*
        While loop that waits until timeout failure is achieved
         */
        while (true) {
            synchronized (lock) {
                if (this.done) {
                    break;
                }
            }
        }

        return this.arrayListIP;
    }
}