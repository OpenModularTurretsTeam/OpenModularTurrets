package omtteam.openmodularturrets.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigWriterTest {
    static class Sample {
        @Comment("Top level flag.")
        boolean flag = true;
        @Comment("A number.\nSecond line.") @Range(min = 1, max = 100)
        int number = 10;
        @Range(min = 0)
        int atLeast = 5;
        @Range(max = 1)
        double ratio = 0.5D;
        @Comment("Names.")
        List<String> names = new ArrayList<>(List.of("a", "b:c"));
        List<String> none = new ArrayList<>();
        transient int ignored = 3;
        @Comment("Nested group.") @SerializedName("inner_group")
        Inner inner = new Inner();
    }

    static class Inner {
        @Range(min = 0, max = 1)
        double chance = 0.25D;
    }

    @Test
    void writesCommentsRangesAndDefaults() {
        String text = ConfigWriter.write(new Sample(), new Sample());
        assertTrue(text.contains("// Top level flag.\n  // Default: true\n  \"flag\": true,"), text);
        assertTrue(text.contains("// A number.\n  // Second line.\n  // Range: 1 to 100\n  // Default: 10\n  \"number\": 10,"), text);
        assertTrue(text.contains("// Minimum: 0\n  // Default: 5\n  \"atLeast\": 5,"), text);
        assertTrue(text.contains("// Maximum: 1.0\n  // Default: 0.5\n  \"ratio\": 0.5,"), text);
        assertTrue(text.contains("// Default: [\"a\",\"b:c\"]\n  \"names\": [\n    \"a\",\n    \"b:c\"\n  ],"), text);
        assertTrue(text.contains("\"none\": [],"), text);
        assertTrue(text.contains("// Nested group.\n  \"inner_group\": {\n    // Range: 0.0 to 1.0"), text);
    }

    @Test
    void outputReadsBackAsJson() {
        Sample sample = new Sample();
        sample.number = 42;
        sample.inner.chance = 0.75D;
        JsonObject json = JsonParser.parseString(ConfigWriter.write(sample, new Sample())).getAsJsonObject();
        Sample back = new Gson().fromJson(json, Sample.class);
        assertEquals(42, back.number);
        assertEquals(0.75D, back.inner.chance);
        assertEquals(List.of("a", "b:c"), back.names);
        assertFalse(json.has("ignored"));
    }

    @Test
    void rangesClampNestedFields() {
        Sample sample = new Sample();
        sample.number = 500;
        sample.atLeast = -3;
        sample.ratio = 7;
        sample.inner.chance = -1;
        ConfigValidator validator = new ConfigValidator();
        ConfigRanges.validate(sample, validator, "");
        assertEquals(100, sample.number);
        assertEquals(0, sample.atLeast);
        assertEquals(1D, sample.ratio);
        assertEquals(0D, sample.inner.chance);
        assertTrue(validator.changed());
    }
}
