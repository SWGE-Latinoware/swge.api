package celtab.swge.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URIObject {

    private String url;

    private Map<String, List<String>> attributes;

}
