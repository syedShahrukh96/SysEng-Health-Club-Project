package com.club.sysEng;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ControllerTest {

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
    String checkResult="";


    @Mock
    private MemberService memberService;

    private Controller controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        //controller = new Controller(memberService,checkInNumber, checkedInStatus);
        //controller = new Controller(memberService, firstNameID, lastNameID,DobID,emailID,mobileNumberID,membershipLevelID, checkResult);

    }
    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @AfterAll
    public static void cleanupJFX() {
        Platform.exit();
    }





    @Test
    void processMessageTest() {
        String result = controller.processMessage("10000006");
        assertEquals("Access Granted: 10000006", result);
    }


    @Test
    public void submitCheckInTest() throws IOException {
        // Arrange
        String member_id = "10000005";
        String expectedResult = "10000005";

        when(memberService.checkInMemberID(member_id)).thenReturn(expectedResult);
        TextField textField = new TextField("10000005");
        Label textFieldStatus = new Label("10000005");

        //Controller controller = new Controller(memberService, textField, textFieldStatus);
        // Act
        try {
            controller.SubmitCheckIn();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}