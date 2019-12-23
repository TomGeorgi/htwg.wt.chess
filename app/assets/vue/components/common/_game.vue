<template>
    <div id="game-container" class="game-container">
        <div id="game" class="game">
            <div v-for="row in rows" class="c-row" :key="row">
                <span class="number">
                    {{ size - row }}
                </span>
                <div v-for="col in cols" :key="col" :id="'square-' + row + '-' + col" class="noselect">
                    <slot></slot>
                    <div class="clear"></div>
                </div>
            </div>
            <div class="clear"></div>
            <letter-box/>
        </div>
    </div>
</template>

<script>

    Vue.component('letter-box', require('./_letter-box.vue').default);

    export default {
        data: () => ({
            size: 8,
            rows: [0, 1, 2, 3, 4, 5, 6, 7],
            cols: [0, 1, 2, 3, 4, 5, 6, 7],
        }),
        mounted: function () {
            for (let row = 0; row < size; row++) {
                for (let col = 0; col < size; col++) {
                    let square = $('#square-' + row + "-" + col);
                    if ((row + col) % 2 !== 0) {
                        square.addClass('square-white');
                    } else {
                        square.addClass('square-black');
                    }
                    //
                }
            }
        }
    };
</script>

<style scoped lang="scss">

    a:link {
        color: black;
    }

    a:any-link {
        color: black;
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

</style>