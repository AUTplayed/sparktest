var $ = document.querySelector.bind(document);

$("#save").onclick = () => {
    var person = {
        firstName: $("#firstName").value,
        lastName: $("#lastName").value
    }
    var request = new XMLHttpRequest();
    request.open("POST", "/api/person");
    request.send(JSON.stringify(person));
}