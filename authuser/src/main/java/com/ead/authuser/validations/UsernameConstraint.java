package com.ead.authuser.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameConstraintImpl.class)//classe que vai conter essa validação
@Target({ElementType.METHOD, ElementType.FIELD})//quais os campos que serão aceitos a anotação
@Retention(RetentionPolicy.RUNTIME)//quando a validação vai ocorrer
public @interface UsernameConstraint {
    String message() default "Invalid username";//a mensagem que vai aparecer quando a anotação for chamada
    Class<?>[] groups() default{};//definição do grupo que vai aceitar a anotação
    Class<? extends Payload>[] payload() default{};//Payload: O nível em que o erro vai ocorrer

}
