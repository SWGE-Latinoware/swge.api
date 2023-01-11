package celtab.swge.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter(autoApply = true)
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> jsonMap) {
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> convertToEntityAttribute(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
