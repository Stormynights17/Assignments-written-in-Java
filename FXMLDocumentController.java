/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ojl8q7fxmlcpumonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author OJlav
 */
public class FXMLDocumentController implements Initializable {
    
    
    @FXML
    private VBox root;
    @FXML
    private ImageView bottomImg;
    @FXML
    private ImageView topImg;
    @FXML
    private HBox buttonBox;
    @FXML
    private Button recordBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private ListView<String> listView;
    @FXML
    private VBox bigBox;
    @FXML
    private Text usageLabel;
    
    Timeline timeline;
    private KeyFrame keyFrame1;
    
    String usage;
    int count;
    
    public double angleDelta = 2.97;
    
    private static double cpu = 0;
    
    Date date = new Date();
    Format formatter = new SimpleDateFormat("hh:mm:ss a");
    String dt;
    
    @FXML
    private void onStartButton(ActionEvent event) {
        //System.out.println("Start clicked!");
        if (!isRunning()) {
            //If not runnning, button is start button
            stopBtn.setText("Stop");
            timeline.play();
            recordBtn.setText("Record");
        }
        else {
            //If running, button is stop button
            stopBtn.setText("Start");
            timeline.pause();
            recordBtn.setText("Reset");
        }
        
    }
    
    @FXML
    private void onRecordButton(ActionEvent event) {
        //System.out.println("Record clicked");
        if (!isRunning()) {
            //If timeline isn't running, this is a reset button
            count = 0;
            listView.getItems().clear();
        }
        else {
            //If timeline is running, this is the record button
            count++;
            
            listView.getItems().add("Record " + count + ": " + usageLabel.getText() + " at " + dt);
        }
    }
    
    public boolean isRunning(){
            if (timeline != null){
                if(timeline.getStatus() == Animation.Status.RUNNING){
                    return true;
                }
            }
            return false;
    }
    
     public double getCPUUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double value = 0;
        
        for(Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            
            if (method.getName().startsWith("getSystemCpuLoad") && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (double) method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = 0;
                }
                
                return value;
            }
        }
        return value;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dt = formatter.format(date);
        
        keyFrame1 = new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
            
            cpu = this.getCPUUsage();
            //System.out.println("CPU: " + cpu); 
            
            usage = String.format("%.2f", cpu*100);
            usageLabel.setText(usage + "%");
            
            double rotation = (cpu * angleDelta * 100) - 150;
            topImg.setRotate(rotation);
            
        } );
        
        timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame1);
        
        timeline.setCycleCount(Animation.INDEFINITE);
    }    
    
}
