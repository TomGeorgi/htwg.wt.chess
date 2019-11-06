let size = 8;

let gameJson = {
    playerOne: "PlayerOne",
    playerTwo: "PlayerTwo",
    0: {0:"&#9820", 1:"&#9816", 2:"&#9821", 3:"&#9819", 4:"&#9818", 5:"&#9821", 6:"&#9816", 7:"&#9820"},
    1: {0:"&#9823", 1:"&#9823", 2:"&#9823", 3:"&#9823", 4:"&#9823", 5:"&#9823", 6:"&#9823", 7:"&#9823"},
    2: {0:0, 1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0},
    3: {0:0, 1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0},
    4: {0:0, 1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0},
    5: {0:0, 1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0},
    6: {0:"&#9817", 1:"&#9817", 2:"&#9817", 3:"&#9817", 4:"&#9817", 5:"&#9817", 6:"&#9817", 7:"&#9817"},
    7: {0:"&#9814", 1:"&#9822", 2:"&#9815", 3:"&#9813", 4:"&#9812", 5:"&#9815", 6:"&#9822", 7:"&#9814"},
};

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

class Grid {

    constructor(playerOne, playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.cells = [[]];
        this.size = 8
    }

    fill(json) {
        for (let row = 0; row < this.size; row++) {
            let rw = [];
            for (let col = 0; col < this.size; col++) {
                rw[col] = (json[row][col]);
            }
            this.cells[row] = rw;
        }
    }
}


let grid = new Grid(gameJson.playerOne, gameJson.playerTwo);
grid.fill(gameJson);

function fillGrid(grid) {
    for (let row = 0; row < grid.size; row++) {
        for (let col = 0; col < grid.size; col++) {
            if (grid.cells[row][col] !== 0) {
                //console.log(grid.cells[row][col].toString());
                $("#square-" + row + "-" + col).html(grid.cells[row][col].toString());
            }
        }
    }
}

function registerClickListener() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            $("#square-" + row.toString() + "-" + col.toString()).click(function () {
                console.log("Square" + row.toString() + "-" + col.toString() + "clicked")
            });
            /*$("#square-" + row.toString() + "-" + col.toString()).mousemove(function ( event ) {
                console.log("works");
            });
            $(document.getElementById("#square-" + row.toString() + "-" + col.toString())).onmousemove = function () {
                console.log("Square" + row.toString() + "-" + col.toString() + "moved")
            };
            $(document.getElementById("#square-" + row.toString() + "-" + col.toString())).onmouseleave = function () {
                console.log("Square" + row.toString() + "-" + col.toString() + "leaved")
            };*/
        }
    }
}

let oldColor = "";

function registerMouseMoveEvent() {
    for (let row = 0; row < size; row++) {
        for (let col = 0; col < size; col++) {
            let id = "square-" + row.toString() + "-" + col.toString();

            $(document.getElementById(id)).mouseenter(function() {
                if (grid.cells[row][col] !== 0) {
                    let li = document.getElementById(id);
                    oldColor = li.style.backgroundColor.toString();
                    li.style.backgroundColor = "green";
                }
            });
            $(document.getElementById(id)).mouseleave(function() {
                let li = document.getElementById(id);
                li.style.backgroundColor = oldColor;
                oldColor = "";
            });
        }
    }
}

$( document ).ready(function () {
    console.log( "This is a test! ");
    setupBoard();
    fillGrid( grid );
    registerMouseMoveEvent();
    //console.log(grid.cells);
    //registerClickListener();
});