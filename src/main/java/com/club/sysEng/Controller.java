package com.club.sysEng;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


public class Controller  {

    @FXML
    public TextField DobID;

    @FXML
    public TextField emailID;

    @FXML
    public TextField firstNameID;

    @FXML
    public TextField lastNameID;

    @FXML
    public ComboBox<String> membershipLevelID;

    @FXML
    public TextField mobileNumberID;

    @FXML
    private TextField checkInNumber;

    @FXML
    private TextField MembershipIdForCancellation;

    @FXML
    private String membershipPlan;

    @FXML
    private Label checkedInStatus;

    @FXML
    public Label registrationSuccess;

    @FXML
    private Label memberShipCancelled;

    @FXML
    public Label membershipID;

    @FXML
    private Label errorMessage;

    @FXML
    private Label welcomeComment;



    String firstName;
    String lastName;
    String Dob;
    String email;
    String mobileNumber;
    String membershipLevel;

    String checkResult;
    private MemberService memberService = new MemberService();



     //enable only when doing testing: contructore for Controller Test - checkIn it will mock the controller constants.
//    public Controller(MemberService memberService, TextField checkInNumber, Label checkedInStatus) {
//        this.memberService = memberService;
//        this.checkInNumber = checkInNumber;
//        this.checkedInStatus = checkedInStatus;
//    }



    @FXML
    public void NewMemberRegistration() {
        //public String NewMemberRegistration(ActionEvent event)
        System.out.println("NewMemberRegistration");
        try {
            FXMLLoader loader  = new FXMLLoader(getClass().getResource("SysEngMemberRegistration.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("SysEng Club Member Registration");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

    }






    @FXML
    public void cancelMembership(){
        System.out.println("cancelMembership");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SysEngMemberCancellation .fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("SysEng Club Member Cancellation");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }






    @FXML
    public void memberCheckIn() {
        System.out.println("checkInMembership");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SysEngMemberCheckIn.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("SysEng Club Member CheckIn");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }






    @FXML
    public void SelectLevel() {
        String selectedLevel = membershipLevelID.getValue();
        System.out.println("Selected Level: " + selectedLevel);

        membershipPlan = selectedLevel;
    }




    @FXML
    void memberRegistrationSubmit() {

        String result;

        firstName = firstNameID.getText();
        System.out.print(firstName + " ,");

        lastName = lastNameID.getText();
        System.out.print(lastName + " ,");

        Dob = DobID.getText();
        System.out.print(Dob + " ,");

        email = emailID.getText();
        System.out.print(email + " ,");

        mobileNumber = mobileNumberID.getText();
        System.out.print(mobileNumber + " ,");

        membershipLevel = membershipLevelID.getValue();
        System.out.print(membershipPlan);

        checkResult = memberService.isFormValid(firstName, lastName, Dob, email, mobileNumber);
        System.out.println("the checkResult is: "+checkResult);

        if (checkResult.isEmpty()) {
            // Call the service layer to register the member
            result = memberService.registerMember(firstName, lastName, Dob, email, mobileNumber, membershipPlan);
            welcomeComment.setText("Welcome to SysEng Club");
            registrationSuccess.setText("Registration is successfully done");
            membershipID.setText("Membership ID: " + result);
        } else {
            // Show error message
            errorMessage.setText(checkResult);
            errorMessage.setTextFill(Color.RED);
        }
    }




    @FXML
    void SubmitCheckIn() throws IOException {
        String member_id = checkInNumber.getText();
        System.out.println("member check in: " + member_id);
        String message = memberService.checkInMemberID(member_id);
        // Process the message to determine if it's a number or text
        message = processMessage(message);
        checkedInStatus.setText(message);
    }





    public String processMessage(String message) {
        String numberRegex = "\\b\\d{8}\\b";
        if (message.matches(numberRegex)) {
            // If the message is a number, prepend "access Granted: "
            return "Access Granted: " + message;
        } else {
            // If the message is not a number, return it as is
            return message;
        }
    }






    @FXML
    void membershipCancellation() throws IOException {
        String cancelMemberID =MembershipIdForCancellation.getText();
        System.out.println("membership cancel : "+cancelMemberID);
        String message = memberService.cancelMembership(cancelMemberID);
        memberShipCancelled.setText("Membership Cancelled: "+message);
    }
}
