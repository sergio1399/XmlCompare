package app.components.directory;

import app.components.mt.MTComparator;
import app.components.xml.XMLComparator;
import app.components.xml.XMLCompareResult;
import app.config.AppConfig;
import com.prowidesoftware.swift.model.SwiftMessage;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class CompareServiceTest {

    @Test
    public void testCompare2Xml() throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        int[] expected = {1, 2, 2, 2, 1, 1, 2, 2};
        for(int cnt = 2; cnt < 3; cnt++){
            String filename1 = "devTransformResult" + cnt + ".xml";
            String filename2 = "promTransformResult" + cnt + ".xml";
            Resource resource1 =
                    ctx.getResource("classpath:" + filename1);
            Resource resource2 =
                    ctx.getResource("classpath:" + filename2);
            if(resource1 != null && resource2 != null){
                XMLCompareResult result = XMLComparator.diff(resource1.getInputStream(), resource2.getInputStream());
                assertEquals(expected[cnt-1], result.getErrors().size());
            }
        }
    }

    @Test
    public void testCompareSwt() throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        String filename1 = "tst1.xml";
        Resource resource1 = ctx.getResource("classpath:" + filename1);
        String filename2 = "tst2.xml";
        Resource resource2 = ctx.getResource("classpath:" + filename2);
        if(resource1 != null && resource2 != null){
            XMLCompareResult result = XMLComparator.diff(resource1.getInputStream(), resource2.getInputStream());
            assertEquals(2, result.getErrors().size());
        }
    }

}
