package nimko.com.paserhtmlgame.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Collection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SharedComponents {

  public static Dialog createSimpleDialogWithComponent(String title, String okText,
      Collection<Component> text) {
    var layout = new VerticalLayout();
    layout.setSpacing(false);
    text.forEach(layout::add);
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle(title);
    dialog.add(layout);
    dialog.setCloseOnEsc(true);
    Button closeButton = new Button(okText,
        (e) -> dialog.close());
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    dialog.getFooter().add(closeButton);
    return dialog;
  }
}
