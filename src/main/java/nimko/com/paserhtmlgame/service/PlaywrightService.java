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

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaywrightService {

  protected Playwright playwright;
  protected Browser browser;

  @PostConstruct
  private void setUp() {
    if (playwright == null) {
      playwright = Playwright.create();
    }
    if (browser == null) {
      browser = playwright.chromium().launch(
          new LaunchOptions().setHeadless(true)
      );
    }
  }

  public Page openPage(String url) {
    var page = browser.newPage();
    page.setDefaultTimeout(6000);
    page.navigate(url);
    return page;
  }

  public Map<String, String> getPullHref(Page page) {
    Map<String, String> hrefs = new LinkedHashMap<>();
    Locator locator = page.locator("li > a.as-game-box");
    int count = locator.count();

    for (int i = 0; i < count; i++) {
      String href = locator.nth(i).getAttribute("href");
      String name = locator.nth(i).textContent();
      if (href != null && name != null) {
        var pageOfGame = openPage(href);
        Locator iframeLocator = pageOfGame.locator("iframe#gameframe");
        String dataSrc = iframeLocator.getAttribute("data-src");
        hrefs.put(name, dataSrc);
      }
    }
    return hrefs;
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

}
