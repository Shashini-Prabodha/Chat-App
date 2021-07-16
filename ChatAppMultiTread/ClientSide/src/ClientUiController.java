
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientUiController extends Thread {
    public Text txtClientName;
    public JFXTextArea txtMessageArea;
    public TextField txtMessage;
    public JFXComboBox txtCombo;
    public JFXTextField txtId;
    public JFXButton btnConnect;
    public JFXButton btnAttach;
    public static final int BUFFER_SIZE = 500102;
    public JFXButton btnOff;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    BufferedReader in;
    PrintWriter writer;
    int filesize = 6022386; // filesize temporary hardcoded

    long start = System.currentTimeMillis();
    int bytesRead;
    int current = 0;

    public void initialize() {
        connectSocket();
        txtClientName.setText(LoginClientUiController.userName);
    }

    private void connectSocket() {
        try {
            socket = new Socket("localhost", 5000);
            System.out.println("Connect with server...!");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(LoginClientUiController.userName + " Added...!");

            this.start();

        } catch (IOException e) {

        }
    }

    public void run() {
        try {
            while (true) {
                String msg = in.readLine();
                txtMessageArea.appendText(msg + "\n");

                {
                 /*   ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    byte[] buffer = (byte[])ois.readObject();
                    FileOutputStream fos = new FileOutputStream(txtMessage.getText());
                    fos.write(buffer);
                    writer.println(buffer);*/

                    if (msg.endsWith("jpg")) {
                        /*File myFile = new File(msg);
                        byte[] mybytearray = new byte[(int) myFile.length()];
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                        bis.read(mybytearray, 0, mybytearray.length);
                        OutputStream ost = socket.getOutputStream();
                        ost.write(mybytearray, 0, mybytearray.length);
                        ost.flush();*/
                        rece(msg);
                        System.out.println("image in");

                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void btnSentOnAction(ActionEvent actionEvent) throws IOException {

        sendMessage(txtMessage.getText());
        for (User user : LoginClientUiController.users) {
            System.out.println(user.name + " btn Sent");
        }
    }


    public void sendMessage(String msg) {
        if (!msg.trim().equalsIgnoreCase("")) {
            writer.println(LoginClientUiController.userName + ": " + msg);
            txtMessage.setText("");
            if (msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {

                System.exit(0);
            }
        }

    }

    public void btnOffOnAction(ActionEvent actionEvent) {
        sendMessage("logout...!");
        Stage stage = (Stage) btnOff.getScene().getWindow();
        stage.close();
    }

    public void btnEmogiOnAction(ActionEvent actionEvent) {
    }

    public void btnCamOnAction(ActionEvent actionEvent) {

    }

    public void btnAttachOnAction(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        Stage stage = (Stage) btnAttach.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            txtMessage.setText(selectedFile.getAbsolutePath());
        }

        send(txtMessage.getText());
    }

    public void send(String file_name) throws IOException {
        File myFile = new File(file_name);
        byte[] mybytearray = new byte[(int) myFile.length()];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = socket.getOutputStream();
        System.out.println("Sending...");
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
    }

    private void se(String fileName) throws IOException {
        int flag = 0, i;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        String extn = "";
        for (i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) == '.' || flag == 1) {
                flag = 1;
                extn += fileName.charAt(i);
            }
        }

        if (extn.equals(".jpg") || extn.equals(".png")) {
            try {

                File file = new File(fileName);
                FileInputStream fin = new FileInputStream(file);
                dataOutputStream.writeUTF(fileName);
                System.out.println("Sending image...");
                byte[] readData = new byte[1024];

                while ((i = fin.read(readData)) != -1) {
                    dataOutputStream.write(readData, 0, i);
                }
                System.out.println("Image sent");
                txtMessageArea.appendText("\nImage Has Been Sent");
                fin.close();
            } catch (IOException ex) {
                System.out.println("Image ::" + ex);
            }
        }
    }

    static int i = 0;

    private void r(String fileName) throws IOException {
        String str;
        int flag = 0;
        str = dataInputStream.readUTF();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            File file = new File("RecievedImage" + str);
            FileOutputStream fout = new FileOutputStream(file);

            //receive and save image from client
            byte[] readData = new byte[1024];
            while ((i = dataInputStream.read(readData)) != -1) {
                fout.write(readData, 0, i);
                if (flag == 1) {
                    System.out.println("Image Has Been Received");
                    flag = 0;
                }
            }
            fout.flush();
            fout.close();
        }
    }

    public void rece(String file) throws IOException {
        byte[] mybytearray = new byte[filesize];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray, 0, mybytearray.length);
        current = bytesRead;

        do {
            bytesRead =
                    is.read(mybytearray, current, (mybytearray.length - current));
            if (bytesRead >= 0) current += bytesRead;
        } while (bytesRead > -1);

        bos.write(mybytearray, 0, current);
        bos.flush();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        bos.close();
    }

}
