let xmlhttp = new XMLHttpRequest();
let today = new Date();
let currMonth = today.getMonth();
let currYear = today.getFullYear();
let months = ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"];
let monthAndYear = document.querySelector("#monthAndYear");
let eventList = document.querySelector("#eventList");
let selected;
let checkedCount;
let cellId;
let eventListSelection = "month";

window.onload = function() {

    printMonth();
    genCal(currMonth, currYear);
};

function printMonth() {
    xmlhttp.onreadystatechange = useResponse;
    xmlhttp.open("POST", "/PrintMonth", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    let monthYear = "date=" + currYear + "-" + (currMonth+1);
    xmlhttp.send(monthYear);
    eventListSelection = "month";
}

function printAllMonths() {
    xmlhttp.onreadystatechange=useResponse;
    xmlhttp.open("GET", "/PrintAll", true);
    xmlhttp.send(null);
    eventListSelection = "all";
}

function printSelectedDay() {
    xmlhttp.onreadystatechange=useResponse;
    xmlhttp.open("POST", "/PrintDay", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    let monthYear = "date=" + currYear + "-" + (currMonth+1) + "-" + (cellId.substring(1,cellId.length));
    xmlhttp.send(monthYear);
    eventListSelection = "select";
}

function useResponse() {
    document.getElementById("eventList").innerHTML=xmlhttp.responseText;
}

function printAddEventDiv() {
    xmlhttp.onreadystatechange=useResponseEventControls;
    xmlhttp.open("GET", "/AddEvent", true);
    xmlhttp.send(null);
    fillInAddEventDay()
}

//TODO Better solution?
function fillInAddEventDay() {
    let addForm = document.querySelector("#addEvent");
    if(addForm !== null) {
        addForm.eventDate.value = cellId.substring(1, cellId.length) + "-" + (currMonth + 1) + "-" + currYear;
    }
    else {
        setTimeout(fillInAddEventDay, 100);
    }
}

function printEditEventDiv() {
    checkForSelected();
    if(checkedCount >1) {
        alert("You can only edit one event at a time!")
    }
    if(checkedCount === 1) {
        xmlhttp.onreadystatechange = useResponseEventControls;
        xmlhttp.open("GET", "/EditEvent", true);
        xmlhttp.send(null);
        fillInEditEventID();
    }
}

//TODO Better solution?
function fillInEditEventID() {
    if(document.querySelector("#editEvent") !== null) {
        let selectedId ="";
        for (let i = 0; i < selected.length; i++) {
            if (selected[i].checked) {
                selectedId = selected[i].value;
                break;
            }
        }
        document.querySelector("#editEvent").eventID.value = selectedId;
    }
    else {
        setTimeout(fillInEditEventID, 100);
    }
}

//TODO Better solution?
function removeEvent() {
    checkForSelected();
    if(checkedCount >= 1) {
        if (confirm("Would you like to delete the selected?")) {
            let selectedIds = "";
            for (let i = 0; i < selected.length; i++) {
                if (selected[i].checked) {
                    if (selectedIds !== "") {
                        selectedIds += "-";
                    }
                    selectedIds += selected[i].value;
                }
            }
            xmlhttp.open("POST", "/DeleteEvent", true);
            let listOfIds = "eventID=" + selectedIds;
            xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlhttp.send(listOfIds);
            reloadEventList();
        }
    }
}

function useResponseEventControls() {
    document.getElementById("eventControlContainer").innerHTML=xmlhttp.responseText;
}

function checkForSelected() {
    checkedCount = 0;
    selected = document.getElementById("eventTableBody").getElementsByTagName("input");
    for (let i = 0;i<selected.length;i++ ) {
        if(selected[i].checked) {
            checkedCount++;
        }
    }
    if(checkedCount === 0)
    {
        alert("No events have been selected");
    }

    return checkedCount > 0;
}

function reloadEventList() {
    switch (eventListSelection){
        case "all":
            printAllMonths();
            break;
        case "month":
            printMonth();
            break;
        case "select":
            printSelectedDay();
            break;
        default:
            printAllMonths();
            break;
    }
}

function clearEventList() {
    eventList.innerHTML = " ";
    printMonth();
}

function next() {
    currYear = (currMonth === 11) ? currYear + 1 : currYear;
    currMonth = (currMonth + 1) % 12;
    clearEventList();
    genCal(currMonth, currYear);
}

function previous() {
    currYear = (currMonth === 0) ? currYear - 1 : currYear;
    currMonth = (currMonth === 0) ? 11 : currMonth - 1;
    clearEventList();
    genCal(currMonth, currYear);
}

function genCal(month, year) {
    let firstDay = (new Date(year, month)).getDay();
    let daysInMonth = 32 - new Date(year, month, 32).getDate();
    let tbl = document.querySelector("#calBody");
    let date = 1;
    tbl.innerHTML = "";
    monthAndYear.innerHTML = months[month] + " " + year;

    for (let i = 0; i < 6; i++) {
        let row = document.createElement("tr");
        for (let j = 0; j < 7; j++) {
            if (i === 0 && j < firstDay) {
                let cell = document.createElement("td");
                let cellText = document.createTextNode("");
                cell.appendChild(cellText);
                row.appendChild(cell);
            }
            else if (date > daysInMonth) {
                break;
            }
            else {
                let cell = document.createElement("td");
                let cellText = document.createTextNode(date);
                if (date === today.getDate() && year === today.getFullYear() && month === today.getMonth()) {
                    cell.classList.add("currentDay");
                }
                cellId = "d"+date;
                cell.id = cellId;
                cell.addEventListener("click", function (ev) {
                    cellId =ev.target.id;
                    document.getElementById("eventControlContainer").innerHTML=" ";
                    printSelectedDay();
                });
                cell.appendChild(cellText);
                row.appendChild(cell);
                date++;
            }
        }
        tbl.appendChild(row);
    }
}