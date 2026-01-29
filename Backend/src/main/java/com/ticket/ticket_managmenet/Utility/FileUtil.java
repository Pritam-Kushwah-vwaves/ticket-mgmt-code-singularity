package com.ticket.ticket_managmenet.Utility;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class FileUtil {

    private static final Tika tika = new Tika();

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static String extractText(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        // 1️⃣ Detect file type
        String detectedType;
        try (InputStream is = file.getInputStream()) {
            detectedType = tika.detect(is);
        }

        if (!"application/pdf".equalsIgnoreCase(detectedType)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        // 2️⃣ Extract text
        String extractedText;
        try (InputStream is = file.getInputStream()) {
            extractedText = tika.parseToString(is);
        }

        if (extractedText == null || extractedText.isBlank()) {
            throw new RuntimeException("No readable text found in PDF");
        }

        return extractedText.trim();
    }
}




//package com.ticket.ticket_managmenet.Utility;
//
//import org.apache.tika.Tika;
//import org.springframework.web.multipart.MultipartFile;
//
//public class FileUtil {
//
//    private static final Tika tika = new Tika();
//
//    public static String extractText(MultipartFile file) throws Exception {
//
//        if (file == null || file.isEmpty()) {
//            return "";
//        }
//
//        return tika.parseToString(file.getInputStream());
//    }
//}
