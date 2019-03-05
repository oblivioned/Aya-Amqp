import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.web3j.crypto.Hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class GatewayServices {

    static final String AMQP_HOST   = "localhost";
    static final String AMQP_User   = "guest";
    static final String AMQP_PWD    = "guest";
    static final String AMQP_VHOST  = "/test";
    static final int    AMQP_PORT   = 5672;
    static final ConnectionFactory AMQPConnectionFactory = new ConnectionFactory();

    static Connection AMQP_Connect;
    static Channel    AMQP_Channel;

    public static void InitAQMPConnection() throws Exception
    {
        AMQPConnectionFactory.setHost(AMQP_HOST);
        AMQPConnectionFactory.setUsername(AMQP_User);
        AMQPConnectionFactory.setPassword(AMQP_PWD);
        AMQPConnectionFactory.setVirtualHost(AMQP_VHOST);
        AMQPConnectionFactory.setPort(AMQP_PORT);

        AMQP_Connect = AMQPConnectionFactory.newConnection();
        AMQP_Channel = AMQP_Connect.createChannel();
    }

    public static boolean PublishMessage(String queueName, byte[] message) throws Exception
    {
        if ( !AMQP_Channel.isOpen() )
        {
            return false;
        }

        AMQP_Channel.queueDeclare(queueName, false, false, false ,null);
        AMQP_Channel.basicPublish("", queueName,null, message);

        return true;
    }

    public static void main(String[] args) throws Exception {

        try {
            InitAQMPConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HttpServerHandle());
        server.start();
    }

    static class HttpServerHandle implements HttpHandler {

        public void handle(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath().substring(1);

            InputStream requestBodyStream = httpExchange.getRequestBody();
            OutputStream os = httpExchange.getResponseBody();

            byte[] contentB = new byte[requestBodyStream.available()];

            if ( requestBodyStream.read(contentB) > 0)
            {
                String message = new String(contentB);

                try {

                    if ( PublishMessage(path, contentB) )
                    {
                        httpExchange.sendResponseHeaders(200, 0);
                        os.write(bytesToHexString(Hash.sha256(contentB)).getBytes());
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                httpExchange.sendResponseHeaders(200, 0);
                os.write("Miss parmas!".getBytes());
            }
            os.close();
        }

    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
