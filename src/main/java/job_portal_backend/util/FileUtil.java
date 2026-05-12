package job_portal_backend.util;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {
    public String writeMediaFile(MultipartFile multipartFile, String absolutePath, String relativePath)
            throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) return null;

        String fileName = multipartFile.getOriginalFilename();
        // Replace spaces with underscores and add timestamp to prevent duplicates
        String cleanName = (fileName == null ? "file" : fileName.replaceAll(" ", "_"));
        String finalName = System.currentTimeMillis() + "_" + cleanName;

        File directory = new File(absolutePath);
        if (!directory.exists()) directory.mkdirs();

        File dest = new File(absolutePath, finalName);
        FileUtils.writeByteArrayToFile(dest, multipartFile.getBytes());

        return relativePath + finalName;
    }
}