package com.karrardelivery.controller.spec;

import com.karrardelivery.entity.management.User;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
    @Spec(params = "firstName", path = "firstName", spec = LikeIgnoreCase.class),
    @Spec(params = "middleName", path = "middleName", spec = LikeIgnoreCase.class),
    @Spec(params = "lastName", path = "lastName", spec = LikeIgnoreCase.class),
    @Spec(params = "phone", path = "phone", spec = LikeIgnoreCase.class),
    @Spec(params = "email", path = "email", spec = LikeIgnoreCase.class),
    @Spec(params = "query", path = "role", spec = Equal.class),
    @Spec(params = "role", path = "enabled", spec = Equal.class),
    @Spec(params = "deleted", path = "deleted", spec = Equal.class, constVal = "false")

})
public interface UserSpec extends Specification<User> {
}
