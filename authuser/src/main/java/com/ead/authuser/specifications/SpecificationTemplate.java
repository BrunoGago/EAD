package com.ead.authuser.specifications;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "userType", spec = Equal.class),
            @Spec(path = "email", spec = Like.class),
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "fullName", spec = Like.class)
    })

    public interface UserSpec extends Specification<UserModel>{}

    public static Specification<UserModel> userCourseId(final UUID courseId){
        return (root, query, cb) -> {
            //Dados não serão repetidos
            query.distinct(true);
            //Join entre as entidades e qual a relação entre elas
            Join<UserModel, UserCourseModel> userProd = root.join("usersCourses");
            //Retorna o criteriaBuilder
            return cb.equal(userProd.get("courseId"), courseId);
        };
    }

}
