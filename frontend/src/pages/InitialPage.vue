<template>
  <b-card title="Timepicker app">
    <b-card-body>Timepicker is a simple app for companies to choose a date</b-card-body>
    <p v-if="$store.getters.authenticated">Hi {{ $store.getters.user.name }} {{ $store.getters.user.lastName }}!</p>
    <b-button @click="login" v-if="!$store.getters.authenticated">Login</b-button>
    <b-button @click="logout" v-else>Logout</b-button>
  </b-card>
</template>

<script>
export default {
  name: "InitialPage",

  methods: {
    async login() {
      await this.$oauth2.login();
    },

    async logout() {
      await this.$store.dispatch('LOGOUT');
      await this.$oauth2.logout();
    }
  }
}
</script>

<style scoped>

</style>
