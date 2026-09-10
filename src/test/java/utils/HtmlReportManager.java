package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class HtmlReportManager {

    private static final String REPORT_PATH = "Test Failure Report.html";

    /**
     * Initializes the HTML report structure only if it doesn't already exist.
     * Prevents the second running framework from erasing the first framework's results.
     */
    public static synchronized void initReport() {
        File reportFile = new File(REPORT_PATH);
        if (!reportFile.exists()) {
            try (FileWriter writer = new FileWriter(reportFile, false)) {
                writer.write("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<title>Test Failure Execution Report</title>\n" +
                        "<style>\n" +
                        "  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0d1117; color: #c9d1d9; padding: 28px; margin: 0; }\n" +
                        "  .header { border-bottom: 1px solid #30363d; padding-bottom: 16px; margin-bottom: 24px; max-width: 1200px; margin-left: auto; margin-right: auto; }\n" +
                        "  .header h1 { font-size: 20px; color: #f0f6fc; margin: 0; font-weight: 500; }\n" +
                        "  .container { display: flex; flex-direction: column; gap: 20px; max-width: 1200px; margin: 0 auto; }\n" +
                        "  .failure-card { background-color: #161b22; border: 1px solid #30363d; border-left: 4px solid #58a6ff; border-radius: 8px; padding: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.25); }\n" +
                        "  .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }\n" +
                        "  .test-title { font-size: 17px; font-weight: 600; color: #f0f6fc; }\n" +
                        "  .test-badge { background: #21262d; border: 1px solid #30363d; color: #8b949e; padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }\n" +
                        "  .error-box { background: #0d1117; border: 1px solid #30363d; border-radius: 6px; padding: 14px; margin-bottom: 16px; color: #e6edf3; font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, monospace; font-size: 13px; line-height: 1.5; white-space: pre-wrap; word-break: break-word; }\n" +
                        "  .screenshot-container { margin-top: 12px; }\n" +
                        "  .screenshot-title { font-size: 12px; color: #8b949e; margin-bottom: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }\n" +
                        "  .screenshot-preview { width: 100%; max-height: 500px; object-fit: contain; border-radius: 6px; border: 1px solid #30363d; background: #010409; display: block; }\n" +
                        "</style>\n</head>\n<body>\n" +
                        "<div class=\"header\"><h1>📋 Test Failure Log</h1></div>\n" +
                        "<div class=\"container\">\n" +
                        "<!-- FAILURE_CARDS_PLACEHOLDER -->\n" +
                        "</div>\n</body>\n</html>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void logFailureCard(String framework, String cleanTestName, Throwable exception, String screenshotPath, long durationMs) {
        initReport();

        String cleanErrorMessage = parseAssertionDetails(exception);
        String imageSrc = convertImageToBase64Src(screenshotPath);

        String cardHtml = String.format(
                "  <div class=\"failure-card\">\n" +
                        "    <div class=\"card-header\">\n" +
                        "      <div class=\"test-title\">%s</div>\n" +
                        "      <span class=\"test-badge\">%s</span>\n" +
                        "    </div>\n" +
                        "    <div class=\"error-box\"><b>Assertion Details:</b>\n%s</div>\n" +
                        "    <div class=\"screenshot-container\">\n" +
                        "      <div class=\"screenshot-title\">Captured Page State</div>\n" +
                        "      <img class=\"screenshot-preview\" src=\"%s\" alt=\"Failure Screenshot Preview\">\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "<!-- FAILURE_CARDS_PLACEHOLDER -->",
                cleanTestName, framework, escapeHtml(cleanErrorMessage), imageSrc
        );

        try {
            String content = new String(Files.readAllBytes(new File(REPORT_PATH).toPath()));
            content = content.replace("<!-- FAILURE_CARDS_PLACEHOLDER -->", cardHtml);
            Files.write(new File(REPORT_PATH).toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseAssertionDetails(Throwable exception) {
        if (exception == null) return "No failure exception detailed.";
        String fullMessage = exception.getMessage() != null ? exception.getMessage() : exception.toString();

        fullMessage = fullMessage.replaceAll("For documentation on this error, please visit:.*", "");
        fullMessage = fullMessage.replaceAll("Build info:.*", "");
        fullMessage = fullMessage.replaceAll("System info:.*", "");
        fullMessage = fullMessage.replaceAll("Driver info:.*", "");

        return fullMessage.trim();
    }

    private static String convertImageToBase64Src(String filePath) {
        File imgFile = new File(filePath);
        if (!imgFile.exists()) {
            return "";
        }
        try {
            byte[] fileContent = Files.readAllBytes(imgFile.toPath());
            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            return "";
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}