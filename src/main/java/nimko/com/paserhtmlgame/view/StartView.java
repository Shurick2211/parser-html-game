package nimko.com.paserhtmlgame.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nimko.com.paserhtmlgame.service.PlaywrightService;

@PageTitle("Start page")
@Route(value = "start-page")
@RouteAlias(value = "")
@Slf4j
@RequiredArgsConstructor
public class StartView extends AppLayout {

  private final PlaywrightService playwrightService;
  private VerticalLayout layout;

  @PostConstruct
  private void init(){
    layout = new VerticalLayout(new H2("Hello!"), new Button("Parse", VaadinIcon.BROWSER.create(), e -> getPage()));
    layout.setWidthFull();


    setContent(layout);
  }

  private void getPage(){
    var url = "https://sprunkin.com/trending-games/";
    var content = playwrightService.getPullHref(playwrightService.openPage(url));
    content.forEach((k,v) -> layout.add(new Div(new Span(k), new Anchor(v, v))));
  }
}
