package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class TariffForm extends BaseForm {
    private MultipartFile[] files;
    private String[] checkedValue;
}
