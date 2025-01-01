package nimko.com.paserhtmlgame.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@PageTitle("Start page")
@Route(value = "start-page")
@RouteAlias(value = "")
@Slf4j
public class StartView extends AppLayout {

  @PostConstruct
  private void init(){
    var layout = new VerticalLayout(new H2("Hello!"));


    setContent(layout);
  }

}
