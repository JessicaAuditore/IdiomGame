package org.soul.model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.soul.Client;

public class GameApplicationModel {

    private final String title = "成语接龙游戏客户端 v2.0";
    private final String instruction = "游戏说明:\n本游戏服务器启动后60s内完成注册\n若参与游戏的学生少于2人或者老师未上线，则游戏无法开始\n----------------";
    private Scene scene;
    private Text sceneTitle, tip;
    private Label ip, id, name, idiom, connection;
    private TextField ipTextField, idTextField, nameTextField, questionTextField, answerTextField;
    private Button register, submit;
    private ImageView imageView;
    private TextArea process;
    private boolean isQuestion = false;

    public GameApplicationModel() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        sceneTitle = new Text("成语接龙游戏客户端");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);
        tip = new Text("未连接服务器...");
        tip.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(tip, 5, 1);

        ip = new Label("服务器IP地址:");
        id = new Label("学号:");
        name = new Label("姓名:");
        idiom = new Label("题目:");
        connection = new Label("答案:");
        grid.add(ip, 0, 1);
        grid.add(id, 0, 3);
        grid.add(name, 0, 5);
        grid.add(idiom, 5, 3);
        grid.add(connection, 5, 5);

        ipTextField = new TextField();
        ipTextField.setText("127.0.0.1");
        idTextField = new TextField();
        nameTextField = new TextField();
        questionTextField = new TextField();
        questionTextField.setEditable(false);
        answerTextField = new TextField();
        grid.add(ipTextField, 0, 2);
        grid.add(idTextField, 0, 4);
        grid.add(nameTextField, 0, 6);
        grid.add(questionTextField, 5, 4);
        grid.add(answerTextField, 5, 6);

        register = new Button("注册");
        register.setMinSize(200, 20);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(register);
        grid.add(hbBtn, 0, 7);

        submit = new Button("提交");
        submit.setMinSize(200, 20);
        submit.setDisable(true);
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.CENTER);
        hbBtn1.getChildren().add(submit);
        grid.add(hbBtn1, 5, 7);

        imageView = new ImageView();
        imageView.setImage(new Image(Client.class.getResourceAsStream("image/picture.jpg"), 250, 250, true, true));
        HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(imageView);
        grid.add(pictureRegion, 9, 1, 2, 7);

        process = new TextArea();
        process.setText(instruction);
        process.setEditable(false);

        grid.add(process, 0, 8, 17, 7);

        scene = new Scene(grid, 800, 550);
    }

    public Scene getScene() {
        return scene;
    }

    public String getTitle() {
        return title;
    }

    public Button getRegister() {
        return register;
    }

    public Button getSubmit() {
        return submit;
    }

    public String getIp() {
        return ipTextField.getText().trim();
    }

    public String getId() {
        return idTextField.getText().trim();
    }

    public String getName() {
        return nameTextField.getText().trim();
    }

    public Text getTip() {
        return tip;
    }

    public TextArea getProcess() {
        return process;
    }

    public void setProcess(String content) {
        getProcess().setText(content);
    }

    public void addProcess(String content) {
        getProcess().appendText("\n" + content);
    }

    public TextField getAnswerTextField() {
        return answerTextField;
    }

    public TextField getQuestionTextField() {
        return questionTextField;
    }

    public boolean getIsQuestion() {
        return isQuestion;
    }

    public void setIsQuestion(boolean isQuestion) {
        this.isQuestion = isQuestion;
    }
}
