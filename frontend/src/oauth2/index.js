const KEYCLOAK_URL = "http://localhost:8000";
const REALM = "timepicker";
const CLIENT_ID = "timepicker-app";
export const REDIRECT_URI = "http://localhost:8080/auth"
const LOGOUT_REDIRECT_URI = "http://localhost:8080"

export default {
  async login() {
    window.location.replace(`${KEYCLOAK_URL}/auth/realms/${REALM}/protocol/openid-connect/auth?client_id=${CLIENT_ID}&response_type=code&redirect_uri=${REDIRECT_URI}&scope=openid`)
  },

  async logout() {
    window.location.replace(`${KEYCLOAK_URL}/auth/realms/${REALM}/protocol/openid-connect/logout?redirect_uri=${LOGOUT_REDIRECT_URI}`)
  }
}
