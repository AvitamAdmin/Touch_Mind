package com.cheil.form;

import com.cheil.core.mongo.model.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class MediaForm extends BaseForm {
    private String mediaId;
    private String shortDescription;
    private Category category;
    private MultipartFile file;
}
