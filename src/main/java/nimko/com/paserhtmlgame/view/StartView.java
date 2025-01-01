package nimko.com.paserhtmlgame.view;

import com.microsoft.playwright.Page;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
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

  private Div contentDiv;

  private final Div pageDiv = new Div();

  private Map<String, String> content;

  private int pageCount = 1;
  private AtomicInteger count;

  @PostConstruct
  private void init() {
    addClassNames(FontSize.LARGE, Width.FULL);
    count = new AtomicInteger();
    contentDiv = new Div();
    contentDiv.setWidthFull();
    getPage(count.getAndIncrement());
    var controlLayout = new HorizontalLayout(
        new Button("Parse", VaadinIcon.BROWSER.create(), e -> getPage(count.getAndIncrement())),
        pageDiv, new Button("Next page", VaadinIcon.ARROW_RIGHT.create(), e -> nextPage()));
    controlLayout.addClassNames(AlignItems.CENTER);
    VerticalLayout layout = new VerticalLayout(new H2("Hello worker!"), controlLayout,
        contentDiv);
    layout.setWidthFull();
    setContent(layout);

  }

  private void nextPage() {
    this.count.set(0);
    Page page = playwrightService.openPage(getUrl(pageCount++));
    content = playwrightService.getPullHref(page);
  }

  private void getPage(int count) {
    contentDiv.removeAll();
    if (content == null || (count != 0 && count % content.size() == 0)) {
      nextPage();
    }

    content.entrySet().stream().skip(count).limit(1).forEach(entry -> {
      var link = new Anchor();
      link.addClassNames(Right.MEDIUM);
      var name = new Span(String.format("%d %s:", count + 1, entry.getKey()));
      name.addClassNames(FontWeight.BOLD, Right.MEDIUM);
      var textButton = new Button("Text");
      var checkButton = new Button("Проверить!");
      checkButton.addClassNames(Left.MEDIUM);
      checkButton.addClickListener(e -> {
        var res = checkIn(entry.getKey());
        textButton.setEnabled(res);
        if (!res) {
          e.getSource().setText("ЕСТЬ!");
          e.getSource().addThemeVariants(ButtonVariant.LUMO_ERROR);
        } else {
          e.getSource().setText("НОВАЯ!");
          e.getSource().setEnabled(res);
        }
      });
      var text = new Paragraph();
      text.addClassNames(Background.CONTRAST_10);
      textButton.addClickListener(e -> {
        var gameData = playwrightService.getGameSrc(entry.getValue());
        link.setHref(gameData._1().toString());
        link.setText(gameData._1().toString());
        if (!gameData._2().toString().startsWith("https")) {
          text.getElement().setProperty("innerHTML", gameData._2().toString());
          textButton.setVisible(false);
        } else {
          text.add(new Anchor(gameData._2().toString(), gameData._2().toString()));
        }
      });
      contentDiv.add(new Div(name, link, textButton, checkButton, text));
    });
  }

  private boolean checkIn(String key) {
    var url = "https://www.najox.com/ru/";
    return playwrightService.checkGame(url, key);
  }

  private String getUrl(int page) {
    pageDiv.setText("Page - " + page);
    var url = page == 1 ? "https://sprunkin.com/trending-games"
        : String.format("https://sprunkin.com/trending-games/page/%d/", page);
    return url;
  }
}
