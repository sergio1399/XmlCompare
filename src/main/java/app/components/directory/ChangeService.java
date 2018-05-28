package app.components.directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

import static com.sun.jmx.mbeanserver.Util.cast;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Service
public class ChangeService {

    @Autowired
    private Environment env;

    @Autowired
    private CompareService compareService;

    public void watchNewXml() throws InterruptedException {

        WatchService watchService
                = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Path path = Paths.get(env.getProperty("watch_directory.path"));

        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        WatchKey key;
        while ((key = watchService.take()) != null) {

            for (WatchEvent<?> event : key.pollEvents()) {

                if (event.kind() == ENTRY_CREATE) {
                    Thread.sleep(100);
                    // Get the name of created file.
                    WatchEvent<Path> ev = cast(event);
                    Path filename = ev.context();

                    System.out.printf("A new file %s was created",
                            filename.getFileName());
                    System.out.println();
                    if(filename.getFileName().toString().endsWith(".zip") || filename.getFileName().toString().endsWith(".zip"))
                        compareService.execOneXml(filename.getFileName().toString());
                }

            }
            key.reset();
        }
    }


}
