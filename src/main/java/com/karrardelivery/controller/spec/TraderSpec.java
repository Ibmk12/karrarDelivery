package com.karrardelivery.controller.spec;

import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "phoneNumber", path = "phoneNumber", spec = Equal.class),
        @Spec(params = "email", path = "email", spec = LikeIgnoreCase.class),
        @Spec(params = "deleted", path = "deleted", spec = Equal.class)
})
public interface TraderSpec extends Specification<Trader> {
}
