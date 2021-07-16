import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class LoginClientUiController {
    public JFXTextField txtId;
    static Socket socket;
    static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;
    public JFXButton btnConnect;
    public static ArrayList<User> users = new ArrayList<>();
    public static HashSet<String> LoggedInUsers = new HashSet<>();
    public static String userName;

    public void btnConnect(ActionEvent actionEvent) {
        boolean checkUserResult = checkUser(txtId.getText());
        System.out.println(txtId.getText()+" txtid ******  "+checkUserResult);
        if (checkUserResult){
            loadChat();
        }else {
            new Alert(Alert.AlertType.ERROR, "User Name Already exsist..!").show();
            txtId.setText("");
        }
        for (User user : users) {
            System.out.println("onnnn "+ user.name);
        }
    }

    private boolean checkUser(String username) {
        System.out.println("loop out");

        for (User user : users) {
            System.out.println("loop in");
            System.out.println(user.name +": ");

            if (user.name.equalsIgnoreCase(username)) {
                return false;
            }
        }
        userName=username;
        users.add(new User(userName));
        return true;
    }

    private void loadChat(){
        try {

            Stage exitstage = (Stage) btnConnect.getScene().getWindow();
            exitstage.close();
            URL resource = this.getClass().getResource("view/ClientUi.fxml");
            Parent load = FXMLLoader.load(resource);
            Scene scene = new Scene(load);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            anableMove(scene,stage);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void anableMove(Scene scene, Stage primaryStage) {
        AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
        scene.setOnMousePressed(event -> {
            xOffset.set(primaryStage.getX() - event.getScreenX());
            yOffset.set(primaryStage.getY() - event.getScreenY());
        });
        //Lambda mouse event handler
        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset.get());
            primaryStage.setY(event.getScreenY() + yOffset.get());
        });
    }
}
