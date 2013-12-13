package mybar.app.managedbean.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        //Get the first password from the attribute.
        UIInput confirmComponent = (UIInput) component.getAttributes().get("confirm");
        //Get the value from the UIInput component.
        String confirm = (String) confirmComponent.getSubmittedValue();
        //Get the value entered in the second string from the component parameter passed to the method.
        String password = (String) value;
        //Compare both fields.
        if (!password.equals(confirm)) {
            confirmComponent.setValid(false);
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password must match confirm password.", null));
        }
    }
}