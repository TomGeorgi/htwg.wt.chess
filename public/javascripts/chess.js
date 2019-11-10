let size = 8;
let selected_row = -1;
let selected_col = -1;

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

function setupBoard() {
    for(let row = 0; row < size; row++) {
        for(let col = 0; col < size; col++) {
            let square = $('#square-' + row + "-" + col);
            if((row + col) % 2 !== 0) {
                square.addClass('square-white');
            } else {
                square.addClass('square-black');
            }
            //
        }
    }
}

function updateBoard() {
    for (let row = 0; row < grid.size; row++) {
        for (let col = 0; col < grid.size; col++) {
            let html = "<div class='cell'" + /* draggable='true'*/ ">"+ grid.cells[row][col].toString();
            $("#square-" + row + "-" + col).html(grid.cells[row][col].toString());
        }
    }
}

function registerMouseMoveEvent() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            let id = "square-" + row.toString() + "-" + col.toString();

            $(document.getElementById(id)).mouseenter(function() {
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
                        selected_row = row;
                        selected_col = col;
                    }
                } else {
                    if (selected_row !== row || selected_col !== col) {
                        doTurn(selected_row, selected_col, row, col);
                        updateGameStatus();
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
    } else if(gameStatus === "CHECK_MATE") {
        $(id).addClass("alert alert-danger");
        id.innerText = playerNotAtTurn.toString() + message.toString();
    } else if(gameStatus === "MOVE_NOT_VALID") {
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

        success: function(result) {
            grid.fill(result.grid.cells);
        }
    });
}

function doTurn(row, col, row_two, col_two) {
    $.ajax({
        method: "GET",
        url: "/turn/" + row.toString() + "/" + col.toString() + "/" + row_two.toString() + "/" + col_two.toString(),
        dataType: "json",
        async: false,

        success: function(data) {
            grid.fill(data.grid.cells);
            updateBoard();
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

        success: function(data) {
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

        success: function(data) {
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

        success: function(data) {
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

        success: function(data) {
            message = data;
        }
    });

    return message;
}

function newGame() {
    let playerOne = "playerOne";
    let playerTwo = "playerTwo";

    $.ajax({
        method: "GET",
        url: "/new/" + playerOne.toString() + "/" + playerTwo.toString(),
        dataType: "json",
        async: false,

        success: function(result) {
            grid.fill(result.grid.cells);
            updateBoard();
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

            success: function(result) {
                grid.fill(result.grid.cells);
                updateBoard();
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

$( document ).ready(function () {
    setupBoard();
    grid = new Grid();
    loadJson();
    updateBoard();
    registerClickListener();
    registerMouseMoveEvent();
});