package eu.dickovadev.pojisteniapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.Introspector;
import java.lang.reflect.Method;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String confirmField;
    private String message;

    @Override
    public void initialize(FieldsMatch constraint) {
        this.field = constraint.field();
        this.confirmField = constraint.confirmField();
        this.message = constraint.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String a = read(value, getterName(field));
        String b = read(value, getterName(confirmField));

        // Let @NotBlank handle null/empty; this validator only compares equality
        if (a == null || b == null) return true;

        boolean ok = a.equals(b);
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(confirmField)
                    .addConstraintViolation();
        }
        return ok;
    }

    private String getterName(String prop) {
        String name = Introspector.decapitalize(prop);
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String read(Object target, String getter) {
        try {
            Method m = target.getClass().getMethod(getter);
            Object val = m.invoke(target);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
