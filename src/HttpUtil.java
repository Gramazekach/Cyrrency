import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class HttpUtil {

    public static final String DATE_FORMATTER = "d.MM.yyyy";

    public static void main(String[] args) {
        String enteredDate = enterDate();
        String url = "http://www.cbr.ru/scripts/XML_daily.asp" + enteredDate.toString();
        System.out.print(url);
        String result = HttpUtil.sendRequest(url, null, null);
        System.out.println("Result: " + result);
    }

    private static String enterDate() {

        System.out.println("Enter date in dd.MM.yyyy format:");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        String name = scanner.next();
        while (scanner.hasNext()) {
            String date = scanner.nextLine();
            if (date.isEmpty()) {
                continue;
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                LocalDate parse = LocalDate.parse(date, formatter);
                return parse.format(formatter);
            } catch (Exception ex) {
                System.out.println("Invalid date format");
                return enterDate();
            }
        }
        System.out.println("Please enter date.");
        return enterDate();
    }

    /**
     * @param url     - required
     * @param headers - nullable
     * @param request - nullable
     */
    public static String sendRequest(String url, Map<String, String> headers, String request) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(20000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");

            if (request != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(request);
                outputStream.flush();
                outputStream.close();
            }

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int status = urlConnection.getResponseCode();
            System.out.println("status code:" + status);

            if (status == HttpURLConnection.HTTP_OK) {
                result = getStringFromStream(urlConnection.getInputStream());
            }
        } catch (Exception e) {
            System.out.println("sendRequest failed");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private static String getStringFromStream(InputStream inputStream) throws IOException {
        final int BUFFER_SIZE = 4096;
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            resultStream.write(buffer, 0, length);
        }
        return resultStream.toString("UTF-8");
    }

}