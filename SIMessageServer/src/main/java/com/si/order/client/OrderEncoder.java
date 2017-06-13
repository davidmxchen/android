/**
 *
 * @author Mingxing Chen
 */

package com.si.order.client;

import com.si.data.Order;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class OrderEncoder implements Encoder.Text<Order>{

    @Override
    public String encode(Order order) throws EncodeException {
        return order.toJSONObject().toString();
    }

    @Override
    public void init(EndpointConfig ec) {
        System.out.println("OrderEncoder init");
    }

    @Override
    public void destroy() {
        System.out.println("OrderEncoder destroy");
    }
    
}
