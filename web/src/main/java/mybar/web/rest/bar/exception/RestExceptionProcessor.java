package mybar.web.rest.bar.exception;

import mybar.exception.BottleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice(annotations = RestController.class)
public class RestExceptionProcessor {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BottleNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo bottleNotFound(HttpServletRequest req, BottleNotFoundException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.no.bottle.id", null, locale);

        errorMessage += ex.getBottleId();
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

}