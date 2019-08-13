import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class ControllerSecond {

    @FXML
    private Button deleteBtn;

    @FXML
    private TableView<Student> tableOfStudents;

    @FXML
    private TableColumn<Student, String> colName;

    @FXML
    private TableColumn<Student, String> colSurname;

    @FXML
    private TableColumn<Student, String> colAge;

    @FXML
    private TableColumn<Student, String> colSex;

    @FXML
    private TableColumn<Student, String> colNameOfCourse;

    @FXML
    private TableColumn<Student, String> colDate;

    @FXML
    private TableColumn<Student, String> colSum;

    @FXML
    private RadioButton DevelRadioBtn;

    @FXML
    private RadioButton DrivingRadioBtn;

    @FXML
    private RadioButton langRadioBtn;

    @FXML
    private RadioButton busRadioBtn;

    @FXML
    private RadioButton desRadioBtn;

    @FXML
    private RadioButton marketingRadioBtn;

    @FXML
    private RadioButton lifestyleRadioBtn;

    @FXML
    private RadioButton photoRadioBtn;

    @FXML
    private RadioButton allRadioBtn;

    @FXML
    void saveFinalFile(ActionEvent event) { //метод запису в файл при нажиманні на клавіщу Save To File
        FileChooser fileChooser = new FileChooser(); //створюємо об'єкт, за домопогою якого можна буде відкрити вікно, задати назву, розширення ноовго файлу

        //Встановлюємо фільтр розширення
        FileChooser.ExtensionFilter extFilter1 =
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

        FileChooser.ExtensionFilter extFilter2 =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");

        fileChooser.getExtensionFilters().addAll(extFilter1,extFilter2,new FileChooser.ExtensionFilter("PDF files (*.pdf)","*.pdf"));

        File file = fileChooser.showSaveDialog(new Stage());

        String newPath = file.getPath(); //записую у стрінгову змінну шлях до файлу

        if(file != null){
            MyStudList.saveFinalFile(MyStudList.myDataList,newPath); //записуєм вказані дані у файл
        }
    }

    @FXML
    void initialize(){

        ToggleGroup group = new ToggleGroup(); //Створюємо групу з Радіо Кнопок, щоб вибрати можна було лише одну Радіо Кнопку

        // Вставляємо наші Радіо Кнопки у группу
        DevelRadioBtn.setToggleGroup(group);
        DrivingRadioBtn.setToggleGroup(group);
        langRadioBtn.setToggleGroup(group);
        busRadioBtn.setToggleGroup(group);
        desRadioBtn.setToggleGroup(group);
        marketingRadioBtn.setToggleGroup(group);
        lifestyleRadioBtn.setToggleGroup(group);
        photoRadioBtn.setToggleGroup(group);
        allRadioBtn.setToggleGroup(group);

        //добавляємо слухача над змінами групи Радіо Кнопок, метод буде спрацьовувати при зміні користувачем Радіо Кнопки
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) ->{

            tableOfStudents.setItems(MyStudList.myDataList); //кожен раз обновляється таблиця значеннями, що і були
            RadioButton selectedItem = (RadioButton) newValue; // Створюємо тимчасовий екземпляр вибраної нами кнопки

            if(!selectedItem.getText().equals("ALL")) { //Якщо текст вибраної кнопеи не = "ALL" - виконується наступний алгоритм

                // створюємо новий масив об'єктів студентів
                ObservableList<Student> mySelectedList = FXCollections.observableArrayList();

                for (Student s : tableOfStudents.getItems()) { // в форІч циклі будемо по одному працювати з кожним студентом із нашої таблиці

                    // Якщо назва курсу студента співпадає із текстом вибраної Радіо Кнопки - даний студент записуєтся в
                    // новий масив наших студентів
                    if (s.belongToCourse.get().equals(selectedItem.getText())) {
                        mySelectedList.add(s);
                    }
                }

                tableOfStudents.setItems(mySelectedList); // в таблицю записуються дані з нового масиву об'єктів
            }
            else{ //і вншому випадку в таблицю записуються наші старі дані
                tableOfStudents.setItems(MyStudList.myDataList);
            }

        });


        //Метод, що спрацьовує при нажиманні на кнопку Delete
        deleteBtn.setOnAction(event->{

            //Видалення клкменту із таблиці
            tableOfStudents.getItems().remove(tableOfStudents.getSelectionModel().getSelectedIndex());

            MyStudList.saveFile(MyStudList.myDataList); //Перезаписування нових даних в файл
            MyStudList.readCSV();                       //Зчитування нових даних в масив об'єктів наших студентів

         });


        //  Вказуємо для кожної колонки таблиці, звідки брати дані
        colName.setCellValueFactory(cellData -> cellData.getValue().name );
        colSurname.setCellValueFactory(cellData ->cellData.getValue().surname);
        colAge.setCellValueFactory(cellData->cellData.getValue().age);
        colSex.setCellValueFactory(cellData->cellData.getValue().sex);
        colNameOfCourse.setCellValueFactory(cellData->cellData.getValue().belongToCourse);
        colDate.setCellValueFactory(cellData->cellData.getValue().perionOfPaying);
        colSum.setCellValueFactory(cellData->cellData.getValue().sum);

        tableOfStudents.setItems(MyStudList.myDataList); // Ставимо в таблицю дані нашого масиву об'єктів
    }
}

class Student{

    //Оголошуємо поля нашого класу, дані, які буде містити студент
    StringProperty name;
    StringProperty surname;
    StringProperty age;
    StringProperty sex;

    StringProperty belongToCourse;
    StringProperty perionOfPaying;
    StringProperty sum;

    Student(){}

    //Конструктор класу, в якому задаються дані наших полів
    Student(String name, String surname,String age,String sex, String belongToCourse, String perionOfPaying, String sum)
    {
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.age = new SimpleStringProperty(age);
        this.sex = new SimpleStringProperty(sex);

        this.belongToCourse = new SimpleStringProperty(belongToCourse);
        this.perionOfPaying = new SimpleStringProperty(perionOfPaying);
        this.sum = new SimpleStringProperty(sum);
    }

    @Override
    public String toString() {  //Перевизначаєм метод виведення даних об`єктів за необхідності
        return this.name +" "+ this.surname +" "+ this.age +" "+ this.sex +" "+this.belongToCourse+" "+ this.perionOfPaying+" "+ this.sum;
    }
}
