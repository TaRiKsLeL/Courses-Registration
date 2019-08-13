import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.BufferedWriter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class Controller {

    ObservableList<String>  comboCourseList = FXCollections.observableArrayList();
    //Створюємо список назв курсів
    ObservableList<String>  comboSexList = FXCollections.observableArrayList("Male","Female");
    //Створюємо список стАтей
    BufferedWriter bw = null;

    @FXML
    private Button submitBtn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField snameField;

    @FXML
    private TextField ageField;

    @FXML
    private Button pricesBtn;

    @FXML
    private DatePicker periodField;

    @FXML
    private Label sumLabel;

    @FXML
    private Button cleanBtn;

    @FXML
    private ComboBox<String> comboCourse;

    @FXML
    private ComboBox<String> sexBox;


    @FXML
    void initialize(){ // Метод який спрацьовує при запуску вікна

        try{
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MyStudList.PATH,true),"Cp1251"));
            //Ініціалізовуємо записувач

              String FIRST_ROW = "Name;Surname;Age;Sex;Name of course;Period;Sum"; //Перша строка нашої таблиці

            FileReader fileReader = new FileReader(MyStudList.PATH);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine();
            if (line == null ||
                    (line.length() == 0 && br.readLine() == null)) {
                bw.write(FIRST_ROW);                                      //Ця перша строка запишеться якщо файл пустий
                bw.newLine();
                System.out.println("IS EMPTY ;)");
            } else {
                System.out.println("FILE ISN'T EMPTY ;)");
            }

        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        CourseInfo.setCourseNameToList(comboCourseList);

        comboCourse.setPromptText("Choose course"); // Ставимо початковий текст у КомбоБокс
        comboCourse.setItems(comboCourseList);      // Ставимо наш список у КомбоБокс

        sexBox.setPromptText("Choose sex");         // Ставимо початковий текст у КомбоБокс
        sexBox.setItems(comboSexList);              // Ставимо наш список у КомбоБокс

        periodField.valueProperty().addListener((observable, oldDate, newDate)->{
            try{

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.now();
            System.out.println(dtf.format(localDate)); //2016/11/16


            if(!(comboCourse.getValue().equals(null) && newDate.equals(null))){

                System.out.println(newDate);

                System.out.println(MyStudList.getDayCount(dtf.format(localDate),newDate.toString()));

                for(CourseInfo ci : CourseInfo.myCourseAndPricesList) {

                    if(ci.courseName.get().equals(comboCourse.getValue())){
                        sumLabel.setText((MyStudList.priceForDays(MyStudList.getDayCount(dtf.format(localDate),newDate.toString()),ci.coursePrice.getValue())));
                    }
                }
            }

            else{
                sumLabel.setText("0");
            }
                }
            catch (NullPointerException n){}
        });

        comboCourse.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

            try{

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.now();


                if(!(newValue.equals(null) && periodField.getValue().equals(null))){

                    System.out.println(MyStudList.getDayCount(dtf.format(localDate),periodField.getValue().toString()));
                    for(CourseInfo ci : CourseInfo.myCourseAndPricesList) {

                        if(ci.courseName.get().equals(newValue)){
                            sumLabel.setText((MyStudList.priceForDays(MyStudList.getDayCount
                                    (dtf.format(localDate),periodField.getValue().toString()),ci.coursePrice.getValue())));
                        }
                    }
                }

                else{
                    sumLabel.setText("0");
                }
            }
            catch (NullPointerException n){}
        });

        submitBtn.setOnAction(event -> {            // Метод який спрацьовує пр нажиманні на кнопку підтвердження

            int a=0; //Шоб 2 раза не вискакувало сповіщення 1-про те, що тип в полі не правильний, 2 -про те що поле пусте, передаємо
            //мітку, яка при виконанні не видасть нам друге сповіщення

            MyStudList.readCSV();
            for(Student st : MyStudList.myDataList){

                if(nameField.getText().equals(st.name.get()) && snameField.getText().equals(st.surname.get())&&comboCourse.getValue().equals(st.belongToCourse.get())){

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                    alert.setTitle("Warning");
                    alert.setHeaderText("Student with such a name and course is already added");
                    alert.setContentText("Change data?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if(result.get()==ButtonType.OK){
                        a=1;
                        MyStudList.myDataList.remove(st);
                        Student temp = new Student(nameField.getText(),snameField.getText(),ageField.getText(),sexBox.getValue().toString(), comboCourse.getValue(),periodField.getValue().toString(),sumLabel.getText());

                        MyStudList.myDataList.add(temp);
                        MyStudList.saveFile(MyStudList.myDataList);
                        break;
                    }
                }
            }
            MyStudList.myDataList = FXCollections.observableArrayList();

            if(ageField.getText().matches("^\\D*$") || ageField.getText().equals("")) {
                //          Вивести інформацію на екран, якщо в полі вводу віку присутні якісь літери

                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Введіть значення віку ще раз");
                alert.showAndWait();

                ageField.setText(null);
                a=1;
        }

            if(sexBox.getSelectionModel().isEmpty()){

                //          Вивести інформацію на екран, якщо в полі вводу статі нічого не вибрано

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Введіть стать");
                alert.showAndWait();

                a=1;
            }

            if(comboCourse.getSelectionModel().isEmpty()){

                //          Вивести інформацію на екран, якщо в полі вибору курсу нічого не вибрано

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Введіть бажаний курс");
                alert.showAndWait();

                a=1;
            }

            if(a==0){
                try {

                    //      Запис даних з полів в файл, між якими - ;, щоб розділяти кожен елемент

                    bw.write(nameField.getText());
                    bw.write(";");
                    bw.write(snameField.getText());
                    bw.write(";");
                    bw.write(ageField.getText());
                    bw.write(";");
                    bw.write(sexBox.getValue()+"");
                    bw.write(";");
                    bw.write(comboCourse.getValue()+"");
                    bw.write(";");
                    bw.write(periodField.getValue().toString());
                    bw.write(";");
                    bw.write(sumLabel.getText());
                    bw.write(";");
                    bw.newLine();
                    bw.flush();


                    //          Вивести інформацію на екран, якщо новий студент був добавлений в файл

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);

                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("New student was added!");

                    alert.showAndWait();

                } catch (Exception ex) {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);

                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText(ex.getMessage());
                    //          Вивести інформацію на екран, якщо сталася помилка при добавленні студента в файл

                    alert.showAndWait();
                }}

                System.out.println("Ви нажали на кнопку підтвердження");

            //Очищення полів після запису студента в файл

            comboCourse.setValue("Choose course");
            sexBox.setValue("Choose sex");
            nameField.setText(null);
            periodField.setValue(null);
            snameField.setText(null);
            ageField.setText(null);
            sumLabel.setText("0");

        });

        cleanBtn.setOnAction(event -> {

            //Очищення полів після натискання на кнопку очищення

            comboCourse.setValue("Choose course");
            sexBox.setValue("Choose sex");
            nameField.setText(null);
            periodField.setValue(null);
            snameField.setText(null);
            ageField.setText(null);
            sumLabel.setText("0");

        });

        pricesBtn.setOnAction(event -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PricesActivity.fxml"));
                Parent root2 =(Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root2));
                stage.show();
            }catch (Exception e){}
        });

    }

    @FXML
    public void showOtherWindow(javafx.event.ActionEvent actionEvent) {
        // Метод, що спрацьовує після натискання на кнопку Base Of Students

        MyStudList.readCSV(); //Виклик методу зчитування даних з файлу

        try{ //                 Виклик нового вікна
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecondActivity.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }
}

