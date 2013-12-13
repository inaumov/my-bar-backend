package mybar.app.managedbean.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;

@FacesValidator
public class FileValidator implements Validator {

    private static final int MAX_SIZE = 1024;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        List<FacesMessage> messages = new ArrayList<FacesMessage>();
        Part file = (Part) value;
        if (file.getSize() > MAX_SIZE) {
            messages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "file is too large", "file is too large"));
        }
        if (!"text/plain".equals(file.getContentType())) {
            messages.add(new FacesMessage("not a text file"));
        }
        if (!messages.isEmpty()) {
            throw new ValidatorException(messages);
        }
    }
}