package com.sde.converter.commons;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OBPaging implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer startIndex;
    private Integer maxPerPage;
    private Integer totalRecords;
    private String moreRecordsIndicator;
}