class MyStudList{

    final static String PATH = "MyData.csv";    //Назва файлу, куда записуються і звідки зчитуються дані

   static ObservableList<Student> myDataList;   //Список об'єктів студентів


   static void readCSV(){
       MyStudList.myDataList = FXCollections.observableArrayList(); //Щоб та сама інформація не добавлялась кожен раз

       String FieldDelimiter = ";";                                 //Роздільник між значенніми полів

       BufferedReader br2;

       try {
           br2 = new BufferedReader(new InputStreamReader(new FileInputStream(PATH),"Cp1251"));
            //Задаємо зчитатувач

           String line = br2.readLine();                            //Пропускаємо перший рядок, в якому назви полів
           while ((line = br2.readLine()) != null) {
               String[] fields = line.split(FieldDelimiter, -1); //записуємо в тимчасовий масив значення в рядку, розділені ;

               Student record = new Student(fields[0], fields[1], fields[2], fields[3], fields[4],fields[5] ,fields[6]);
               MyStudList.myDataList.add(record); //Добавляємо в наж список об'єктів нового студента

           }
           br2.close();

       } catch (FileNotFoundException ex) { //   Вивести інформацію на екран, якщо сталася помилка при зчитуванні з файла
           Alert alert = new Alert(Alert.AlertType.INFORMATION);

           alert.setTitle("Information");
           alert.setHeaderText(null);
           alert.setContentText(ex.getMessage()+"");

           alert.showAndWait();

       } catch (IOException ex) {
       }
   }

