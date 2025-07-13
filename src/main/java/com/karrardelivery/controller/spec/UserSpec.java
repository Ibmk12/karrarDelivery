package com.karrardelivery.controller.spec;

import com.karrardelivery.entity.management.User;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
    @Spec(path = "firstName", spec = LikeIgnoreCase.class),
    @Spec(path = "middleName", spec = LikeIgnoreCase.class),
    @Spec(path = "lastName", spec = LikeIgnoreCase.class),
    @Spec(path = "phone", spec = LikeIgnoreCase.class),
    @Spec(path = "email", spec = LikeIgnoreCase.class),
    @Spec(path = "role", spec = Equal.class),
    @Spec(path = "enabled", spec = Equal.class),
    @Spec(path = "deleted", spec = Equal.class, constVal = "false")

})
public interface UserSpec extends Specification<User> {
}
