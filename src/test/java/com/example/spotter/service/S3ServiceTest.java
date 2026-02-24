package com.example.spotter.service;

import com.example.spotter.exception.exceptions.StorageServiceException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        String dummyPublicUrl = "{bucket}-test.com";
        ReflectionTestUtils.setField(s3Service, "publicUrl", dummyPublicUrl);
    }

    @Nested
    class uploadFileTests {
        @Test
        @DisplayName("Should upload file and return filename when file has an extension")
        public void uploadFileAndReturnFileNameWhenFileHasExtension() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "test-file",
                    "test-file.jpg",
                    "image/jpeg",
                    "test content".getBytes());

            String expectedBucketName = "test-bucket";

            String fileName = s3Service.uploadFile(mockFile, expectedBucketName);

            assertNotNull(fileName);
            assertTrue(fileName.endsWith(".jpg"));

            ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
            Mockito.verify(s3Client, Mockito.times(1)).putObject(requestCaptor.capture(), Mockito.any(RequestBody.class));
            PutObjectRequest capturedRequest = requestCaptor.getValue();

            assertEquals(expectedBucketName, capturedRequest.bucket());
            assertEquals(fileName, capturedRequest.key());
            assertEquals("image/jpeg", capturedRequest.contentType());
        }

        @Test
        @DisplayName("Should upload file and return filename when file has no extension")
        public void uploadFileAndReturnFileNameWhenFileHasNoExtension() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "test-file",
                    "test-file",
                    "",
                    "test content".getBytes());

            String expectedBucketName = "test-bucket";

            String fileName = s3Service.uploadFile(mockFile, expectedBucketName);

            assertNotNull(fileName);
            assertFalse(fileName.contains("."));

            ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
            Mockito.verify(s3Client, Mockito.times(1)).putObject(requestCaptor.capture(), Mockito.any(RequestBody.class));
            PutObjectRequest capturedRequest = requestCaptor.getValue();

            assertEquals(expectedBucketName, capturedRequest.bucket());
            assertEquals(fileName, capturedRequest.key());
            assertEquals("", capturedRequest.contentType());
        }

        @Test
        @DisplayName("Should throw StorageServiceException when S3Client throws S3Exception")
        public void uploadFileShouldThrowStorageServiceExceptionWhenS3Exception() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "test-file",
                    "test-file",
                    "",
                    "test content".getBytes());

            String expectedBucketName = "test-bucket";

            Mockito.when(s3Client.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("Test AWS error").build());

            StorageServiceException thrownException = assertThrows(StorageServiceException.class, () -> s3Service.uploadFile(mockFile, expectedBucketName));
            assertEquals("Could not upload file from S3 (AWS Error)", thrownException.getMessage());
        }

        @Test
        @DisplayName("Should throw StorageServiceException when S3Client throws SdkClientException")
        public void uploadFileShouldThrownStorageServiceExceptionWhenSdkClientException() {
            MockMultipartFile mockFile = new MockMultipartFile(
                    "test-file",
                    "test-file",
                    "",
                    "test content".getBytes());

            String expectedBucketName = "test-bucket";

            Mockito.when(s3Client.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class)))
                    .thenThrow(SdkClientException.builder().message("Test AWS error").build());

            StorageServiceException thrownException = assertThrows(StorageServiceException.class, () -> s3Service.uploadFile(mockFile, expectedBucketName));
            assertEquals("Failed to upload file from S3 (Connection Error)", thrownException.getMessage());
        }

        @Test
        @DisplayName("Should throw StorageServiceException when getInputStream throws IOException")
        public void uploadFileShouldThrownStorageServiceExceptionWhenFileGetInputStreamThrowsIOException() throws IOException {
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            String expectedBucketName = "test-bucket";
            Mockito.when(mockFile.getOriginalFilename()).thenReturn("test-name");
            Mockito.when(mockFile.getContentType()).thenReturn("test-content-type");
            Mockito.when(mockFile.getInputStream()).thenThrow(new IOException());

            StorageServiceException thrownException = assertThrows(StorageServiceException.class, () -> s3Service.uploadFile(mockFile, expectedBucketName));
            assertEquals("Error while uploading file to S3", thrownException.getMessage());
        }
    }

    @Nested
    class deleteFileTests {
        @Test
        @DisplayName("Delete file should call deleteObject with valid arguments")
        public void deleteFileShouldCallDeleteObjectWhenValidArguments() {
            String expectedBucketName = "test-bucket";
            String mockFileName = "test-file";

            s3Service.deleteFile(mockFileName, expectedBucketName);

            ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
            Mockito.verify(s3Client, Mockito.times(1)).deleteObject(requestCaptor.capture());
            DeleteObjectRequest capturedRequest = requestCaptor.getValue();

            assertEquals(expectedBucketName, capturedRequest.bucket());
            assertEquals(mockFileName, capturedRequest.key());
        }

        @Test
        @DisplayName("Delete file should throw StorageServiceException when S3Client throws S3Exception")
        public void deleteFileShouldThrowStorageServiceExceptionWhenS3Exception() {
            String expectedBucketName = "test-bucket";
            String mockFileName = "test-file";

            Mockito.when(s3Client.deleteObject(Mockito.any(DeleteObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("Test AWS error").build());

            StorageServiceException thrownError = assertThrows(StorageServiceException.class, () -> s3Service.deleteFile(mockFileName, expectedBucketName));
            assertEquals("Could not delete file from S3 (AWS Error)", thrownError.getMessage());
        }

        @Test
        @DisplayName("Delete file should throw StorageServiceException when S3Client throws SdkClientException")
        public void deleteFileShouldThrowStorageServiceExceptionWhenSdkClientException() {
            String expectedBucketName = "test-bucket";
            String mockFileName = "test-file";

            Mockito.when(s3Client.deleteObject(Mockito.any(DeleteObjectRequest.class)))
                    .thenThrow(SdkClientException.builder().message("Test AWS error").build());

            StorageServiceException thrownError = assertThrows(StorageServiceException.class, () -> s3Service.deleteFile(mockFileName, expectedBucketName));
            assertEquals("Failed to delete file from S3 (Connection Error)", thrownError.getMessage());
        }
    }
}