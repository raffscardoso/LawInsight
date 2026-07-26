package com.raffs.LawInsight.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfExtractionService {

    public String extractText(byte[] content) {
        try (var pdf = Loader.loadPDF(content)) {
            var stripper = new PDFTextStripper();
            return stripper.getText(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF", e);
        }
    }
}
