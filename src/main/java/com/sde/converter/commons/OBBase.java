package com.sde.converter.commons;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OBBase implements Serializable {

    private static final long serialVersionUID = 1L;
    private OBHeader obHeader = new OBHeader();
    private OBPaging obPaging;
}
