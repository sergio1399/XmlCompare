package app.components.mt;

import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.utils.SwiftMessageComparator;
import com.prowidesoftware.swift.io.ConversionService;
import com.prowidesoftware.swift.io.parser.*;
import org.w3c.dom.Document;

import java.io.*;

public class MTComparator {

    public static void diff(){

    }

   /* public static String swtToXml(InputStream is, String encoding){
        String xml = null;
        String swt = null;
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(is, encoding )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            swt = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConversionService conversionService = new ConversionService();
        xml = conversionService.getXml(swt);
        return xml;
    }*/
/*
    public static Document getDoc(String source){

    }

    public static MTCompareResult diff(SwiftMessage mes1, SwiftMessage mes2){
        MTCompareResult result = new MTCompareResult();
        SwiftMessageComparator comparator = new SwiftMessageComparator();


        return result;
    }*/


}
