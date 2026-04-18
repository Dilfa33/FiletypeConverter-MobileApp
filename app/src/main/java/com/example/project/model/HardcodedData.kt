package com.example.project.model

object HardcodedData {

    val files = listOf(
        FileItem("1",  "document.docx",      "DOCX", "PDF",  2.4f,   "Apr 10, 2026", ConversionStatus.SUCCESS),
        FileItem("2",  "photo.jpg",           "JPG",  "PNG",  1.1f,   "Apr 9, 2026",  ConversionStatus.SUCCESS),
        FileItem("3",  "report.pdf",          "PDF",  "TXT",  0.3f,   "Apr 8, 2026",  ConversionStatus.FAILED),
        FileItem("4",  "presentation.pptx",   "PPTX", "PDF",  4.7f,   "Apr 7, 2026",  ConversionStatus.SUCCESS),
        FileItem("5",  "video.mp4",           "MP4",  "MP3",  120.0f, "Apr 6, 2026",  ConversionStatus.SUCCESS),
        FileItem("6",  "spreadsheet.xlsx",    "XLSX", "PDF",  0.8f,   "Apr 5, 2026",  ConversionStatus.SUCCESS),
        FileItem("7",  "image.png",           "PNG",  "JPG",  3.2f,   "Apr 4, 2026",  ConversionStatus.FAILED),
        FileItem("8",  "notes.txt",           "TXT",  "PDF",  0.1f,   "Apr 3, 2026",  ConversionStatus.SUCCESS),
        FileItem("9",  "archive.zip",         "ZIP",  "PDF",  15.6f,  "Apr 2, 2026",  ConversionStatus.PROCESSING),
        FileItem("10", "audio.mp3",           "MP3",  "WAV",  8.4f,   "Apr 1, 2026",  ConversionStatus.SUCCESS),
    )

    val supportedFormats = listOf("PDF", "DOCX", "PNG", "JPG", "TXT", "MP4", "MP3", "XLSX", "ZIP", "WAV")
}
