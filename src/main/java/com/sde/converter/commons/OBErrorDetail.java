package com.sde.converter.commons;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OBErrorDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private String type;
    private String[] arguments;
    private String fieldId;
    private List<OBArgumentDetail> argumentDetails;
}
