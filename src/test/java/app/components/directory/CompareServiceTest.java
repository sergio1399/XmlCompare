package app.components.directory;

import app.components.xml.XMLComparator;
import app.components.xml.XMLCompareResult;
import app.config.AppConfig;
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
        int[] expected = {1, 1, 2, 2, 1, 1, 2, 2};
        for(int cnt = 1; cnt < 9; cnt++){
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

}
