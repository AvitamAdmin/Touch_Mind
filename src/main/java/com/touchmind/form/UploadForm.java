package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UploadForm extends BaseForm {
    private MultipartFile file;
    private MultipartFile file2;
    private String[] checkedValue;
    private String apiUrl;
}
