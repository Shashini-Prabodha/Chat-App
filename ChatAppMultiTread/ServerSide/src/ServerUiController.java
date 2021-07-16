import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerUiController {
    public TextArea txtMessageArea;
    public TextField txtMessage;

    static ServerSocket serverSocket;
    static Socket socket;
    static DataInputStream dataInputStream;
    static PrintWriter writer;
    static ArrayList<ClientHandler> client = new ArrayList<>();

    String messageIn = "";

    public void initialize() throws InterruptedException {

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                System.out.println("Server Started");
               txtMessageArea.appendText("Server Started...!\n");

                while (true) {

                    socket = serverSocket.accept();
                    System.out.println("socket--> "+socket);
                    txtMessageArea.appendText("Client Accepted!\n");
                    System.out.println("Client Accepted!");

                    ClientHandler cliendHandler = new ClientHandler(socket, client);
                    client.add(cliendHandler);
                    System.out.println(client.toString());
                    cliendHandler.start();
                }

            } catch (IOException e) {

            }
        }).start();
    }


    public void btnSentOnAction(ActionEvent actionEvent) throws IOException {
        if (!txtMessage.getText().trim().equalsIgnoreCase("")) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            for (ClientHandler cl : client) {
                cl.writer.println("Server : " + txtMessage.getText());

            }
            txtMessageArea.appendText("You : " + txtMessage.getText() + "\n ");
            txtMessage.setText("");
        }
    }

    public void btnCamOnAction(ActionEvent actionEvent) {
    }

    public void btnAttachOnAction(MouseEvent mouseEvent) {
    }

    public void btnSettingOnAction(ActionEvent actionEvent) {
    }

    public void btnOffOnAction(ActionEvent actionEvent) {
    }

    public void btnContactOnAction(ActionEvent actionEvent) {

    }
}
