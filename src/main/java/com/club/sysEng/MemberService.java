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





    private int calculateAge(Date dob) {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);

        long diffInMillis = currentDate.getTime() - dob.getTime();
        return (int) (diffInMillis / (365 * 24 * 60 * 60 * 1000L));
    }





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





    public static String generateMemberID() {
        lastGeneratedId++;
        return String.format("%08d", lastGeneratedId);
    }





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
