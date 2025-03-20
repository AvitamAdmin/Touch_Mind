package com.touchMind.fileimport.service;

import java.io.IOException;
import java.util.Map;

public interface FileExportService {
    String exportEntity(String node, Map<String, String> headerFields) throws IOException;
}
