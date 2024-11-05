package method;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import configs.dataPaths;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SendEmail {

    private static final int DAILY_LIMIT = 100;
    private static final int MONTHLY_LIMIT = 3000;

    private int dailyEmailCount = 0;
    private int monthlyEmailCount = 0;


    private SendGrid createSendGridClient() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(dataPaths.dataBasePath + "\\src\\test\\resources\\configFiles\\")
                    .filename("environmentFiles.env")
                    .load();
            String  apiKey = dotenv.get("SENDGRID_API_KEY");


            if (apiKey == null) {
                System.out.println("SENDGRID_API_KEY not found.");
                throw new IllegalArgumentException("API key not found.");
            }
            return new SendGrid(apiKey);
        } catch (Exception e) {
            System.out.println("Error loading environment variables: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create SendGrid client.");
        }
    }

    public void sendEmailWithTemplateAndJson(
            List<String> attachmentLocationPath,
            String testAutomationGroup,
            int scenarioCount,
            int testCasesCount,
            int passedCount,
            int failedCount,
            int skippedCount
    ) {
        SendGrid sg = createSendGridClient();
        Request request = new Request();

        // Track email counts to respect SendGrid's limits
        if (dailyEmailCount >= DAILY_LIMIT || monthlyEmailCount >= MONTHLY_LIMIT) {
            System.out.println("Email limit reached. Cannot send more emails today.");
            return;
        }

        try {
            // Set the request method to POST and the endpoint
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // Create email details
            Email from = new Email("sreenivasulu@travelstart.com");
            String[] recipients = {"sreenivas.tsqa@gmail.com"};

            Mail mail = new Mail();
            mail.setFrom(from);

            // Add multiple recipients
            Personalization personalization = new Personalization();
            for (String recipientEmail : recipients) {
                Email to = new Email(recipientEmail);
                personalization.addTo(to);
            }

            // Set dynamic template ID (replace with your actual template ID)
            mail.setTemplateId("d-fe3df44aaf5340a4945f035ccb1fa941");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);

            // Pass dynamic values as JSON (from method arguments)
            personalization.addDynamicTemplateData("username", System.getProperty("user.name").toUpperCase());
            personalization.addDynamicTemplateData("testRunTimeStamp", timestamp);
            personalization.addDynamicTemplateData("failedCount", failedCount);
            personalization.addDynamicTemplateData("scenarioCount", scenarioCount);
            personalization.addDynamicTemplateData("testCasesCount", testCasesCount);
            personalization.addDynamicTemplateData("passedCount", passedCount);
            personalization.addDynamicTemplateData("skippedCount", skippedCount);
            personalization.addDynamicTemplateData("testAutomationGroup", testAutomationGroup);

            mail.addPersonalization(personalization);

            // Attach all files in attachmentLocationPath
            for (String filePath : attachmentLocationPath) {
                File file = new File(filePath);
                if (file.exists()) {
                    Attachments attachments = new Attachments();
                    attachments.setContent(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));
                    attachments.setType(Files.probeContentType(file.toPath()));
                    attachments.setFilename(file.getName());
                    attachments.setDisposition("attachment");
                    mail.addAttachments(attachments);
                } else {
                    System.out.println("File not found: " + filePath);
                }
            }

            // Add the mail object to the request body
            request.setBody(mail.build());

            // Send the request
            Response response = sg.api(request);
            if (response.getStatusCode() == 202) {
                System.out.println("Email report has been sent using the template with dynamic JSON data");
            } else {
                System.out.println("Email sending failed");
                System.out.println(String.format("Status Code: %d", response.getStatusCode()));
                System.out.println("Response Body: " + response.getBody());
                System.out.println("Response Headers: " + response.getHeaders());
            }

            // Update email counts
            dailyEmailCount++;
            monthlyEmailCount++;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void sendEmail(String subject, List<String> attachmentLocationPath) {
        SendGrid sg = createSendGridClient();
        Request request = new Request();

        // Track email counts to respect SendGrid's limits
        if (dailyEmailCount >= DAILY_LIMIT || monthlyEmailCount >= MONTHLY_LIMIT) {
            System.out.println("Email limit reached. Cannot send more emails today.");
            return;
        }

        try {
            // Set the request method to POST and the endpoint
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // Create email details
            Email from = new Email("sreenivasulu@travelstart.com");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            subject = String.format(subject+" %s", timestamp);

            String[] recipients = {"sreenivas.tsqa@gmail.com"};
            Content content = new Content("text/plain", String.format("""
                Dear Stakeholders,

                Please find attached the detailed automation test result, executed on %s. The attached excel report contains a comprehensive breakdown of all tests, including booking scenarios, test cases and test statuses and other key metrics.

                For complete details, kindly review the attached excel report.

                Best regards,
                Sreenivas Muthyalapati
                QA Analyst
                Travelstart
                """, timestamp));

            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setSubject(subject);
            mail.addContent(content);

            // Add multiple recipients
            Personalization personalization = new Personalization();
            for (String recipientEmail : recipients) {
                Email to = new Email(recipientEmail);
                personalization.addTo(to);
            }
            mail.addPersonalization(personalization);

            // Attach all files in attachmentLocationPath
            for (String filePath : attachmentLocationPath) {
                File file = new File(filePath);
                if (file.exists()) {
                    Attachments attachments = new Attachments();
                    attachments.setContent(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));
                    attachments.setType(Files.probeContentType(file.toPath()));
                    attachments.setFilename(file.getName());
                    attachments.setDisposition("attachment");
                    mail.addAttachments(attachments);
                } else {
                    System.out.println("File not found: " + filePath);
                }
            }

            // Add the mail object to the request body
            request.setBody(mail.build());

            // Send the request
            Response response = sg.api(request);
            System.out.println(String.format("Status Code: %d", response.getStatusCode()));
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Response Headers: " + response.getHeaders());

            if (response.getStatusCode() == 202) {
                System.out.println("Email report has been sent");
            }
            // Update email counts
            dailyEmailCount++;
            monthlyEmailCount++;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to get the latest report file dynamically
    private File getLatestReport(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElseThrow(() -> new IOException("No report found in the specified directory"));
        }
    }

    public JSONObject reportToJsonObject(String testAutomationGroup, String testRunTimeStamp, int testScenariosCount, int testCasesCount, int passedTestCount, int failedTestCount, int skippedTestCount) {
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("testAutomationGroup", testAutomationGroup);
        jsonBody.put("testRunTimeStamp", testRunTimeStamp);
        jsonBody.put("scenarioCount", testScenariosCount);
        jsonBody.put("testCasesCount", testCasesCount);
        jsonBody.put("passedCount", passedTestCount);
        jsonBody.put("failedCount", failedTestCount);
        jsonBody.put("skippedCount", skippedTestCount);

        return jsonBody;
    }
}
