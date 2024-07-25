package fr.carbonIT.treasurehunt.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest
class FileUploadControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FileUploadController fileUploadController;

    @Mock
    private MultipartFile file;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileUploadController).build();
    }

    @Test
     void testUploadFile_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Fichier téléchargé avec succès."));
    }

    @Test
     void testUploadFile_EmptyFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", new byte[0]);

        mockMvc.perform(multipart("/api/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le fichier est vide."));
    }

    @Test
     void testUploadFile_InvalidFileExtension() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("testfile.pdf");

        mockMvc.perform(multipart("http://localhost:8080/api/upload")
                        .file("file", "content".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le fichier doit être au format .txt."));
    }

    @Test
     void testUploadFile_Exception() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "content".getBytes()) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("Simulated IO Exception");
            }
        };

        mockMvc.perform(multipart("/api/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erreur lors du téléversement du fichier : Simulated IO Exception"));
    }
}
