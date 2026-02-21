package br.com.matteusmoreno.application.service;

import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@ApplicationScoped
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @ConfigProperty(name = "cloudinary.cloud-name") String cloudName,
            @ConfigProperty(name = "cloudinary.api-key") String apiKey,
            @ConfigProperty(name = "cloudinary.api-secret") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String upload(byte[] fileBytes, String publicId, CloudinaryFolder folder) {
        try {
            log.info("Uploading file to Cloudinary: folder={}, publicId={}", folder.getPath(), publicId);

            Map result = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder.getPath(),
                    "overwrite", true,
                    "resource_type", "auto"
            ));

            String url = (String) result.get("secure_url");
            log.info("File uploaded successfully: {}", url);
            return url;

        } catch (Exception e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    public void delete(String publicId) {
        try {
            log.info("Deleting file from Cloudinary: publicId={}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("File deleted from Cloudinary: publicId={}", publicId);
        } catch (Exception e) {
            log.error("Error deleting file from Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }

    public String extractPublicId(String url) {
        if (url == null || url.isBlank()) return null;
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) return null;
        String afterUpload = url.substring(uploadIndex + 8);
        int versionSlash = afterUpload.indexOf("/");
        if (afterUpload.startsWith("v") && versionSlash != -1) {
            afterUpload = afterUpload.substring(versionSlash + 1);
        }
        int dotIndex = afterUpload.lastIndexOf(".");
        return dotIndex != -1 ? afterUpload.substring(0, dotIndex) : afterUpload;
    }
}

