package nimko.com.paserhtmlgame.view;

import com.microsoft.playwright.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import jakarta.annotation.PostConstruct;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nimko.com.paserhtmlgame.service.AppSystemService;
import nimko.com.paserhtmlgame.service.PlaywrightService;

@PageTitle("Start page")
@Route(value = "start-page")
@RouteAlias(value = "")
@Slf4j
@RequiredArgsConstructor
public class StartView extends AppLayout {

  private final PlaywrightService playwrightService;
  private final AppSystemService systemService;

  private Div contentDiv;
  private Div pageDiv;
  private Map<String, String> content;
  private Map<String, String> autoScanContent;
  private final static int AUTO_SCAN_NUM = 10;
  private int pageCount;
  private AtomicInteger count;
  private Div parseDiv;
  private Span total;
  private Button parseButton;

  @PostConstruct
  private void init() {
    pageCount = 1;
    pageDiv = new Div("Page - 1");
    total = new Span();
    autoScanContent = new LinkedHashMap<>();
    addClassNames(FontSize.LARGE, Width.FULL);
    count = new AtomicInteger();
    contentDiv = new Div();
    contentDiv.setWidthFull();
    getPage(count.getAndIncrement());
    parseButton = new Button("Parse", VaadinIcon.BROWSER.create(),
        e -> getPage(count.getAndIncrement()));
    parseDiv = new Div(parseButton);
    Button nextPage = new Button("Next page", VaadinIcon.ARROW_RIGHT.create(), e -> nextPage());
    var buttonAuto = new Button("Auto", VaadinIcon.SEARCH.create(), e -> autoSearch());
    buttonAuto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    var buttonClear = new Button("Clear", VaadinIcon.DEL.create(), e -> init());
    buttonClear.addThemeVariants(ButtonVariant.LUMO_ERROR);
    var mainControl = new HorizontalLayout(pageDiv, nextPage, buttonAuto, buttonClear);
    mainControl.addClassNames(AlignItems.CENTER);
    var controlLayout = new HorizontalLayout(parseDiv, total);
    controlLayout.addClassNames(AlignItems.CENTER);

    var versionDiv = new HorizontalLayout(new Button("Version", VaadinIcon.PACKAGE.create(),
        e -> systemService.createVersionDialog().open()));
    versionDiv.setWidthFull();
    versionDiv.addClassNames(AlignItems.CENTER, JustifyContent.END);

    var firstLayout = new HorizontalLayout(new H2("Hello worker!"), versionDiv);
    firstLayout.setWidthFull();

    VerticalLayout layout = new VerticalLayout(firstLayout,
        mainControl, controlLayout, contentDiv);
    layout.setWidthFull();
    setContent(layout);
  }

  protected void autoSearch() {
    log.info("Start AUTO");
    contentDiv.removeAll();
    count.set(0);
    boolean notFirst = false;
    while (autoScanContent.size() < AUTO_SCAN_NUM) {
      if (notFirst) {
        nextPage();
      }
      content.entrySet().stream().filter(e -> checkIn(e.getKey()))
          .limit(AUTO_SCAN_NUM - autoScanContent.size())
          .forEach(e -> autoScanContent.put(e.getKey(), e.getValue()));
      notFirst = true;
    }
    total.setText("Найдено - " + autoScanContent.size());
    content = autoScanContent;
    //   parseDiv.removeAll();
//    var parseButton = new Button("Parse", VaadinIcon.BROWSER.create(),
//        e -> {
//          createParsePanel(count.getAndIncrement(), false);
//          if (count.get() == AUTO_SCAN_NUM) {
//            e.getSource().setEnabled(false);
//          }
//        });
//    parseDiv.add(parseButton);
    parseButton.setEnabled(false);
    for (int i = 0; i < AUTO_SCAN_NUM; i++) {
      createParsePanel(count.getAndIncrement(), false);
    }
  }

  private void nextPage() {
    contentDiv.removeAll();
    this.count.set(0);
    Page page = playwrightService.openPage(getUrl(pageCount++));
    content = playwrightService.getPullHref(page);
  }

  private void getPage(int count) {
    if (content == null || (count != 0 && count % content.size() == 0)) {
      nextPage();
    }
    createParsePanel(count, true);
  }

  private void createParsePanel(int count, boolean needCheck) {
    var panelLayout = new VerticalLayout();
    content.entrySet().stream().skip(count).limit(1).forEach(entry -> {
      var link = new Anchor();
      link.addClassNames(Right.MEDIUM);
      var name = new Span(String.format("%d %s:", count + 1, entry.getKey()));
      name.addClickListener(e -> copyInBuffer(entry.getKey()));
      name.addClassNames(FontWeight.BOLD, Right.MEDIUM);
      var textButton = new Button("Text");
      var checkButton = new Button("Проверить!");
      var disabledButton = new Button("", VaadinIcon.TRASH.create(), e -> {
        panelLayout.setVisible(false);
        if (contentDiv.getChildren().noneMatch(Component::isVisible)) {
          parseButton.setEnabled(true);
        }
      });
      disabledButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
      checkButton.setEnabled(needCheck);
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
      textButton.addClickListener(e -> parseText(entry, link, text, textButton));
      panelLayout.add(new HorizontalLayout(name, link, textButton, checkButton, disabledButton),
          text);
      contentDiv.add(panelLayout);
      if (!needCheck) {
        textButton.getElement().callJsFunction("click");
      }
    });
  }

  private void parseText(Entry<String, String> entry, Anchor link, Paragraph text,
      Button textButton) {
    var gameData = playwrightService.getGameSrc(entry.getValue());
    String href = gameData.getT1();
    link.setHref(href);
    link.setText(href);
    link.setTarget("_blank");
    link.addAttachListener(e -> copyInBuffer(href));
    if (!gameData.getT2().startsWith("https")) {
      text.getElement().setProperty("innerHTML", gameData.getT2());
      textButton.setVisible(false);
    } else {
      var a = new Anchor(gameData.getT2(), gameData.getT2());
      a.setTarget("_blank");
      text.add(a);
    }
  }

  private boolean checkIn(String key) {
    var url = "https://www.najox.com/ru/";
    try {
      return playwrightService.checkNoGames(url, key);
    } catch (Exception e) {
      log.error("{}.checkIn() - Error check - {}, mess - {}", getClass().getSimpleName(), key,
          e.getMessage());
    }
    return false;
  }

  private String getUrl(int page) {
    pageDiv.setText("Page - " + page);
    return page == 1 ? "https://sprunkin.com/trending-games"
        : String.format("https://sprunkin.com/trending-games/page/%d/", page);
  }

  private void copyInBuffer(String text) {
    try {
      Process process = Runtime.getRuntime().exec("cmd /c clip");
      try (OutputStream os = process.getOutputStream()) {
        os.write(text.getBytes());
        os.flush();
      }
      System.out.println("Text copied to clipboard: " + text);
    } catch (IOException e) {
      log.error("{}.copeInBuffer() - Failed to copy text to clipboard.", getClass().getSimpleName(),
          e);
    }
    Notification notification = new Notification("Скопировано в буфер обмена!");
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    notification.setPosition(Position.BOTTOM_CENTER);
    notification.setDuration(2000);
    notification.open();
  }
}
