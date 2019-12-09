import {LitElement, html, css} from "https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module";

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

function setupBoard() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            let square = document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById("square-" + row + "-" + col);
            if ((row + col) % 2 !== 0) {
                $(square).addClass('square-white');
            } else {
                $(square).addClass('square-black');
            }
            //
        }
    }
}

function updateBoard(grid) {
    for (let row = 0; row < grid.size; row++) {
        for (let col = 0; col < grid.size; col++) {
            let html = "<div class='cell'" + /* draggable='true'*/ ">" + grid.cells[row][col].toString();
            document.getElementsByTagName("chess-app")[0].shadowRoot.getElementById("square-" + row + "-" + col).innerText = grid.cells[row][col].toString();
        }
    }
}

function registerMouseMoveEvent() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            let id = "square-" + row.toString() + "-" + col.toString();

            $(document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById(id)).mouseenter(function () {
                if (this.innerText !== "") {
                    $(this).addClass('square-hover');
                }
            });

            $(document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById(id)).mouseleave(function () {
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

            $(document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById(id), $("chess-app").shadowRoot).click(function () {
                if (selected_row === -1 && selected_col === -1) {
                    if (this.innerText !== "") {
                        $(this, $("chess-app").shadowRoot).addClass('selected');
                        let pMoves = getPossibleMove(row, col);
                        for (let move = 0; move < pMoves.cell.moves.length; move++) {
                            let r = pMoves.cell.moves[move][0];
                            let c = pMoves.cell.moves[move][1];

                            let highlightedElement = document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById("square-" + r.toString() + "-" + c.toString());

                            elements.push(highlightedElement);
                            $(highlightedElement, $("chess-app").shadowRoot).addClass('selected');
                        }
                        selected_row = row;
                        selected_col = col;
                    }
                } else {
                    for (let i = 0; i < elements.length; i++) {
                        $(elements[i], $("chess-app").shadowRoot).removeClass('selected');
                    }
                    elements = [];
                    if (selected_row !== row || selected_col !== col) {
                        doTurn(selected_row, selected_col, row, col);
                    }
                    $(document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById("square-" + selected_row + "-" + selected_col), $("chess-app").shadowRoot).removeClass('selected');
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
    let id = document.getElementsByTagName('chess-app')[0].shadowRoot.getElementById('message-box');

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

window.newGame = function newGame() {
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
};

window.newGameWithNames = function newGameWithNames() {

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
};

window.undo = function undo() {
    return $.ajax({
        method: "GET",
        url: "/undo",
        dataType: "html",
    });
};

window.redo = function redo() {
    return $.ajax({
        method: "GET",
        url: "/redo",
        dataType: "html",
    });
};

function connectWebSocket() {
    var websocket = new WebSocket("ws://localhost:9000/websocket");

    websocket.onopen = function (event) {
        console.log("Connected to Websocket");
        websocket.send("connect");
    };

    websocket.onclose = function () {
        // websocket.setTimeout(connectWebSocket, 1000);
        console.log("Connection with Websocket closed!");
    };

    websocket.onerror = function (error) {
        console.log("Error in Websocket Occurred: " + error);
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
    //setupBoard();
    grid = new Grid();
    loadJson();
    connectWebSocket();
    registerClickListener();
    registerMouseMoveEvent();
});

class ChessApp extends LitElement {
    static get properties() {
        return {
            size: { type: Number },
            rows: { type: Array },
        };
    }
    constructor() {
        super();
        this.size = 8;
        this.rows = [0, 1, 2, 3, 4, 5, 6, 7];
        this.cols = [0, 1, 2, 3, 4, 5, 6, 7];
    }

    static get styles() {
        return [
            super.styles,
            css`
            a:link {
              color: black;
            }
            
            a:any-link {
              color: black;
            }
            
            @media (min-width: 0px) {
            
              .page-container {
                width: 15em;
                margin: auto;
                margin-top: 1em;
                display: block;
              }
            
              .game-container {
                height: 100%;
                width: 100%;
                display: inline-flex;
                color: black;
                font-family: "Verdana", Geneva, sans-serif;
                font-size: 16px;
                font-weight: bold;
                line-height: 19px;
                margin: 0em;
                margin-top: 0.5em;
                margin-bottom: 0.5em;
              }
            }
            
            @media (min-width: 480px) {
            
              .page-container {
                width: 29em;
                margin: auto;
                margin-top: 1em;
                display: block;
              }
            
              .game-container {
                height: 100%;
                width: 100%;
                display: inline-flex;
                color: black;
                font-family: "Verdana", Geneva, sans-serif;
                font-size: 30px;
                font-weight: bold;
                line-height: 33px;
                margin: 0em;
                margin-top: 0.5em;
                margin-bottom: 0.5em;
              }
            }
            
            @media (min-width: 800px) {
            
              .page-container {
                width: 33.5em;
                margin: auto;
                margin-top: 1em;
                display: block;
              }
            
              .game-container {
                height: 100%;
                width: 100%;
                display: inline-flex;
                color: black;
                font-family: "Verdana", Geneva, sans-serif;
                font-size: 40px;
                font-weight: bold;
                line-height: 44px;
                margin: 0em;
                margin-top: 0.5em;
                margin-bottom: 0.5em;
              }
            }
            
            @media (min-width: 1000px) {
            
              .page-container {
                width: 51em;
                margin: auto;
                margin-top: 1em;
                display: block;
              }
            
              .game-container {
                height: 100%;
                width: 100%;
                display: inline-flex;
                margin: 0em;
                margin-top: 0.5em;
                margin-bottom: 0.5em;
                font-size: 60px;
                line-height: 66px;
                font-family: "Verdana",Geneva, sans-serif;
                font-weight: bold;
                color: black;
              }
            }
            
            #Inline-Title {
              text-align: center;
            }
            
            .game {
              border-style: inset;
              border-width: 0.05em;
              border-spacing: 0em;
              border-color: #F6F6FF;
              padding: 0.05em;
              background-color: black;
              margin: auto;
              width: auto;
            }
            
            .clear {
              clear: both;
            }
            
            .number {
              display: inline-block;
              float: left;
              border-style: outset;
              border-width: 0.05em;
              width: 1.2em;
              height: 1.2em;
              text-align: center;
              background-color: #E0E0FF;
              border-color: #F6F6FF;
              margin-right: 0.05em;
            }
            
            .square-white, .square-black {
              display: inline-block;
              float: left;
              border-style: outset;
              border-width: 0.05em;
              border-color: #F6F6FF;
              width: 1.2em;
              height: 1.2em;
              text-align: center;
            }
            
            .square-white {
              background-color: #FECE9D;
            }
            
            .square-black {
              background-color: #D38B44;
            }
            
            .letter, .empty {
              display: inline-block;
              float: left;
              border-style: outset;
              border-width: 0.05em;
              width: 1.2em;
              height: 1.2em;
              text-align: center;
              background-color: #E0E0FF;
              border-color: #F6F6FF;
              border-top-color: black;
              margin-top: 0.05em;
            }
            
            .empty {
              border-top-color: black;
              border-left-color: black;
              margin-right: 0.05em;
            }
            
            h1 {
              text-align: center;
              font-family: "Verdana", Geneva, sans-serif;
            }
            
            p {
              font-family: "Verdana", Geneva, sans-serif;
              font-size: 20px;
              text-align: center;
              white-space: pre-wrap;
            }
            
            #chess-image-text {
              font-weight: bold;
              text-align: center;
            }
            
            #chess-image {
              display: block;
              margin-left: auto;
              margin-right: auto;
              margin-bottom: 2%;
              width: 50%;
              height: 50%;
            }
            
            .square-hover {
              -moz-box-shadow:    inset 0 0 10px #311B0B;
              -webkit-box-shadow: inset 0 0 10px #311B0B;
              box-shadow:         inset 0 0 10px #311B0B;
            }
            
            .selected {
              -moz-filter: grayscale(50%);
              -webkit-filter: grayscale(50%);
              filter: grayscale(50%);
            
              -moz-box-shadow:    inset 0 0 15px #0f0;
              -webkit-box-shadow: inset 0 0 15px #0f0;
            }
            
            .noselect {
              -webkit-touch-callout: none; /* iOS Safari */
              -webkit-user-select: none; /* Safari */
              -khtml-user-select: none; /* Konqueror HTML */
              -moz-user-select: none; /* Firefox */
              -ms-user-select: none; /* Internet Explorer/Edge */
              user-select: none; /* Non-prefixed version, currently
                                              supported by Chrome and Opera */
            }
            `
        ];
    }

    render() {
        return html`
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

            <div class="page-container">
                <div class="game-container">
                    <div class="game">
                        ${this.rows.map(row => html`
                            <div class="c-row">
                                <number-element id="${row}"></number-element>
                                ${this.cols.map(col => html`
                                    ${(row + col) % 2 !== 0 ? 
                                        html`<div id="square-${row}-${col}" class="noselect square-white"></div>`: 
                                        html`<div id="square-${row}-${col}" class="noselect square-black"></div>`
                                    }
                                `)}
                            </div>
                            <div class="clear"></div>
                        `)}
                        <span class="empty"> </span>
                        <letter-element></letter-element>
                    </div>  
                </div>    
                <div id="message-box" class="alert alert-warning" role="alert">
                </div>          
            </div>
 
        `;
    }
}

customElements.define('chess-app', ChessApp);