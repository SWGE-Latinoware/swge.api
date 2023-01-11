package celtab.swge.service;

import celtab.swge.exception.ServiceException;
import celtab.swge.model.File;
import celtab.swge.property.FileStorageProperties;
import celtab.swge.repository.FileRepository;
import celtab.swge.specification.GenericSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService extends GenericService<File, Long> {

    private final Path fileStorageLocation;

    @Data
    @AllArgsConstructor
    public static class FileResponse {

        private File fileInfo;

        private Resource file;
    }

    public FileService(
        FileStorageProperties fileStorageProperties,
        FileRepository fileRepository
    ) {
        super(fileRepository, "file(s)", new GenericSpecification<>(File.class));
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new ServiceException("It was not possible to create the file storage directory '" + fileStorageProperties.getUploadDir() + "'!");
        }
    }

    @Transactional
    public File saveFile(File file, InputStream blob) {
        try {
            var dbFile = repository.save(file);
            var targetLocation = fileStorageLocation.resolve(dbFile.getId().toString());
            Files.copy(blob, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return dbFile;
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the file '" + file.getId() + "':'" + file.getName() + "'!");
        }
    }

    @Transactional
    public File saveFile(File file, MultipartFile fileObj) {
        try {
            return saveFile(file, fileObj.getInputStream());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("It was not possible to save the file '" + file.getId() + "':'" + file.getName() + "'!");
        }
    }

    public Resource loadFileAsResource(Long id) {
        try {
            var filePath = fileStorageLocation.resolve(id.toString()).normalize();
            var resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new FileNotFoundException();
        } catch (Exception e) {
            throw new ServiceException("It was not possible to load the file '" + id + "'!");
        }
    }

    public FileResponse loadFileWithResource(Long id) {
        try {
            var resource = loadFileAsResource(id);
            var file = repository.findById(id).orElse(null);
            if (file == null) {
                throw new FileNotFoundException();
            }
            return new FileResponse(file, resource);
        } catch (Exception e) {
            throw new ServiceException("It was not possible to load the file '" + id + "'!");
        }
    }

    @Transactional
    public void removeFile(Long id) {
        try {
            repository.deleteById(id);
            Files.delete(fileStorageLocation.resolve(String.valueOf(id)));
        } catch (Exception e) {
            throw new ServiceException("It was not possible to delete the file '" + id + "'!");
        }
    }
}
