import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by marvinbernal on 2014-03-31.
 *
 * A node which forwards packets from incoming nodes to other nodes.
 *
 * Specifically this node can receive packets from the source or other forwarders and forward them to other forwarders
 * or receiver nodes.
 *
 */
final class ForwarderNode extends Node{



    /**
     * Primary node constructor.
     *
     * @param routerID
     * @param role
     * @param listeningPort
     * @param receivingPacketRate
     */
    public ForwarderNode(double routerID, Role role, String stringAddressOfNode, int listeningPort, int receivingPacketRate) {
        super(routerID, Role.FORWARDER, stringAddressOfNode, listeningPort, receivingPacketRate);

    }
    /**
     * A lazy constructor only requiring ID, role and listening for node creation.
     *
     * IP Address and packet rate are set to default.
     *
     * @param routerID      Router ID.
     * @param listeningPort Port to listen for incoming connections.
     */
    public ForwarderNode(double routerID, int listeningPort) {
        this(routerID, Role.FORWARDER, Node.LOCAL_HOST, listeningPort, Node.DEFAULT_RECEIVING_PACKET_RATE);
    }

    /**
     * Initialize will begin to listen for incoming connections and proceed to forward it to the next hop node as denoted
     * in the packet structure.
     *
     */
    @Override
    public void initialize() {
        new Thread(){
            @Override
            public void run(){

                while(true){
                    try{
                        setSocket(getServerSocket().accept()); // blocks until a connection is made
                        DataInputStream in = new DataInputStream(getSocket().getInputStream());
                        String incomingMessage = in.readUTF();
                        System.out.println("<Node " + getRouterID() + " @ " + getIPAddress() + " receives message " + incomingMessage + "\" from remote address " + getSocket().getRemoteSocketAddress());

                        DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
                        out.writeUTF("--- Node " + getRouterID() + " acknowledges message from " + getSocket().getRemoteSocketAddress());

                        // Forward Data
                        sendPacket(Integer.parseInt(incomingMessage), incomingMessage);
                        getSocket().close();

                    }catch(SocketTimeoutException socketTimeoutException){

                        System.out.println("Socket timed out!");

                    }catch(IOException iOException){
                        iOException.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
