package app.components.xml;

/**
 * Документ или пачка документов. Представляет собой документ произвольного формата и содержания.
 */
public class DocumentSource {

    private byte[] stream;

    public DocumentSource(String body) {
        try{
            this.stream = body.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public byte[] getStream(){
        return stream;
    }

    public String getString(){
        return new String(stream);
    }
}
