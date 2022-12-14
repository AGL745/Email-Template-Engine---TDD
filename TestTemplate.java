import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

class Template {

    private Map<String, String> variables;
    private String templateText;

    public Template(String templateText) {
        this.variables = new HashMap<String, String>();
        this.templateText = templateText;
    }

    public void set(String name, String value) {
        this.variables.put(name, value);
    }

    public String evaluate() {
        String result = replaceVariables();
        
        checkForMissingValues(result);
        return result;
    }

    private String replaceVariables() {
        String result = templateText;

        for(Entry<String, String> entry : variables.entrySet()) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            result = result.replaceAll(regex, entry.getValue());
        }
        return result;
    }

    private void checkForMissingValues(String result) { 
        Matcher m = Pattern.compile("\\$\\{.+\\}").matcher(result);

        if(m.find()) {
            throw new MissingValueException("No value for " + m.group());
        }
    }

}

class MissingValueException extends RuntimeException{

    public MissingValueException(String message) {
        super(message);
    }
    
}

public class TestTemplate {
    private Template template;

    @Before
    public void setUp() throws Exception {
        template = new Template("${one}, ${two}, ${three}");
        template.set("one", "1");
        template.set("two", "2");
        template.set("three", "3");
    }

    @Test
    public void multipleVariables() throws Exception {
        assertTemplateEvaluatesTo("1, 2, 3");
        
    }

    private void assertTemplateEvaluatesTo(String expected) {
        assertEquals(expected, template.evaluate());
    }

    @Test
    public void missingValueRaisesException() throws Exception {
        try {
            new Template("${foo}").evaluate();
            fail("evaluate() should throw an exception if "
                    + "a variable was left without a value.");
        } catch (MissingValueException expected) {
            assertEquals("No value for ${foo}", expected.getMessage());        
        }
    }
    
}