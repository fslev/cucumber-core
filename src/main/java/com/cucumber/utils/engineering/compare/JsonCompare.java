package com.cucumber.utils.engineering.compare;

import com.cucumber.utils.engineering.compare.comparators.json.CustomJsonComparator;
import com.cucumber.utils.engineering.compare.exceptions.CompareException;
import com.cucumber.utils.engineering.utils.JsonUtils;
import com.cucumber.utils.engineering.utils.RegexUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.skyah.comparator.CompareMode;
import ro.skyah.comparator.JSONCompare;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonCompare implements Placeholdable {
    private Logger log = LogManager.getLogger();

    private JsonNode expected;
    private JsonNode actual;
    private CustomJsonComparator comparator = new CustomJsonComparator();
    private boolean nonExtensibleObject;
    private boolean nonExtensibleArray;
    private boolean arrayStrictOrder;
    private String message;

    public JsonCompare(Object expected, Object actual) throws CompareException {
        this(null, expected, actual, false, false, false);
    }

    public JsonCompare(String message, Object expected, Object actual) throws CompareException {
        this(message, expected, actual, false, false, false);
    }

    public JsonCompare(Object expected, Object actual, boolean nonExtensibleObject, boolean nonExtensibleArray) throws CompareException {
        this(null, expected, actual, nonExtensibleObject, nonExtensibleArray, false);
    }

    public JsonCompare(String message, Object expected, Object actual, boolean nonExtensibleObject, boolean nonExtensibleArray) throws CompareException {
        this(message, expected, actual, nonExtensibleObject, nonExtensibleArray, false);
    }

    public JsonCompare(Object expected, Object actual, boolean nonExtensibleObject, boolean nonExtensibleArray, boolean arrayStrictOrder) throws CompareException {
        this(null, expected, actual, nonExtensibleObject, nonExtensibleArray, arrayStrictOrder);
    }

    public JsonCompare(String message, Object expected, Object actual, boolean nonExtensibleObject, boolean nonExtensibleArray,
                       boolean arrayStrictOrder) throws CompareException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
            this.expected = expected instanceof String ? mapper.readTree((String) expected) : mapper.convertValue(expected, JsonNode.class);
            this.actual = actual instanceof String ? mapper.readTree((String) actual) : mapper.convertValue(actual, JsonNode.class);
            if (!this.expected.getNodeType().equals(JsonNodeType.OBJECT)
                    && !this.expected.getNodeType().equals(JsonNodeType.ARRAY)
                    && !this.actual.getNodeType().equals(JsonNodeType.OBJECT)
                    && !this.actual.getNodeType().equals(JsonNodeType.ARRAY)) {
                throw new CompareException("Malformed JSON");
            }
            this.nonExtensibleObject = nonExtensibleObject;
            this.nonExtensibleArray = nonExtensibleArray;
            this.arrayStrictOrder = arrayStrictOrder;
            this.message = message;
        } catch (IOException e) {
            throw new CompareException("Malformed JSON");
        }
    }

    @Override
    public Map<String, String> compare() {
        try {
            JSONCompare.assertEquals(message, expected, actual, comparator, compareModes());
        } catch (AssertionError e) {
            if (!comparator.getGeneratedFieldProperties().isEmpty()) {
                while (true) {
                    comparator.getDepletedFieldPropertyList().add(new HashMap<>(comparator.getGeneratedFieldProperties()));
                    comparator.getGeneratedFieldProperties().clear();
                    try {
                        JSONCompare.assertEquals(message, expected, actual, comparator, compareModes());
                    } catch (AssertionError e1) {
                        if (!comparator.getGeneratedFieldProperties().isEmpty()) {
                            continue;
                        }
                        throw e1;
                    }
                    break;
                }
            } else {
                checkJsonContainsSpecialRegexCharsAndWarn(expected.toString());
                throw e;
            }
        }
        return comparator.getGeneratedProperties();
    }

    private CompareMode[] compareModes() {
        Set<CompareMode> modes = new HashSet<>();
        if (nonExtensibleObject) {
            modes.add(CompareMode.JSON_OBJECT_NON_EXTENSIBLE);
        }
        if (nonExtensibleArray) {
            modes.add(CompareMode.JSON_ARRAY_NON_EXTENSIBLE);
        }
        if (arrayStrictOrder) {
            modes.add(CompareMode.JSON_ARRAY_STRICT_ORDER);
        }
        return modes.toArray(new CompareMode[0]);
    }

    private void checkJsonContainsSpecialRegexCharsAndWarn(String json) {
        try {
            Map<String, List<String>> specialRegexChars = JsonUtils.walkJsonAndProcessNodes(json, nodeValue -> {
                List<String> regexChars = RegexUtils.getRegexCharsFromString(nodeValue);
                return regexChars.isEmpty() ? null : regexChars;
            });
            if (!specialRegexChars.isEmpty()) {
                String prettyResult = specialRegexChars.entrySet().stream().map(e -> e.getKey() + " contains: " + e.getValue().toString())
                        .collect(Collectors.joining("\n"));
                log.debug(" \n\n Comparison mechanism failed while comparing JSONs." +
                                " \n One reason for this, might be that Json may have unintentional regex special characters. " +
                                "\n If so, try to quote them by using \\Q and \\E or simply \\" +
                                "\n Found the following list of special regex characters inside expected:\n\n{}\n\nExpected:\n{}\n",
                        prettyResult, expected);
            }
        } catch (Exception e) {
            log.debug("Cannot extract special regex characters from json", e);
        }
    }
}
