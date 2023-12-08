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



    /**
     * The NewMemberRegistration function is used to create a new member registration form.
     *
     *
     *
     * @return A string
     *
     */
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






    /**
     * The cancelMembership function is used to cancel a membership.
     *
     *
     *
     * @return A boolean value
     *
     */
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






    /**
     * The memberCheckIn function is used to open the SysEngMemberCheckIn.fxml file, which allows a user to check in a member
     * by entering their name and email address. The function also sets the title of the window that opens as &quot;SysEng Club Member CheckIn&quot;.

     *
     *
     * @return The membercheckin
     *
     */
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






    /**
     * The SelectLevel function is used to select the membership level that the user wants.
     * The function takes in a string value from the ComboBox and sets it equal to a variable called selectedLevel.
     * Then, it prints out what was selected by using System.out.println(). Finally, it sets membershipPlan equal to
     * whatever was selected in order for us to use this information later on when we create an account for them with their chosen plan type!

     *
     *
     * @return The selected level
     */
    @FXML
    public void SelectLevel() {
        String selectedLevel = membershipLevelID.getValue();
        System.out.println("Selected Level: " + selectedLevel);

        membershipPlan = selectedLevel;
    }




    /**
     * The memberRegistrationSubmit function is called when the user clicks on the submit button.
     * It calls a service layer function to register a member and displays an error message if there are any errors in the form.

     *
     *
     * @return A string
     */
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




    /**
     * The SubmitCheckIn function is called when the user clicks on the &quot;Check In&quot; button.
     * It takes in a member ID number from the text field and passes it to a function that checks if
     * that member has checked in already. If they have, then an error message will be displayed, otherwise
     * their name will be displayed as having checked in successfully.

     *
     *
     * @return A string
     */
    @FXML
    void SubmitCheckIn() throws IOException {
        String member_id = checkInNumber.getText();
        System.out.println("member check in: " + member_id);
        String message = memberService.checkInMemberID(member_id);
        // Process the message to determine if it's a number or text
        message = processMessage(message);
        checkedInStatus.setText(message);
    }





    /**
     * The processMessage function takes in a String message and returns a String.
     * If the message is an 8 digit number, it prepends &quot;Access Granted: &quot; to the front of it.
     * Otherwise, it returns the original message as is.

     *
     * @param message Pass the message that is to be processed
     *
     * @return The message if it is not a number
     */
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






    /**
     * The membershipCancellation function is used to cancel a membership.
     *
     *
     *
     * @return The message that is displayed on the screen
     *
     *
     */
    @FXML
    void membershipCancellation() throws IOException {
        String cancelMemberID =MembershipIdForCancellation.getText();
        System.out.println("membership cancel : "+cancelMemberID);
        String message = memberService.cancelMembership(cancelMemberID);
        memberShipCancelled.setText("Membership Cancelled: "+message);
    }
}
