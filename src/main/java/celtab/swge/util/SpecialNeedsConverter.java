package celtab.swge.util;

import celtab.swge.model.enums.SpecialNeedsType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class SpecialNeedsConverter implements AttributeConverter<List<SpecialNeedsType>, String> {


    @Override
    public String convertToDatabaseColumn(List<SpecialNeedsType> specialNeedsTypes) {
        try {
            return specialNeedsTypes.stream().map(specialNeedsType -> String.valueOf(specialNeedsType.ordinal())).collect(Collectors.joining(","));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SpecialNeedsType> convertToEntityAttribute(String jsonString) {
        try {
            return Arrays.stream(jsonString.split(",")).map(s -> SpecialNeedsType.values()[Integer.parseInt(s)]).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
