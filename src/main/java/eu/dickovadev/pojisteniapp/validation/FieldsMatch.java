package eu.dickovadev.pojisteniapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsMatchValidator.class)
public @interface FieldsMatch {
    String message() default "Hodnoty se neshodují";
    String field();
    String confirmField();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
