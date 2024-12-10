package com.touchmind.fileimport.service;

import java.io.IOException;

public interface FileExportService {
    String exportEntity(String node) throws IOException;
}
