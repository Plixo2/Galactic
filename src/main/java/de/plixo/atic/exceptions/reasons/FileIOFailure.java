package de.plixo.atic.exceptions.reasons;

import lombok.Getter;

import java.io.File;

public final class FileIOFailure extends Failure {

    @Getter
    private final FileType fileType;

    public FileIOFailure(File failedFile, FileType fileType) {
        setFile(failedFile);
        this.fileType = fileType;
    }

    public enum FileType {
        PACKAGE,
        UNIT,
        ROOT,
        TEMP_FILES
    }
}
