package com.club.sysEng;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;


public class MemberService {

    //Constants
    static Constants constant = new Constants();

    // Last generated ID from CSV
    private static int lastGeneratedId = getLastGeneratedIdFromCSV();





    /**
     * The registerMember function is used to register a new member.
     *
     *
     * @param  firstName Store the first name of the member
     * @param  lastName Get the last name of a member
     * @param  dob Get the date of birth from the user
     * @param email Get the email address of a member

     * @param  mobileNumber Check if the mobile number exists in the csv file
     * @param  membershipLevel Determine the membership level of the member
     *
     * @return A string
     */
    public String registerMember(String firstName, String lastName, String dob, String email, String mobileNumber, String membershipLevel) {
        String memberID = generateMemberID();

        try (FileWriter fw = new FileWriter(Constants.MEMBER_DATA_FILE, true)) {
            StringBuilder sb = new StringBuilder();

            // Append header if the file is empty
            if (new File(Constants.MEMBER_DATA_FILE).length() == 0) {
                sb.append(Constants.CSV_HEADER).append("\r\n");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dobDate = dateFormat.parse(dob);

            int age = calculateAge(dobDate);

            // Check if mobile number exists in CSV file
            if (isMobileNumberExists(mobileNumber)) {

                memberID = "Existing customer";
                System.exit(0);
            } else {
                System.out.println("Your registration is completed");
                System.out.println("Welcome to sysEng club");
            }

            // Construct the CSV line
            sb.append(memberID).append(",");
            sb.append(firstName).append(",");
            sb.append(lastName).append(",");
            sb.append(dateFormat.format(dobDate)).append(",");
            sb.append(age).append(",");
            sb.append(email).append(",");
            sb.append(mobileNumber).append(",");
            sb.append(membershipLevel).append(",");
            sb.append(Constants.MEMBERSHIP_ACTIVATED).append(",");
            sb.append(constant.numberOfVisits).append("\r\n");

            // Write the CSV line to the file
            fw.write(sb.toString());
            System.out.println("Your registration is completed");
            System.out.println("Welcome to sysEng club");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return memberID;
    }





    /**
     * The calculateAge function takes a Date object as an argument and returns the age of the person in years.
     *
     *
     * @param dob Calculate the age of the person
     *
     * @return The age of a person in years
     */
    private int calculateAge(Date dob) {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);

        long diffInMillis = currentDate.getTime() - dob.getTime();
        return (int) (diffInMillis / (365 * 24 * 60 * 60 * 1000L));
    }





    /**
     * The isMobileNumberExists function checks if the mobile number exists in the member data file.
     *
     *
     * @param mobileNumber Check if the mobile number exists in the member data file
     *
     * @return A boolean value
     */
    private static boolean isMobileNumberExists(String mobileNumber) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.MEMBER_DATA_FILE))) {
            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                if (!line.trim().isEmpty()) {
                    String[] data = line.split(",");
                    if (data.length > 6 && data[6].equals(mobileNumber)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }





    /**
     * The getLastGeneratedIdFromCSV function reads the member data file and returns the last generated id.
     *
     *
     *
     * @return The last member id in the csv file
     */
    private static int getLastGeneratedIdFromCSV() {
        int lastMemberId = Constants.DEFAULT_STARTING_MEMBER_ID;
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.MEMBER_DATA_FILE))) {
            br.readLine(); // Skip the header

            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (!columns[0].isEmpty()) {
                    lastMemberId = Integer.parseInt(columns[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastMemberId;
    }





    /**
     * The generateMemberID function generates a unique ID for each member.
     * The function takes no parameters and returns the generated ID as a String.

     *
     *
     * @return A string
     */
    public static String generateMemberID() {
        lastGeneratedId++;
        return String.format("%08d", lastGeneratedId);
    }





    /**
     * The updateMemberData function takes in a memberID, columnIndex and updateFunction as parameters.
     * It then checks if the file exists or is empty. If it does not exist or is empty, an exception will be thrown.
     * The function then reads all lines of the file into a list of strings called lines and sets boolean variable
     * isUpdated to false initially. A for loop iterates through each line in the list of strings (lines) and splits
     * each line by commas into columns array which contains all data from that particular row/line in the csv file.
     * If there are more than 1
     *
     * @param  memberID Identify the member
     * @param  columnIndex Specify the column that is to be updated
     * @param updateFunction Pass in a function that will be used to update the data
     *
     * @return The memberid if the update is successful,
     */
    public String updateMemberData(String memberID, int columnIndex, Function<String, String> updateFunction) throws IOException {
        Path path = Paths.get(Constants.MEMBER_DATA_FILE);

        if (Files.notExists(path) || Files.size(path) == 0) {
            throw new FileNotFoundException("File not found or is empty");
        }

        List<String> lines = Files.readAllLines(path);
        boolean isUpdated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(",");
            if (columns.length > columnIndex && columns[0].equals(memberID)) {
                columns[columnIndex] = updateFunction.apply(columns[columnIndex]);
                lines.set(i, String.join(",", columns));
                isUpdated = true;
                break;
            }
        }

        if (isUpdated) {
            Files.write(path, lines);
            return memberID;
        } else {
            throw new IllegalStateException("Membership ID not found");
        }
    }





    /**
     * The cancelMembership function takes in a member ID as a parameter and cancels the membership of that member.
     *
     *
     * @param cancelMemberID Identify the member to be cancelled
     *
     */
    public String cancelMembership(String cancelMemberID) throws IOException {
        Path path = Paths.get(Constants.MEMBER_DATA_FILE);
        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            String[] columns = line.split(",");
            if (columns.length > 8 && columns[0].equals(cancelMemberID) && Constants.MEMBERSHIP_CANCELLED.equals(columns[8])) {
                return "Membership already cancelled";
            }
        }

        return updateMemberData(cancelMemberID, 8, oldValue -> Constants.MEMBERSHIP_CANCELLED);
    }





    /**
     * The checkInMemberID function takes in a memberID and checks to see if the membership has been cancelled.
     * If it hasn't, then it updates the check-in count for that member by 1.

     *
     * @param  memberID Identify the member
     *
     * @return A string
     *
     *
     */
    public String checkInMemberID(String memberID) throws IOException {
        Path path = Paths.get(Constants.MEMBER_DATA_FILE);
        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            String[] columns = line.split(",");
            if (columns.length > 9 && columns[0].equals(memberID)) {
                if (Constants.MEMBERSHIP_CANCELLED.equals(columns[8])) {
                    return "Membership already cancelled";
                }
                break;
            }
        }

        return updateMemberData(memberID, 9, oldValue -> {
            int checkInCount = oldValue.isEmpty() ? 0 : Integer.parseInt(oldValue);
            return String.valueOf(checkInCount + 1);
        });
    }





    /**
     * The isFormValid function checks the validity of a registration form.
     * It returns an empty string if all fields are valid, or a non-empty error message otherwise.
     *
     *
     * @param  firstName Pass the value of the firstnameid edittext to this function
     * @param  lastName Check the length of the last name entered
     * @param  dobFormat Check the date format of the dobid field
     * @param  emailFormat Check if the email address entered by the user is valid
     * @param  mobileNumberFormat Check the format of the mobile number
     *
     * @return An empty string if the form is valid, or an error message otherwise
     */
    public String isFormValid(String firstName, String lastName, String dobFormat, String emailFormat, String mobileNumberFormat) {

        // Check firstNameID length >= 3 characters
        if (firstName.length() <= 3) {
            return "Please enter a full first name with at least 3 characters.";
        }

        // Check lastNameID length >= 3 characters
        if (lastName.length() <= 3) {
            return "Please enter a full last name with at least 3 characters.";
        }

        // Check DobID format (YYYY-MM-DD) and age >= 18
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Ensure strict date parsing
        try {
            Date dob = dateFormat.parse(dobFormat);
            Date eighteenYearsAgo = new Date(System.currentTimeMillis() - 18L * 365 * 24 * 3600 * 1000);
            if (dob.after(eighteenYearsAgo)) {
                return "You must be at least 18 years old to register.";
            }
        } catch (ParseException e) {
            return "Please enter a correct date of birth in YYYY-MM-DD format.";
        }

        // Check email format
        if (!emailFormat.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            return "Please enter a valid email address.";
        }

        // Check mobileNumber format (10 digits)
        if (!mobileNumberFormat.matches("\\d{10}")) {
            return "Please enter a 10-digit mobile number.";
        }

        // If all checks pass, return an empty string to indicate success
        return "";
    }

}
