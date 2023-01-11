package celtab.swge.specification;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.model.BasicModel;
import celtab.swge.model.enums.QueryType;
import celtab.swge.model.enums.SpecificationRule;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static celtab.swge.model.enums.SpecificationRule.*;

@Getter
public class GenericSpecification<T> {

    protected final Map<String, SpecificationRule> filterRules;

    protected final Map<String, SpecificationRule> queryRules;

    protected final Class<T> model;

    private List<Field> getAllFields(Class<?> klass, List<Field> fields) {
        var allFields = new ArrayList<Field>();
        allFields.addAll(fields);
        allFields.addAll(Arrays.asList(klass.getDeclaredFields()));
        if (klass.getSuperclass() != null && klass != BasicModel.class) {
            return getAllFields(klass.getSuperclass(), allFields);
        }
        return allFields;
    }

    private List<Field> getAllFields(Class<?> klass) {
        return getAllFields(klass, List.of());
    }

    public GenericSpecification(Class<T> model) {
        var fields = getAllFields(model);
        final var filterRulesAux = new HashMap<String, SpecificationRule>();
        final var queryRulesAux = new HashMap<String, SpecificationRule>();
        fields.forEach(field -> {
            var name = field.getName();
            var type = field.getType();
            switch (type.getSimpleName()) {
                case "String":
                    filterRulesAux.put(name, STRING_EQUAL);
                    queryRulesAux.put(name, STRING_LIKE);
                    break;
                case "Boolean":
                    filterRulesAux.put(name, BOOLEAN_EQUAL);
                    queryRulesAux.put(name, BOOLEAN_EQUAL);
                    break;
                default:
                    if (type.isEnum()) {
                        filterRulesAux.put(name, ENUM_EQUAL);
                        queryRulesAux.put(name, ENUM_EQUAL);
                    } else if (Number.class.isAssignableFrom(type)) {
                        filterRulesAux.put(name, NUMBER_EQUAL);
                        queryRulesAux.put(name, NUMBER_EQUAL);
                    }
            }
        });
        this.model = model;
        this.filterRules = filterRulesAux;
        this.queryRules = queryRulesAux;
    }

    public GenericSpecification(Class<T> model, Map<String, SpecificationRule> filterRules, Map<String, SpecificationRule> queryRules) {
        this(model);
        this.filterRules.putAll(filterRules);
        this.queryRules.putAll(queryRules);
    }

    @SuppressWarnings("unchecked")
    public <G> G castValue(String value, Class<G> type) throws ClassCastException {
        G nv;
        switch (type.getSimpleName()) {
            case "String":
                nv = (G) value;
                break;
            case "Number":
                nv = (G) (Double) Double.parseDouble(value);
                break;
            case "Boolean":
                switch (value.toLowerCase()) {
                    case "true":
                        nv = (G) Boolean.TRUE;
                        break;
                    case "false":
                        nv = (G) Boolean.FALSE;
                        break;
                    default:
                        nv = null;
                }
                break;
            default:
                nv = null;
                break;
        }
        if (nv != null) {
            return nv;
        }
        throw new ClassCastException();
    }

