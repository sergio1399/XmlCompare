package app;

import app.components.directory.ChangeService;
import app.components.directory.CompareService;
import app.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        CompareService compareService = ctx.getBean(CompareService.class);
        compareService.execComparation();
        ChangeService changeService = ctx.getBean(ChangeService.class);
        try {
            changeService.watchNewXml();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
