let size = 8;
let selected_row = -1;
let selected_col = -1;
let elements = [];

let player_color = undefined;

class Grid {

    constructor() {
        this.size = size;
        this.cells = [[]];
    }

    fill(data) {
        let rw = [];
        for (let cell = 0; cell < this.size * this.size; cell++) {
            let row = data[cell].row;
            let col = data[cell].col;
            rw[col] = [data[cell].cell];
            if (col === 7) {
                this.cells[row] = rw;
                rw = [];
            }
        }
    }
}

let grid;

/*function handleCardOnDrag(event) {
    event.dataTransfer.setData("text", event.target.id);
}*/

function updateBoard(grid) {
    for (let row = 0; row < grid.size; row++) {
        for (let col = 0; col < grid.size; col++) {
            let html = "<div class='cell'" + /* draggable='true'*/ ">" + grid.cells[row][col].toString();
            $("#square-" + row + "-" + col).html(grid.cells[row][col].toString());
        }
    }
}

function registerMouseMoveEvent() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            let id = "square-" + row.toString() + "-" + col.toString();

            $(document.getElementById(id)).mouseenter(function () {
                if (this.innerText !== "") {
                    $(this).addClass('square-hover');
                }
            });

            $(document.getElementById(id)).mouseleave(function () {
                if ($(this).hasClass('square-hover')) {
                    $(this).removeClass('square-hover');
                }
            })
        }
    }
}

function registerClickListener() {
    for (let row = 0; row < grid.size; row++) {
        for (let col = 0; col < grid.size; col++) {
            let id = "square-" + row.toString() + "-" + col.toString();

            $(document.getElementById(id)).click(function () {
                if (selected_row === -1 && selected_col === -1) {
                    if (this.innerText !== "") {
                        $(this).addClass('selected');
                        let pMoves = getPossibleMove(row, col);
                        for (let move = 0; move < pMoves.cell.moves.length; move++) {
                            let r = pMoves.cell.moves[move][0];
                            let c = pMoves.cell.moves[move][1];

                            let highlightedElement = document.getElementById("square-" + r.toString() + "-" + c.toString());

                            elements.push(highlightedElement);
                            $(highlightedElement).addClass('selected');
                        }
                        selected_row = row;
                        selected_col = col;
                    }
                } else {
                    for (let i = 0; i < elements.length; i++) {
                        $(elements[i]).removeClass('selected');
                    }
                    if (selected_row !== row || selected_col !== col) {
                        doTurn(selected_row, selected_col, row, col);
                    }
                    $("#square-" + selected_row + "-" + selected_col).removeClass('selected');
                    selected_col = -1;
                    selected_row = -1;
                }

            });
        }
    }
}

function updateGameStatus() {
    let gameStatus = getGameStatus();
    let playerAtTurn = getPlayerAtTurn();
    let playerNotAtTurn = getPlayerNotAtTurn();
    let message = getMessage();
    let id = document.getElementById('message-box');

    id.className = "";
    if (gameStatus === "NEXT_PLAYER") {
        $(id).addClass("alert alert-primary");
        id.innerText = playerAtTurn.toString() + message.toString();
    } else if (gameStatus === "CHECK_MATE") {
        $(id).addClass("alert alert-danger");
        id.innerText = playerNotAtTurn.toString() + message.toString();
    } else if (gameStatus === "MOVE_NOT_VALID") {
        $(id).addClass("alert alert-danger");
        id.innerText = playerAtTurn.toString() + message.toString();
    } else {
        $(id).addClass("alert alert-warning");
        id.innerText = playerAtTurn.replace(/(\r\n|\n|\r)/gm, "").toString() + ", " +
            message.replace(/(\r\n|\n|\r)/gm, "").toString();
    }
}

function loadJson() {
    $.ajax({
        method: "GET",
        url: "/json",
        dataType: "json",
        async: false,

        success: function (result) {
            grid.fill(result.grid.cells);
            updateBoard(grid);
            updateGameStatus();
        }
    });
}

