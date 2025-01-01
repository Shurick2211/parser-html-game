package nimko.com.paserhtmlgame.view;

import com.microsoft.playwright.Page;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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

  private final Div contentDiv = new Div();

  private final Div pageDiv = new Div();

  private Page page;
  private Map<String, String> content;

  private int pageCount = 1;
  private AtomicInteger count;

  @PostConstruct
  private void init() {
    count = new AtomicInteger();
    contentDiv.setWidthFull();
    VerticalLayout layout = new VerticalLayout(new H2("Hello!"), new HorizontalLayout(
        new Button("Parse", VaadinIcon.BROWSER.create(), e -> getPage(count.getAndIncrement())),
        pageDiv), contentDiv);
    layout.setWidthFull();

    setContent(layout);
  }

  private void getPage(int count) {
    contentDiv.removeAll();
    if (content == null || (count != 0 && count % content.size() == 0)) {
      this.count.set(0);
      page = playwrightService.openPage(getUrl(pageCount++));
      content = playwrightService.getPullHref(page);
    }

    content.entrySet().stream().skip(count).limit(1).forEach(entry -> {
      var link = new Anchor();
      link.addClassNames(Right.MEDIUM);
      var name = new Span(String.format("%d %s:", count + 1, entry.getKey()));
      name.addClassNames(FontWeight.BOLD, Right.MEDIUM);
      var textButton = new Button("Text");
      var text = new Paragraph();
      text.addClassNames(Background.CONTRAST_10);
      textButton.addClickListener(e -> {
        var gameData = playwrightService.getGameSrc(entry.getValue());
        link.setHref(gameData._1().toString());
        link.setText(gameData._1().toString());
        if (!gameData._2().toString().startsWith("https")) {
          text.getElement().setProperty("innerHTML", gameData._2().toString());
         // text.add(gameData._2().toString());
          textButton.setVisible(false);
        } else {
          text.add(new Anchor(gameData._2().toString(), gameData._2().toString()));
        }
      });
      contentDiv.add(new Div(name, link, textButton, text));
    });
  }

  private String getUrl(int page) {
    pageDiv.setText("Page - " + page);
    var url = page == 1 ? "https://sprunkin.com/trending-games"
        : String.format("https://sprunkin.com/trending-games/page/%d/", page);
    log.info("Num - {}", url);
    return url;
  }
}
