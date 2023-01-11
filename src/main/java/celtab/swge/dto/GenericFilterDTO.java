package celtab.swge.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GenericFilterDTO {

    private String query;

    private List<String> queryFields;

    private Map<String, List<String>> filters;
}
