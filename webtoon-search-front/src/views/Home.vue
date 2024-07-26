<template>
  <div>
    <div id="naverIdLogin"></div>
  </div>
</template>

<script>
/* global naver */

export default {
  name: "HomeView",
  mounted() {
    this.initializeNaverLogin();
  },
  methods: {
    initializeNaverLogin() {
      const checkNaverSDK = setInterval(() => {
        if (typeof naver !== 'undefined') {
          clearInterval(checkNaverSDK);

          const naverLogin = new naver.LoginWithNaverId({
            clientId: "Oi2P9dITH68ua3Pz2MSe", // 네이버 개발자 센터에서 발급받은 클라이언트 ID
            callbackUrl: "http://localhost:8081/callback", // 네이버 개발자 센터에 등록한 Redirect URI
            isPopup: false,
            loginButton: { color: "green", type: 3, height: 50 }
          });

          naverLogin.init();

          window.addEventListener('load', () => {
            naverLogin.getLoginStatus(status => {
              if (status) {
                console.log(naverLogin.user);
                // 로그인된 사용자 정보
                const userInfo = naverLogin.user;
                console.log(userInfo);
              } else {
                console.log("로그인 실패");
              }
            });
          });
        }
      }, 100);
    }
  }
};
</script>

<style>
#naverIdLogin {
  margin: 20px;
}
</style>
