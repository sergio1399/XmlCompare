package app.components.directory;

import app.components.mt.MTComparator;
import app.components.xml.XMLComparator;
import app.components.xml.XMLCompareResult;
import app.config.AppConfig;
import com.prowidesoftware.swift.model.SwiftMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = {AppConfig.class})
public class CompareServiceTest {

    private String controlXml;
    private String testXml;
    private int numErrorsExpected;

    public CompareServiceTest(String controlXml, String testXml, int numErrorsExpected) {
        this.controlXml = controlXml;
        this.testXml = testXml;
        this.numErrorsExpected = numErrorsExpected;
    }

    @Parameterized.Parameters(name = "Control:{0}, test:{1}, errors:{2}")
    public static Iterable<Object[]> dataForTest() {
        return Arrays.asList(new Object[][]{
                {"promTransformResult1.xml", "devTransformResult1.xml", 1},
                {"promTransformResult2.xml", "devTransformResult2.xml", 2},
                {"promTransformResult3.xml", "devTransformResult3.xml", 2},
                {"promTransformResult4.xml", "devTransformResult4.xml", 2},
                {"promTransformResult5.xml", "devTransformResult5.xml", 1},
                {"promTransformResult6.xml", "devTransformResult6.xml", 1},
                {"promTransformResult7.xml", "devTransformResult7.xml", 2},
                {"promTransformResult8.xml", "devTransformResult8.xml", 2}
        });
    }


    @Test
    public void testCompare2Xml() throws IOException{
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        Resource resource1 =
                ctx.getResource("classpath:" + controlXml);
        Resource resource2 =
                ctx.getResource("classpath:" + testXml);
        if(resource1 != null && resource2 != null) {
            XMLCompareResult result = XMLComparator.diff(resource1.getInputStream(), resource2.getInputStream());
            assertEquals(numErrorsExpected, result.getErrors().size());
        }
    }


}
