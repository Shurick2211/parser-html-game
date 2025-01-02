package nimko.com.paserhtmlgame.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaywrightService {

  private Playwright playwright;
  private Browser browser;
  private Map<String, String> hrefs;
  private Locator locator;

  @PostConstruct
  private void setUp() {
    if (playwright == null) {
      playwright = Playwright.create();
    }
    if (browser == null) {
      browser = playwright.webkit().launch(
          new LaunchOptions().setHeadless(true)
      );
    }
  }

  public Page openPage(String url) {
    var page = browser.newPage();
    page.setDefaultTimeout(30000);
    page.navigate(url);
    return page;
  }

  public Map<String, String> getPullHref(Page page) {
    hrefs = new LinkedHashMap<>();
    locator = page.locator("li > a.as-game-box");
    int count = locator.count();
    for (int i = 0; i < count; i++) {
      String href = locator.nth(i).getAttribute("href");
      String name = locator.nth(i).textContent();
      if (href != null && name != null) {
        hrefs.put(name, href);
      }
    }
    page.close();
    return hrefs;
  }


  public Tuple<String, String> getGameSrc(String href) {
    var pageOfGame = openPage(href);
    Locator iframeLocator = pageOfGame.locator("iframe#gameframe");
    String text = href;
    try {
      Locator firstParagraph = pageOfGame.locator(
              "body > div.container > div.container-inner > div.content-main > div.game-description > div.game-description-inner")
          .first();
      text = firstParagraph.innerHTML();
    } catch (Exception e) {
      log.error("{}.getGameSrc() - Parsing error for {}", getClass().getSimpleName(), href);
    }
    pageOfGame.close();
    return new Tuple<>(iframeLocator.getAttribute("data-src"), text);
  }

  @PreDestroy
  private void tearDown() {
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
    log.info("{}.tearDown() - Done", getClass().getSimpleName());
  }

  public boolean checkNoGames(String url, String key) {
    var page = openPage(url);
    page.click("a.but_search[aria-label='Поиск...']");

    page.fill("input.find[name='find']", key);
    page.press("input.find[name='find']", "Enter");
    var result = page.innerText("div.games")
        .contains("К сожалению, мы не нашли ни одной игры с названием");
    log.info("Res - {}", result);
    page.close();
    return result;
  }
}
