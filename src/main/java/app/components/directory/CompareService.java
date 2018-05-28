package app.components.directory;

import app.components.dao.XmlErrorDAO;
import app.components.model.XMLError;
import app.components.xml.XMLComparator;
import app.components.xml.XMLCompareResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class CompareService {

    @Autowired
    private Environment env;

    @Autowired
    XmlErrorDAO dao;

    public void execComparation(){
        System.out.println(env.getProperty("watch_directory.path"));
        File folder = new File(env.getProperty("watch_directory.path"));
        if(!folder.exists())
            folder.mkdirs();
        File[] files = folder.listFiles();
        for (File file : files) {
            if( file.getName().endsWith(".zip") )
            {
                String unzipFolder = unzip(file);
                XMLCompareResult result = compareXmls(unzipFolder);
                setSourceForAll(result, file.getName());
                saveResult(result);
            }
        }

    }

    public void execOneXml(String filename){
        File folder = new File(env.getProperty("watch_directory.path"));
        File [] files = folder.listFiles();
        for (File file : files) {
            if( file.getName().equals(filename) )
            {
                String unzipFolder = unzip(file);
                XMLCompareResult result = compareXmls(unzipFolder);
                setSourceForAll(result, file.getName());
                saveResult(result);
            }
        }
    }

    private String unzip(File file){
        String destDir = env.getProperty("watch_directory.path") + "/" +
                file.getName().substring(0, file.getName().lastIndexOf("."));
        File dir = new File( destDir );
        if(!dir.exists())
            dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destDir;
    }

    private XMLCompareResult compareXmls(String path){
        File folder = new File(path);
        File[] files = folder.listFiles();
        List<File> xmls = new ArrayList<>();
        for (File file : files) {
            if(file.getName().endsWith(".xml") || file.getName().endsWith(".XML")){
                xmls.add(file);
            }
        }
        //обязательно должно быть ровно два файла xml!
        if( xmls.size() != 2)
            return null;
        XMLCompareResult result = null;
        try {
            result = XMLComparator.diff(new FileInputStream(xmls.get(0)), new FileInputStream(xmls.get(1)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setSourceForAll(XMLCompareResult result, String source){
        for (XMLError error : result.getErrors()) {
            error.setSource(source);
        }
        for (XMLError warning : result.getWarnings()) {
            warning.setSource(source);
        }
    }

    @Transactional
    public void saveResult(XMLCompareResult result) {
        for (XMLError error : result.getErrors()) {
            dao.saveError(error);
        }
        for (XMLError warning : result.getWarnings()) {
            dao.saveError(warning);
        }
    }

}
