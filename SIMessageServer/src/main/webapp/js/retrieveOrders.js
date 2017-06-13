/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var orderList = new Array();

//var output = document.getElementById("output");

function getCurrentPos(evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}
            
function defineImage(evt) {
    var currentPos = getCurrentPos(evt);
    
    for (i = 0; i < document.inputForm.color.length; i++) {
        if (document.inputForm.color[i].checked) {
            var color = document.inputForm.color[i];
            break;
        }
    }
            
    for (i = 0; i < document.inputForm.shape.length; i++) {
        if (document.inputForm.shape[i].checked) {
            var shape = document.inputForm.shape[i];
            break;
        }
    }
    
    var json = JSON.stringify({
        "shape": shape.value,
        "color": color.value,
        "coords": {
            "x": currentPos.x,
            "y": currentPos.y
        }
    });
    drawImageText(json);        
    sendText(json);
}

/**
 * parse Order object and display order in its readable format
 * @param {type} order
 * @returns {undefined}
 */
function parseOrder(order) {
//    console.log("order object==> " + order.protocol_type + ", " + order.request);
//    console.log("order.order is " + order.order);
    if(typeof order.order !== 'undefined'){
        orderList.push(order.order);

        var str = "now we have " + orderList.length + " orders<br>";
        writeToScreen(str);
        writeToScreen(JSON.stringify(order.order));
    }else {
        console.log("order.order is " + order.order);
        writeToScreen(order.protocol_type);
    }
    
}