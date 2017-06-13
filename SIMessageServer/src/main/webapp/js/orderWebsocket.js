/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var wsUri = "ws://" + document.location.host + "/SIMessageServer/pos_message_server_endpoint";
console.log(wsUri);
//var wsUri = "ws://localhost:8080/SIMessageServer/pos_message_server_endpoint";
var websocket = new WebSocket(wsUri);
// For testing purposes
var output = document.getElementById("output");

websocket.onerror = function(evt) { onError(evt); };
websocket.onopen = function(evt) { onOpen(evt); };
websocket.onmessage = function(event) { onMessage(event); };

function onError(evt) {
    writeToScreen("<h4>error uri: " + wsUri + " </h4>");
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function writeToScreen(message) {
    document.innerHTML += "<br>" + message + "<br>";
}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
    // onOpen send auth credential to server endpoint
    sendText("{\"protocol_type\":\"auth\",\"credential\":{\"business_id\":\"abcde_12345\"}}");
}

/**
 * Send message to server endpoint
 * @param {type} json in text format
 * @returns none
 */
function sendText(json) {
    console.log("sending text: " + json);
    websocket.send(json);
}
 
 /**
  * All incomming stream from server endpoint will be received through this function as an event
  * @param {type} incomming message as an event, data can be access as event.data in string format.
  * @returns none
  */
function onMessage(event) {
    console.log("data received==> " + event.data);    
    var message = JSON.parse(event.data);
    console.log("message received==> " + message);
    //writeToScreen(evt.data);
    parseOrder(message);
    
}