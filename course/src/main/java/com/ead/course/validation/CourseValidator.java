package com.ead.course.validation;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dto.CourseDto;
import com.ead.course.dto.UserDto;
import com.ead.course.enums.UserType;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    AuthUserClient authUserClient;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        //valida cada um dos atributos de courseDto
        validator.validate(courseDto, errors);
        //validação de userInstructor
        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors){
        ResponseEntity<UserDto> responseUserInstructor;
        try{
            responseUserInstructor = authUserClient.getOneUserById(userInstructor);
            if(responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT)){
                errors.rejectValue("userInstructor", "UserInstructorError", "User must be an INSTRUCTOR OR ADMIN!");
            }
        }catch (HttpStatusCodeException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found!");
            }
        }
    }
}
