package nimko.com.paserhtmlgame.service;

import static nimko.com.paserhtmlgame.utils.DateUtils.formatInstantToLocalDateTimeString;
import static nimko.com.paserhtmlgame.utils.SharedComponents.createSimpleDialogWithComponent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding.Left;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import nimko.com.paserhtmlgame.dto.AppInfoDTO;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppSystemService {


  private final InfoEndpoint infoEndpoint;
  private final ObjectMapper objectMapper;

  public AppInfoDTO getAppInfo() {
    Map<String, Object> info = infoEndpoint.info();
    return objectMapper.convertValue(info, AppInfoDTO.class);
  }

  public Dialog createVersionDialog() {
    var list = new ArrayList<>(createInfoList("ui.version", getAppInfo()));
    return createSimpleDialogWithComponent("Info", "Ok", list);
  }

  private List<Component> createInfoList(String serviceName, AppInfoDTO serviceInfo) {
    return List.of(
        new H3(serviceName),
        createInfoH4("Info of build"), createInfoSpan("build.name", serviceInfo.build().name()),
        createInfoSpan("build.artifact", serviceInfo.build().artifact()),
        createInfoSpan("build.group", serviceInfo.build().group()),
        createInfoSpan("build.version", serviceInfo.build().version()),
        createInfoSpan("build.time",
            formatInstantToLocalDateTimeString(serviceInfo.build().time())),
        createInfoH4("info.git"),
        createInfoSpan("git.branch", serviceInfo.git().branch()),
        createInfoSpan("git.commit", serviceInfo.git().commit().toString())
    );
  }

  private H4 createInfoH4(String infoName) {
    var h4 = new H4(infoName);
    h4.addClassNames(Left.SMALL);
    return h4;
  }

  private Span createInfoSpan(String key, String value) {
    var span = new Span(String.format("%s %s", key, value));
    span.addClassNames(Left.MEDIUM);
    return span;
  }


}
