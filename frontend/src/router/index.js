import Vue from "vue";
import Router from "vue-router";
import InitialPage from "@/pages/InitialPage";
import PageNotFound from "@/pages/PageNotFound";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      name: 'initial',
      component: InitialPage,
      meta: {
        secured: true
      }
    },
    {
      path: '*',
      name: 'pageNotFound',
      component: PageNotFound
    }
  ],
  mode: 'history'
})
