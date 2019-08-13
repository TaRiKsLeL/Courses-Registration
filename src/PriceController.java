import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.*;

public class PriceController {

    @FXML
    private TableView <CourseInfo> tableOfCourses;

    @FXML
    private TableColumn<CourseInfo, String> colCourse;

    @FXML
    private TableColumn<CourseInfo, String> colPrice;

    @FXML
    void initialize() {

        CourseInfo.readCourseInfo();

        colCourse.setCellValueFactory(cellData->cellData.getValue().courseName);
        colPrice.setCellValueFactory(cellData->cellData.getValue().coursePrice);

        tableOfCourses.setItems(CourseInfo.myCourseAndPricesList);

    }
}

class CourseInfo{
    StringProperty courseName;
    StringProperty coursePrice;

    static ObservableList<CourseInfo> myCourseAndPricesList = FXCollections.observableArrayList();

    static void setCourseNameToList(ObservableList<String> observableList2){
        readCourseInfo();
        for(CourseInfo ci: myCourseAndPricesList) {
            observableList2.add(ci.courseName.get());
        }
    }

    static void readCourseInfo(){

        CourseInfo.myCourseAndPricesList = FXCollections.observableArrayList();

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("CoursesPrices.csv"), "Cp1251"));

            String FieldDelimiter = ";";
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String []fields = line.split(FieldDelimiter,-1);

                CourseInfo record = new CourseInfo(fields[0],fields[1]);
                myCourseAndPricesList.add(record);

            }
            bufferedReader.close();
        }
        catch (FileNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Inform");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());

                alert.showAndWait();

            } catch (IOException ex) {
            }
    }

    CourseInfo(){}

    CourseInfo(String courseName,String coursePrice){

        this.courseName = new SimpleStringProperty (courseName);
        this.coursePrice = new SimpleStringProperty(coursePrice);
    }
}