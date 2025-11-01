package com.karrardelivery.controller.spec;

import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.OnTypeMismatch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Join(path = "trader", alias = "t")
@And({
        @Spec(params = "deliveryStatus", path = "deliveryStatus", spec = Equal.class),
        @Spec(params = "invoiceNo", path = "invoiceNo", spec = Equal.class),
        @Spec(params = "customerPhoneNo", path = "customerPhoneNo", spec = LikeIgnoreCase.class),
        @Spec(params = "address", path = "address", spec = LikeIgnoreCase.class),
        @Spec(params = "traderName", path = "t.name", spec = LikeIgnoreCase.class),
        @Spec(params = "traderPhoneNumber", path = "t.phoneNumber", spec = LikeIgnoreCase.class),
        @Spec(params = "email", path = "t.email", spec = LikeIgnoreCase.class),
        @Spec(params = "fromOrderDate", path = "orderDate", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toOrderDate", path = "orderDate", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromDeliveryDate", path = "deliveryDate", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toDeliveryDate", path = "deliveryDate", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromLastUpdated", path = "lastUpdated", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toLastUpdated", path = "lastUpdated", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromTotalAmount", path = "totalAmount", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toTotalAmount", path = "totalAmount", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromTraderAmount", path = "traderAmount", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toTraderAmount", path = "traderAmount", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromDeliveryAmount", path = "deliveryAmount", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toDeliveryAmount", path = "deliveryAmount", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromAgentAmount", path = "agentAmount", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toAgentAmount", path = "agentAmount", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "fromNetCompanyAmount", path = "netCompanyAmount", spec = GreaterThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION),
        @Spec(params = "toNetCompanyAmount", path = "netCompanyAmount", spec = LessThanOrEqual.class,
                onTypeMismatch = OnTypeMismatch.EXCEPTION)
})
public interface OrderSpec extends Specification<Order> {

    static Specification<Order> applyDefaultStatusesIfMissing(Specification<Order> original) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("deliveryStatus"), EDeliveryStatus.DELIVERED),
                cb.equal(root.get("deliveryStatus"), EDeliveryStatus.CANCELED),
                cb.equal(root.get("deliveryStatus"), EDeliveryStatus.UNDER_DELIVERY)
        );
    }
}
