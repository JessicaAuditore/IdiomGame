package org.soul.rule;

import javafx.scene.paint.Color;
import org.soul.model.GameApplicationModel;

import java.util.regex.Pattern;

public class GameRule {

    public static GameApplicationModel applicationModel;

    public static Boolean match() {
        String ip=applicationModel.getIp();
        Pattern pattern = Pattern.compile("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$");
        if (!pattern.matcher(ip).matches()) {
            applicationModel.getTip().setText("服务器地址格式不对");
            applicationModel.getTip().setFill(Color.RED);
            return false;
        }else {
            applicationModel.getTip().setText("");
        }
        String id = applicationModel.getId();
        pattern = Pattern.compile("^[0-9]{3}$");
        if (!pattern.matcher(id).matches()) {
            applicationModel.getTip().setText("学号格式不对");
            applicationModel.getTip().setFill(Color.RED);
            return false;
        }else {
            applicationModel.getTip().setText("");
        }
        return true;
    }
}
