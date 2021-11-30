import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from 'vuex-persistedstate';
import login from "@/api/login";
import oauth2 from "@/oauth2";

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    user: {
      username: String,
      name: String,
      lastName: String
    },
    authenticated: false
  },

  getters: {
    user: state => state.user,
    authenticated: state => state.authenticated
  },

  mutations: {
    SET_AUTHENTICATED: (state, authenticated) => {
      state.authenticated = authenticated
    },

    SET_USER: (state, user) => {
      state.user = user;
    },

    CLEAR_USER_DATA: (state) => {
      state.user = null;
    }
  },

  actions: {
    LOGIN: async (ctx, code) => await login.authenticate(code)
      .then(user => {
        ctx.commit('SET_AUTHENTICATED', true);
        ctx.commit('SET_USER', user);
      }).catch(() => {
        ctx.commit('SET_AUTHENTICATED', false);
        ctx.commit('CLEAR_USER_DATA');
      }),

    LOGOUT: async (ctx) => await oauth2.logout()
      .catch(err => console.log(err))
      .then(() => {
        ctx.commit('SET_AUTHENTICATED', false);
        ctx.commit('CLEAR_USER_DATA');
      })
  },

  plugins: [createPersistedState()]
});
