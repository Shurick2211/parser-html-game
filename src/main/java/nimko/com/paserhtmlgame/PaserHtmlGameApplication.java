package nimko.com.paserhtmlgame;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("my-theme")
@Push(PushMode.MANUAL)
public class PaserHtmlGameApplication implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(PaserHtmlGameApplication.class, args);
  }

  @Override
  public void configurePage(final AppShellSettings settings) {
    settings.addFavIcon("icon", "icons/favicon.ico", "64x64");
  }
}