   static void saveFile(ObservableList<Student> observableStudentList){
       try{
           BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH),"Cp1251"));
            //створюємо об'єкт, з яким ми працюємо при записі в файл

           saveDataFile(observableStudentList,outWriter); //викликаєм метод запису в файл з заданим масивом об'єктів і об'єктом запису в файл, де заданий файл, куди записуються дані
       }
       catch (Exception e){}
   }

   //загальний метод запису в файл при нажиманні на клавіщу Save To File
    static void saveFinalFile(ObservableList<Student> observableStudentList,String file){
        try{
            BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"Cp1251"));

            saveDataFile(observableStudentList,outWriter); //викликаєм метод запису в файл з заданим масивом об'єктів і об'єктом запису в файл, де заданий файл, куди записуються дані
        }
        catch (Exception e){}
    }

    static void saveDataFile(ObservableList<Student> observableStudentList,BufferedWriter outWriter) {
       try{
        outWriter.write("Name;Surname;Age;Sex;Name of course;Period;Sum"); //перший рядок в файлі
        outWriter.newLine(); // перехід на новий рядок

        for(Student students: observableStudentList){  //працюємо з об'єктами студентів, витягуючи по одному студенту і працюючи з ним із кожною ітерацією циклу
            outWriter.write(students.name.getValue().toString());          //отримуємо дані з поля Імені з записуємо в файл
            outWriter.write(";");                                      // розділяємо дані з полів
            outWriter.write(students.surname.getValue().toString());       //отримуємо дані з поля Імені з записуємо в файл
            outWriter.write(";");
            outWriter.write(students.age.getValue().toString());
            outWriter.write(";");
            outWriter.write(students.sex.getValue().toString());
            outWriter.write(";");
            outWriter.write(students.belongToCourse.getValue().toString());
            outWriter.write(";");
            outWriter.write(students.perionOfPaying.getValue().toString());
            outWriter.write(";");
            outWriter.write(students.sum.getValue().toString());
            outWriter.write(";");
            outWriter.newLine();
        }
        outWriter.close();

    }
    catch(Exception ex){}
}

    static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static int getDayCount(String start, String end) {
        long diff = -1;
        try {
            Date dateStart = simpleDateFormat.parse(start);
            Date dateEnd = simpleDateFormat.parse(end);

            //time is always 00:00:00 so rounding should help to ignore the missing hour when going from winter to summer time as well as the extra hour in the other direction
            diff = Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000);
        } catch (Exception e) {
            //handle the exception according to your own situation
        }
        return (int)diff;
    }

    static String priceForDays(int Days,String priceMonth){
        double oneDay =(double)Integer.parseInt(priceMonth)/31;

        return ((int)(oneDay*Days))+"";
    }

}