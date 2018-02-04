package mybar.web.rest.rates.exception;

import lombok.extern.slf4j.Slf4j;
import mybar.exception.CocktailNotFoundException;
import mybar.web.rest.bar.exception.ErrorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Locale;

@Slf4j
@ControllerAdvice(basePackages = "mybar.web.rest.rates")
public class RatesRestExceptionProcessor {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(CocktailNotFoundException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorInfo cocktailNotFound(HttpServletRequest req, CocktailNotFoundException ex) {
        log.error("Cocktail not found thrown", ex);

        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage("error.could.not.rate.unknown.cocktail", null, locale);
        errorMessage = MessageFormat.format(errorMessage, ex.getCocktailId());
        String errorURL = req.getRequestURL().toString();

        return new ErrorInfo(errorURL, errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo requiredFields(HttpServletRequest req, IllegalArgumentException ex) {
        log.error("Invalid property value", ex);

        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, ex.getMessage());
    }

}