function doTurn(row, col, row_two, col_two) {
    /*$.get("/turn/" + row.toString() + "/" + col.toString() +
        "/" + row_two.toString() + "/" + col_two.toString(),
        function (data) {
            console.log("Do Turn on Server");
        }
    );*/
    //console.log(player_color.toString())
    $.ajax({
        method: "GET",
        url: "/turn/" + row.toString() + "/" + col.toString() + "/" + row_two.toString() + "/" + col_two.toString(),
        dataType: "json",
        async: false,

        success: function(data) {
            grid.fill(data.grid.cells);
            updateBoard(grid);
        }
    });
}

function getPlayerAtTurn() {
    let playerAtTurn = "";
    $.ajax({
        method: "GET",
        url: "/playerAtTurn",
        dataType: "html",
        async: false,

        success: function (data) {
            playerAtTurn = data;
        }
    });
    return playerAtTurn;
}

function getPlayerNotAtTurn() {
    let playerNotAtTurn = "";
    $.ajax({
        method: "GET",
        url: "/playerNotAtTurn",
        dataType: "html",
        async: false,

        success: function (data) {
            playerNotAtTurn = data;
        }
    });
    return playerNotAtTurn;
}

function getGameStatus() {
    let gameStatus = "";
    $.ajax({
        method: "GET",
        url: "/gameStatus",
        dataType: "html",
        async: false,

        success: function (data) {
            gameStatus = data.toString();
        }
    });
    return gameStatus;
}

function getMessage() {
    let message = "";
    $.ajax({
        method: "GET",
        url: "/message",
        dataType: "html",
        async: false,

        success: function (data) {
            message = data;
        }
    });

    return message;
}

function getPossibleMove(row, col) {
    let msg = null;
    $.ajax({
        method: "GET",
        url: "/possibleMove/" + row.toString() + "/" + col.toString(),
        dataType: "json",
        async: false,

        success: function (data) {
            msg = data;
        }
    });

    return msg;
}

function newGame() {
    let playerOne = "playerOne";
    let playerTwo = "playerTwo";

    $.ajax({
        method: "GET",
        url: "/new/" + playerOne.toString() + "/" + playerTwo.toString(),
        dataType: "json",
        async: false,

        success: function (result) {
            grid.fill(result.grid.cells);
            updateBoard(grid);
            updateGameStatus();
        }
    });

    $(document.getElementById('navbarDropdown')).removeClass('open');
}

function newGameWithNames() {
    let playerOne = document.getElementById("player-one-name").value;
    let playerTwo = document.getElementById("player-two-name").value;
    let errorBox = document.getElementById("error-box");
    if (playerOne === "" || playerTwo === "") {
        $(errorBox).addClass("alert alert-danger");
        errorBox.innerText = "Please fill all Name Boxes";
        $(errorBox).role = "alert";
    } else {
        $.ajax({
            method: "GET",
            url: "/new/" + playerOne.toString() + "/" + playerTwo.toString(),
            dataType: "json",
            async: false,

            success: function (result) {
                grid.fill(result.grid.cells);
                updateBoard(grid);
                updateGameStatus();
                $(document.getElementById("newGameWithNames")).modal('hide');

            }
        });
        if ($(errorBox).hasClass("alert alert-danger")) {
            $(errorBox).removeClass("alert alert-danger");
            errorBox.innerText = "";
            $(errorBox).role = "";
        }
    }
}

function connectWebSocket() {
    var websocket = new WebSocket("ws://localhost:9000/websocket");
    websocket.setTimeout = -1;

    websocket.onopen = function (event) {
        console.log("Connected to Websocket");
        websocket.send("connect");
    };

    websocket.onclose = function () {
        console.log("Connection with Websocket closed!");
        //connectWebSocket();
    };

    websocket.onerror = function (error) {
        console.log("Error in Websocket Occurred: " + error);
        //connectWebSocket();
    };

    websocket.onmessage = function (e) {
        if (typeof e.data === "string") {
            let json = JSON.parse(e.data);
            let cells = json.grid.cells;
            grid.fill(cells);
            updateBoard(grid);
            updateGameStatus();
        }
    }
}

$(document).ready(function () {
    grid = new Grid();
    loadJson();
    connectWebSocket();
    registerClickListener();
    registerMouseMoveEvent();
});
