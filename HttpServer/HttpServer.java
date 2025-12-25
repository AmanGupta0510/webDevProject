package HttpServer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpServer{

    private static final String HTTP_NEW_LINE_SEPERATOR = "\r\n";
    private static final String HTTP_HEAD_BODY_SEPERATOR = HTTP_NEW_LINE_SEPERATOR+HTTP_NEW_LINE_SEPERATOR;
    private static final int DEFAULT_PACKET_SIZE = 10_000;
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080) ;
        System.out.println("Listening on http://127.0.0.1:8080/");

        while (true) {
            Socket connection = serverSocket.accept();
            var request = readRequest(connection);
            // if(request.isEmpty()){
            //     continue;
            // }
            // System.out.println("We have a request"+request);
            printRequest(request);

            try (var os = connection.getOutputStream()) {

                String body = "{\"id\":1}";
                String response =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;

                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        }
    }

    private static HttpReq readRequest(Socket connection ) throws Exception{
        var stream = connection.getInputStream();
        var toRead = stream.available();
        if(toRead == 0){
            toRead = DEFAULT_PACKET_SIZE;
        }
        var buffer = new byte[toRead];
        var read = stream.read(buffer);
        // if(read<=0){
        //     return Optional.empty();
        // }

        var r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            var line = r.readLine();
            var methodUrl = line.split(" ");
            var method = methodUrl[0];
            var url = methodUrl[1];
            // System.out.println("Request Line = "+line); 
            var headers = readHeader(r); 
            // var body = readBody(connection.getInputStream());  
            
        return new HttpReq(method,url,headers,null);
    }
     
    
    private static Map<String,List<String>> readHeader(BufferedReader reader) throws Exception{
        var line = reader.readLine();

        var headers = new HashMap<String,List<String>>();
        while(line!=null && !line.isEmpty()){
            var keyValue = line.split(":",2);
            var key = keyValue[0];
            var value = keyValue[1];
            headers.computeIfAbsent(key,k ->new ArrayList<>() ).add(value);
            line = reader.readLine();
        }
        return headers;

    }
    
    // private static byte[] readBody(InputStream stream){
    //    return new byte[];
    // }
    private static void printRequest(HttpReq req){
        System.out.println("Methods: "+ req.method);
        System.out.println("Url: "+ req.url);
        System.out.println("Headers:");
        req.header.forEach((k,v)->{
            System.out.println("%s - %s".formatted(k,v));
        });
    }
    private  record HttpReq(String method,String url,Map<String,List<String>> header,byte[] body){

    }

}


                
            
