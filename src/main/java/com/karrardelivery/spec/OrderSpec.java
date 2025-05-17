package com.karrardelivery.spec;

import com.karrardelivery.entity.Order;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "status", path = "status", spec = Equal.class),
        @Spec(params = "deliveryAgent", path = "deliveryAgent", spec = Equal.class)
//        @Spec(path = "agentAmount", spec = Equal.class, constVal = "OFFICER")
})
public interface OrderSpec extends Specification<Order> {
}
