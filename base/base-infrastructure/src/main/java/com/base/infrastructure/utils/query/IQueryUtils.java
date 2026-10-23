package com.base.infrastructure.utils.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public interface IQueryUtils {

    String SELECT = "SELECT";
    String FROM = "FROM";
    String WHERE = "WHERE";
    String AND = "AND";
    String OR = "OR";
    String AND_PATERN = "And";
    String LIKE = "like";
    String TRUE = "true";
    String FALSE = "false";

    String CONDITION_SUPERIOR = ">";
    String CONDITION_SUPERIOR_EGAL = ">=";
    String CONDITION_INFERIOR = "<";
    String CONDITION_INFERIOR_EGAL = "<=";
    String CONDITION_DIFFERENT = "!=";
    String CONDITION_LIKE = "%";
    String CONDITION_IS_NOT = "!~";
    String CONDITION_IS = "~";
    String CONDITION_EGAL = "=";
    String CONDITION_BRACKET_OPEN = "(";
    String CONDITION_DOUBLE_BRACKET_OPEN = "((";
    String CONDITION_BRACKET_CLOSE = ")";
    String CONDITION_DOUBLE_BRACKET_CLOSE = "))";
    String CONDITATION_OR = "|";
    String FORCE_UPPER = "#";

    default List<QueryUtilsDto> initQueryDto(StringBuilder endQuery, String fields, Object... values) {
        var queryDtos = new LinkedList<QueryUtilsDto>();
        var fieldsDecompose = fields;
        var split = fieldsDecompose.split("<<");
        if (split.length > 1) {
            fieldsDecompose = split[0];
            endQuery.append(" group by ").append("" + split[1]);
        }
        split = fieldsDecompose.split(">>");
        if (split.length > 1) {
            fieldsDecompose = split[0];
            var order = split[1].endsWith("Desc") ? split[1].replace("Desc", " desc") : split[1];
            endQuery.append(" order by ").append("" + order);
        }
        var tokens = Arrays.asList(fieldsDecompose.split(AND_PATERN));
        int[] index = { 0 };
        tokens.forEach(token -> {
            if (StringUtils.isNotBlank(token) && values.length > index[0]) {
                queryDtos.add(initQueryDto(index, token, values));
            }
            index[0]++;
        });
        return queryDtos;
    }

    private QueryUtilsDto initQueryDto(int[] index, String token, Object... values) {
        var query = new QueryUtilsDto();
        query.setIndice(index[0]);
        query.setField(getFieldName(token));
        query.setCondition(getFieldCondition(token));
        query.setLike(isLike(token));
        query.setBracketOpen(isBracketOpen(token));
        query.setDoubleBracketOpen(isDoubleBracketOpen(token));
        query.setBracketClose(isBracketClose(token));
        query.setDoubleBracketClose(isDoubleBracketClose(token));
        query.setOr(isOr(token));
        var newValue = values[index[0]];
        if (token.endsWith(FORCE_UPPER)) {
            query.setUpper(true);
        } else if (NumberUtils.isCreatable(newValue.toString())) {
            try {
                newValue = Long.parseLong(newValue.toString());
            } catch (NumberFormatException _) {
                newValue = Float.parseFloat(newValue.toString());
            }
        } else if (TRUE.equals(newValue) || (FALSE).equals(newValue)) {
            newValue = TRUE.equals(newValue);
        } else if (!NumberUtils.isParsable(newValue.toString())) {
            query.setUpper(true);
        }
        query.setValue(newValue);
        return query;
    }

    default String getSqlQuery(List<QueryUtilsDto> queryDtos, boolean actived, StringBuilder endQuery) {
        var requete = new StringBuilder();
        queryDtos.forEach(queryDto -> constructRequest(requete, queryDto));
        if (actived) {
            requete.append(" and e.active = true");
        }
        requete.append(endQuery.toString());
        return requete.toString();
    }

    private void constructRequest(StringBuilder requete, QueryUtilsDto queryDto) {
        var andOr = BooleanUtils.isTrue(queryDto.getOr()) ? OR : AND;
        requete.append(" ").append(requete.toString().contains(WHERE) ? andOr : WHERE);
        if (BooleanUtils.isTrue(queryDto.getDoubleBracketOpen())) {
            requete.append(" ").append(CONDITION_DOUBLE_BRACKET_OPEN);
        } else if (BooleanUtils.isTrue(queryDto.getBracketOpen())) {
            requete.append(" ").append(CONDITION_BRACKET_OPEN);
        }
        if (CONDITION_IS_NOT.equals(queryDto.getCondition())) {
            requete.append(" ").append(queryDto.getField()).append(" is not true");
        } else if (CONDITION_IS.equals(queryDto.getCondition())) {
            requete.append(" ").append(queryDto.getField()).append(" is null");
        } else if (BooleanUtils.isTrue(queryDto.getLike())) {
            requete.append(" UPPER(").append(queryDto.getField()).append(") ").append(queryDto.getCondition())
                    .append(" UPPER(?").append(queryDto.getIndice() + 1).append(")");
        } else if (BooleanUtils.isTrue(queryDto.getUpper())) {
            requete.append(" UPPER(").append(queryDto.getField()).append(") ").append(queryDto.getCondition())
                    .append(" UPPER(?").append(queryDto.getIndice() + 1).append(")");
        } else {
            requete.append(" ").append(queryDto.getField()).append(" ").append(queryDto.getCondition())
                    .append(" ?").append(queryDto.getIndice() + 1);
        }
        if (BooleanUtils.isTrue(queryDto.getDoubleBracketClose())) {
            requete.append(" ").append(CONDITION_DOUBLE_BRACKET_CLOSE);
        } else if (BooleanUtils.isTrue(queryDto.getBracketClose())) {
            requete.append(" ").append(CONDITION_BRACKET_CLOSE);
        }
    }

    default String getFieldName(String fieldName) {
        return StringUtils.uncapitalize(fieldName.replace(CONDITION_SUPERIOR_EGAL, "")
                .replace(CONDITION_INFERIOR_EGAL, "").replace(CONDITION_SUPERIOR, "")
                .replace(CONDITION_INFERIOR, "").replace(CONDITION_DIFFERENT, "").replace(CONDITION_LIKE, "")
                .replace(CONDITION_IS_NOT, "").replace(CONDITION_IS, "").replace(CONDITION_EGAL, "")
                .replace(CONDITION_BRACKET_OPEN, "").replace(CONDITION_BRACKET_CLOSE, "")
                .replace(CONDITION_DOUBLE_BRACKET_OPEN, "").replace(CONDITION_DOUBLE_BRACKET_CLOSE, "")
                .replace(CONDITATION_OR, "").replace(" asc", "").replace(" desc", "")
                .replace(FORCE_UPPER, ""));
    }

    default String getFieldCondition(String fieldName) {
        if (fieldName.contains(CONDITION_SUPERIOR_EGAL)) {
            return CONDITION_SUPERIOR_EGAL;
        } else if (fieldName.contains(CONDITION_INFERIOR_EGAL)) {
            return CONDITION_INFERIOR_EGAL;
        } else if (fieldName.contains(CONDITION_SUPERIOR)) {
            return CONDITION_SUPERIOR;
        } else if (fieldName.contains(CONDITION_INFERIOR)) {
            return CONDITION_INFERIOR;
        } else if (fieldName.contains(CONDITION_DIFFERENT)) {
            return CONDITION_DIFFERENT;
        } else if (fieldName.contains(CONDITION_LIKE)) {
            return LIKE;
        } else if (fieldName.contains(CONDITION_IS_NOT)) {
            return CONDITION_IS_NOT;
        } else if (fieldName.contains(CONDITION_IS)) {
            return CONDITION_IS;
        }
        return CONDITION_EGAL;
    }

    default Boolean isBracketOpen(String fieldName) {
        return fieldName.contains(CONDITION_BRACKET_OPEN)
                && !fieldName.contains(CONDITION_DOUBLE_BRACKET_OPEN);
    }

    default Boolean isDoubleBracketOpen(String fieldName) {
        return fieldName.contains(CONDITION_DOUBLE_BRACKET_OPEN);
    }

    default Boolean isBracketClose(String fieldName) {
        return fieldName.contains(CONDITION_BRACKET_CLOSE)
                && !fieldName.contains(CONDITION_DOUBLE_BRACKET_CLOSE);
    }

    default Boolean isDoubleBracketClose(String fieldName) {
        return fieldName.contains(CONDITION_DOUBLE_BRACKET_CLOSE);
    }

    default Boolean isLike(String fieldName) {
        return fieldName.contains(CONDITION_LIKE);
    }

    default Boolean isOr(String fieldName) {
        return fieldName.contains(CONDITATION_OR);
    }
}