    public <G> G castValueOrDefault(String value, Class<G> type, G defaultValue) {
        try {
            return castValue(value, type);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public <G> List<G> castList(List<String> list, Class<G> type, G defaultValue) {
        try {
            return list.stream().map(value -> castValueOrDefault(value, type, defaultValue)).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Predicate getPredicateForBasicRule(CriteriaBuilder criteriaBuilder, Root root, SpecificationRule rule, Object field, String value) {
        try {
            Path path;
            if (String.class.isAssignableFrom(field.getClass())) {
                path = root.get((String) field);
            } else {
                path = (Path) field;
            }
            switch (rule) {
                case STRING_LIKE:
                    return criteriaBuilder.like(
                        criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%"
                    );
                case STRING_EQUAL:
                    return criteriaBuilder.equal(
                        criteriaBuilder.lower(path), value.toLowerCase()
                    );
                case ENUM_EQUAL:
                case NUMBER_EQUAL:
                    var valueNumberAux = castValueOrDefault(value, Number.class, null);
                    if (valueNumberAux == null) return null;
                    return criteriaBuilder.equal(
                        criteriaBuilder.treat(path, Double.class), valueNumberAux.doubleValue()
                    );
                case BOOLEAN_EQUAL:
                    var valueBooleanAux = castValueOrDefault(value, Boolean.class, null);
                    if (valueBooleanAux == null) return null;
                    return criteriaBuilder.equal(path, valueBooleanAux);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> getByUnified(String field, String value, SpecificationRule rule, QueryType queryType) {
        if (value == null || rule == null || field == null) return null;
        try {
            switch (rule) {
                case STRING_LIKE:
                case STRING_EQUAL:
                case ENUM_EQUAL:
                case NUMBER_EQUAL:
                case BOOLEAN_EQUAL:
                    return (root, query, criteriaBuilder) -> getPredicateForBasicRule(criteriaBuilder, root, rule, field, value);
                case COMPOSED:
                    return (root, query, criteriaBuilder) -> {
                        SpecificationRule effectRule;
                        var fields = Arrays.asList(field.split("\\."));
                        final var path = new Path[]{root.get(fields.get(0))};
                        fields
                            .subList(1, fields.size() - 1)
                            .forEach(f -> path[0] = path[0].get(f));
                        var aux = path[0].getJavaType();
                        var auxSpec = new GenericSpecification<>(aux);
                        switch (queryType) {
                            case QUERY:
                                effectRule = (SpecificationRule) auxSpec.getQueryRules().get(fields.get(fields.size() - 1));
                                break;
                            case FILTER:
                                effectRule = (SpecificationRule) auxSpec.getFilterRules().get(fields.get(fields.size() - 1));
                                break;
                            default:
                                effectRule = null;
                                break;
                        }
                        if (effectRule == null) return null;
                        var effectPath = path[0].get(fields.get(fields.size() - 1));
                        return getPredicateForBasicRule(criteriaBuilder, root, effectRule, effectPath, value);
                    };
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Specification<T> getByUnifiedList(String field, List<String> values, SpecificationRule rule, QueryType queryType) {
        if (values == null) return null;
        return values.stream()
            .map(value -> getByUnified(field, value, rule, queryType))
            .reduce(Specification::or)
            .orElse(null);
    }

    public Specification<T> getByFiltersSpec(GenericFilterDTO genericFilterDTO) {
        if (genericFilterDTO.getFilters() == null || genericFilterDTO.getFilters().isEmpty()) return null;
        return genericFilterDTO.getFilters().entrySet().stream().map(entry ->
            applyFilter(entry.getKey(), entry.getValue())
        ).reduce(Specification::and).orElse(null);
    }

    protected Specification<T> applyFilter(String field, List<String> values) {
        var rule = getRule(QueryType.FILTER, field);
        if (rule == null) return null;
        return getByUnifiedList(field, values, rule, QueryType.FILTER);
    }

    public Specification<T> getByQuerySpec(GenericFilterDTO genericFilterDTO) {
        if (genericFilterDTO.getQuery() == null || genericFilterDTO.getQueryFields() == null || genericFilterDTO.getQueryFields().isEmpty())
            return null;
        return genericFilterDTO.getQueryFields().stream().map(field ->
            applyQuery(field, genericFilterDTO.getQuery())
        ).reduce(Specification::or).orElse(null);
    }

    protected Specification<T> applyQuery(String field, String value) {
        var rule = getRule(QueryType.QUERY, field);
        if (rule == null) return null;
        return getByUnified(field, value, rule, QueryType.QUERY);
    }

    public Specification<T> getByQueryAndFilters(GenericFilterDTO genericFilterDTO) {
        return Specification
            .where(getByFiltersSpec(genericFilterDTO))
            .and(getByQuerySpec(genericFilterDTO));
    }

    private SpecificationRule getRule(QueryType queryType, String field) {
        var rule = queryType == QueryType.QUERY ? queryRules.get(field) : filterRules.get(field);
        if (rule == null) {
            if (!field.contains(".")) {
                return null;
            }
            return COMPOSED;
        }
        return rule;
    }

}
