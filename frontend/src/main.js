import Vue from 'vue';
import App from './App.vue';
import {store} from "@/store";
import axios from "@/axios";
import router from "@/router";
import oauth2 from "@/oauth2";
import '@/plugins/bootstrap-vue';

Vue.config.productionTip = false;

Vue.prototype.$store = store;
Vue.prototype.$axios = axios;
Vue.prototype.$oauth2 = oauth2;

router.beforeEach(async (to, from, next) => {
  if (to.path.match("/auth.*") && to.query.code) {
    await store.dispatch('LOGIN', to.query.code)
    return next("/");
  }
  return next();
})

new Vue({
  store,
  router,
  render: h => h(App),
}).$mount('#app');
