<template>
  <div class="chess-app">
    <game></game>
  </div>
</template>

<script>
  import Vue from 'vue';

  Vue.component('game', require('./common/_game.vue').default);

  export default Vue.extend({});
</script>