<template>
    <div>
        <span class="empty"> </span>
        <span v-for="letter in letters" class="letter" :id="'letter' + letter">{{ letter }}</span>
    </div>
</template>

<script>
    export default {
        data: () => ({
            letters: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'],
        })
    };
</script>

<style scoped lang="scss">
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
</style>