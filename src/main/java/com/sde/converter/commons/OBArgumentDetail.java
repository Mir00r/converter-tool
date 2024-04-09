package com.sde.converter.commons;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OBArgumentDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private String argument;
    private String type;
}
