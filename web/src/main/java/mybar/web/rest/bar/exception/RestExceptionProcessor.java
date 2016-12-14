package mybar.web.rest.bar.exception;

import mybar.exception.BottleNotFoundException;
import mybar.exception.CocktailNotFoundException;
import mybar.exception.UniqueCocktailNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
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
        errorMessage += " ";
        errorMessage += ex.getBottleId();
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

    @ExceptionHandler(CocktailNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo cocktailNotFound(HttpServletRequest req, CocktailNotFoundException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.no.cocktail.id", null, locale);
        errorMessage += " ";
        errorMessage += ex.getCocktailId();
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

    @ExceptionHandler(UniqueCocktailNameException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo cocktailUniqueName(HttpServletRequest req, UniqueCocktailNameException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.unique.cocktail.name", null, locale);
        errorMessage += " ";
        errorMessage = MessageFormat.format(errorMessage, ex.getName());
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

}