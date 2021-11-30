import axios from "@/axios";
import {REDIRECT_URI} from "@/oauth2";

export default {
  authenticate: async (code) => await axios.post('/auth', {code: code, redirectUri: REDIRECT_URI})
    .then(response => response.data)
}
